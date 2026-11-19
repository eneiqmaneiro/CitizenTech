package br.com.fiap.ecosmart.dao;

import br.com.fiap.ecosmart.exception.ConsumoAguaNaoEncontradoException;
import br.com.fiap.ecosmart.model.ConsumoAgua;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ConsumoAguaDao {

    private final DataSource dataSource;
    private static final String INSERT_SQL = "insert into t_ecosmart_consumo_agua (cd_consumo_agua, cd_estabelecimento, nr_litros, dt_registro, nm_setor) values (sq_t_ecosmart_consumo_agua.nextval,?,?,?,?)";
    private static final String SELECT_SQL = "select * from t_ecosmart_consumo_agua order by dt_registro desc, cd_consumo_agua desc";
    private static final String SELECT_BY_ESTAB_SQL = "select * from t_ecosmart_consumo_agua where cd_estabelecimento = ? order by dt_registro desc, cd_consumo_agua desc";
    private static final String SELECT_BY_CD_SQL = "select * from t_ecosmart_consumo_agua where cd_consumo_agua = ?";
    private static final String UPDATE_SQL = "update t_ecosmart_consumo_agua set cd_estabelecimento = ?, nr_litros = ?, dt_registro = ?, nm_setor = ? where cd_consumo_agua = ?";
    private static final String DELETE_SQL = "delete from t_ecosmart_consumo_agua where cd_consumo_agua = ?";

    public ConsumoAguaDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //GetConsumoAgua
    private ConsumoAgua getConsumo(ResultSet rs) throws SQLException {
        return new ConsumoAgua(
                rs.getInt("cd_consumo_agua"),
                rs.getInt("cd_estabelecimento"),
                rs.getDouble("nr_litros"),
                DaoUtil.data(rs, "dt_registro"),
                rs.getString("nm_setor"));
    }

    //Cadastrar (Create)
    public void cadastrar(ConsumoAgua c) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[]{"cd_consumo_agua"})) {
            stmt.setInt(1, c.getCodigoEstabelecimento());
            stmt.setDouble(2, c.getLitros());
            stmt.setDate(3, DaoUtil.sql(c.getDataRegistro()));
            stmt.setString(4, c.getSetor());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next())
                    c.setCodigo(keys.getInt(1));
            }
        }
    }

    //Listar (Read) - se cdEstabelecimento for null, lista todos
    public List<ConsumoAgua> listar(Integer cdEstabelecimento) throws SQLException {
        String sql = cdEstabelecimento == null ? SELECT_SQL : SELECT_BY_ESTAB_SQL;
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            if (cdEstabelecimento != null)
                stmt.setInt(1, cdEstabelecimento);
            try (ResultSet rs = stmt.executeQuery()) {
                List<ConsumoAgua> lista = new ArrayList<>();
                while (rs.next())
                    lista.add(getConsumo(rs));
                return lista;
            }
        }
    }

    //Listar (Read com código)
    public ConsumoAgua pesquisarPorCd(int cd) throws SQLException, ConsumoAguaNaoEncontradoException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_CD_SQL)) {
            stmt.setInt(1, cd);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next())
                    throw new ConsumoAguaNaoEncontradoException("Registro de consumo de água não encontrado!");
                return getConsumo(rs);
            }
        }
    }

    //Atualizar (Update)
    public void atualizar(ConsumoAgua c) throws SQLException, ConsumoAguaNaoEncontradoException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setInt(1, c.getCodigoEstabelecimento());
            stmt.setDouble(2, c.getLitros());
            stmt.setDate(3, DaoUtil.sql(c.getDataRegistro()));
            stmt.setString(4, c.getSetor());
            stmt.setInt(5, c.getCodigo());
            int linhas = stmt.executeUpdate();
            if (linhas == 0)
                throw new ConsumoAguaNaoEncontradoException("Registro de consumo de água não encontrado para atualizar!");
        }
    }

    //Deletar (Delete)
    public void deletar(int cd) throws SQLException, ConsumoAguaNaoEncontradoException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, cd);
            int linhas = stmt.executeUpdate();
            if (linhas == 0)
                throw new ConsumoAguaNaoEncontradoException("Não existe registro de consumo de água para apagar!");
        }
    }
}
