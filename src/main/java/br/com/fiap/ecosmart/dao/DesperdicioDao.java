package br.com.fiap.ecosmart.dao;

import br.com.fiap.ecosmart.exception.DesperdicioNaoEncontradoException;
import br.com.fiap.ecosmart.exception.RegraNegocioException;
import br.com.fiap.ecosmart.model.Desperdicio;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DesperdicioDao {

    private final DataSource dataSource;
    private static final String INSERT_SQL = "insert into t_ecosmart_desperdicio (cd_desperdicio, cd_alimento, nr_quantidade, dt_registro, ds_motivo) values (sq_t_ecosmart_desperdicio.nextval,?,?,?,?)";
    private static final String SELECT_SQL = "select * from t_ecosmart_desperdicio order by dt_registro desc, cd_desperdicio desc";
    private static final String SELECT_BY_ESTAB_SQL = "select d.* from t_ecosmart_desperdicio d join t_ecosmart_alimento a on a.cd_alimento = d.cd_alimento where a.cd_estabelecimento = ? order by d.dt_registro desc, d.cd_desperdicio desc";
    private static final String SELECT_BY_CD_SQL = "select * from t_ecosmart_desperdicio where cd_desperdicio = ?";
    private static final String UPDATE_SQL = "update t_ecosmart_desperdicio set dt_registro = ?, ds_motivo = ? where cd_desperdicio = ?";
    private static final String DELETE_SQL = "delete from t_ecosmart_desperdicio where cd_desperdicio = ?";
    // Lotes do alimento com saldo, do que vence primeiro para o que vence depois (PEPS). FOR UPDATE trava as linhas durante a transação
    private static final String LOTES_SQL = "select cd_estoque, nr_quantidade from t_ecosmart_estoque where cd_alimento = ? and nr_quantidade > 0 order by dt_validade for update";
    private static final String BAIXA_ESTOQUE_SQL = "update t_ecosmart_estoque set nr_quantidade = ? where cd_estoque = ?";

    public DesperdicioDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //GetDesperdicio
    private Desperdicio getDesperdicio(ResultSet rs) throws SQLException {
        return new Desperdicio(
                rs.getInt("cd_desperdicio"),
                rs.getInt("cd_alimento"),
                rs.getDouble("nr_quantidade"),
                DaoUtil.data(rs, "dt_registro"),
                rs.getString("ds_motivo"));
    }

    //Cadastrar (Create) + baixa no estoque, tudo na MESMA transação:
    //ou grava o desperdício e desconta o estoque, ou não faz nada (rollback)
    public void cadastrarComBaixaDeEstoque(Desperdicio d) throws SQLException, RegraNegocioException {
        try (Connection conexao = dataSource.getConnection()) {
            conexao.setAutoCommit(false);
            try {
                baixarEstoque(conexao, d.getCodigoAlimento(), d.getQuantidade());
                inserir(conexao, d);
                conexao.commit();
            } catch (SQLException | RegraNegocioException | RuntimeException e) {
                conexao.rollback();
                throw e;
            } finally {
                conexao.setAutoCommit(true);
            }
        }
    }

    //Desconta a quantidade dos lotes (primeiro os que vencem antes). Se não houver saldo suficiente, lança exceção
    private void baixarEstoque(Connection conexao, int cdAlimento, double quantidade)
            throws SQLException, RegraNegocioException {
        List<Integer> codigos = new ArrayList<>();
        List<Double> saldos = new ArrayList<>();
        try (PreparedStatement stmt = conexao.prepareStatement(LOTES_SQL)) {
            stmt.setInt(1, cdAlimento);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    codigos.add(rs.getInt("cd_estoque"));
                    saldos.add(rs.getDouble("nr_quantidade"));
                }
            }
        }
        double restante = quantidade;
        try (PreparedStatement upd = conexao.prepareStatement(BAIXA_ESTOQUE_SQL)) {
            for (int i = 0; i < codigos.size() && restante > 0.00001; i++) {
                double baixa = Math.min(saldos.get(i), restante);
                upd.setDouble(1, saldos.get(i) - baixa);
                upd.setInt(2, codigos.get(i));
                upd.executeUpdate();
                restante -= baixa;
            }
        }
        if (restante > 0.00001)
            throw new RegraNegocioException("Quantidade de desperdício maior que o estoque disponível deste alimento!");
    }

    private void inserir(Connection conexao, Desperdicio d) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[]{"cd_desperdicio"})) {
            stmt.setInt(1, d.getCodigoAlimento());
            stmt.setDouble(2, d.getQuantidade());
            stmt.setDate(3, DaoUtil.sql(d.getDataRegistro()));
            stmt.setString(4, d.getMotivo());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next())
                    d.setCodigo(keys.getInt(1));
            }
        }
    }

    //Listar (Read) - se cdEstabelecimento for null, lista todos
    public List<Desperdicio> listar(Integer cdEstabelecimento) throws SQLException {
        String sql = cdEstabelecimento == null ? SELECT_SQL : SELECT_BY_ESTAB_SQL;
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            if (cdEstabelecimento != null)
                stmt.setInt(1, cdEstabelecimento);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Desperdicio> lista = new ArrayList<>();
                while (rs.next())
                    lista.add(getDesperdicio(rs));
                return lista;
            }
        }
    }

    //Listar (Read com código)
    public Desperdicio pesquisarPorCd(int cd) throws SQLException, DesperdicioNaoEncontradoException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_CD_SQL)) {
            stmt.setInt(1, cd);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next())
                    throw new DesperdicioNaoEncontradoException("Registro de desperdício não encontrado!");
                return getDesperdicio(rs);
            }
        }
    }

    //Atualizar (Update) - só data e motivo (a quantidade já baixou o estoque, então não é editável)
    public void atualizar(Desperdicio d) throws SQLException, DesperdicioNaoEncontradoException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setDate(1, DaoUtil.sql(d.getDataRegistro()));
            stmt.setString(2, d.getMotivo());
            stmt.setInt(3, d.getCodigo());
            int linhas = stmt.executeUpdate();
            if (linhas == 0)
                throw new DesperdicioNaoEncontradoException("Registro de desperdício não encontrado para atualizar!");
        }
    }

    //Deletar (Delete)
    public void deletar(int cd) throws SQLException, DesperdicioNaoEncontradoException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, cd);
            int linhas = stmt.executeUpdate();
            if (linhas == 0)
                throw new DesperdicioNaoEncontradoException("Não existe registro de desperdício para apagar!");
        }
    }
}
