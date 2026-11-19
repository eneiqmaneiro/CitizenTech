package br.com.fiap.ecosmart.dao;

import br.com.fiap.ecosmart.exception.EstoqueNaoEncontradoException;
import br.com.fiap.ecosmart.model.Estoque;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EstoqueDao {

    private final DataSource dataSource;
    private static final String INSERT_SQL = "insert into t_ecosmart_estoque (cd_estoque, cd_alimento, nr_quantidade, dt_entrada, dt_validade) values (sq_t_ecosmart_estoque.nextval,?,?,?,?)";
    private static final String SELECT_SQL = "select * from t_ecosmart_estoque order by dt_validade";
    private static final String SELECT_BY_ESTAB_SQL = "select e.* from t_ecosmart_estoque e join t_ecosmart_alimento a on a.cd_alimento = e.cd_alimento where a.cd_estabelecimento = ? order by e.dt_validade";
    private static final String SELECT_BY_CD_SQL = "select * from t_ecosmart_estoque where cd_estoque = ?";
    private static final String UPDATE_SQL = "update t_ecosmart_estoque set cd_alimento = ?, nr_quantidade = ?, dt_entrada = ?, dt_validade = ? where cd_estoque = ?";
    private static final String DELETE_SQL = "delete from t_ecosmart_estoque where cd_estoque = ?";

    public EstoqueDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //GetEstoque
    private Estoque getEstoque(ResultSet rs) throws SQLException {
        return new Estoque(
                rs.getInt("cd_estoque"),
                rs.getInt("cd_alimento"),
                rs.getDouble("nr_quantidade"),
                DaoUtil.data(rs, "dt_entrada"),
                DaoUtil.data(rs, "dt_validade"));
    }

    //Cadastrar (Create)
    public void cadastrar(Estoque e) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[]{"cd_estoque"})) {
            stmt.setInt(1, e.getCodigoAlimento());
            stmt.setDouble(2, e.getQuantidade());
            stmt.setDate(3, DaoUtil.sql(e.getDataEntrada()));
            stmt.setDate(4, DaoUtil.sql(e.getDataValidade()));
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next())
                    e.setCodigo(keys.getInt(1));
            }
        }
    }

    //Listar (Read) - se cdEstabelecimento for null, lista todos
    public List<Estoque> listar(Integer cdEstabelecimento) throws SQLException {
        String sql = cdEstabelecimento == null ? SELECT_SQL : SELECT_BY_ESTAB_SQL;
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            if (cdEstabelecimento != null)
                stmt.setInt(1, cdEstabelecimento);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Estoque> lista = new ArrayList<>();
                while (rs.next())
                    lista.add(getEstoque(rs));
                return lista;
            }
        }
    }

    //Listar (Read com código)
    public Estoque pesquisarPorCd(int cd) throws SQLException, EstoqueNaoEncontradoException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_CD_SQL)) {
            stmt.setInt(1, cd);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next())
                    throw new EstoqueNaoEncontradoException("Lote de estoque não encontrado!");
                return getEstoque(rs);
            }
        }
    }

    //Atualizar (Update)
    public void atualizar(Estoque e) throws SQLException, EstoqueNaoEncontradoException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setInt(1, e.getCodigoAlimento());
            stmt.setDouble(2, e.getQuantidade());
            stmt.setDate(3, DaoUtil.sql(e.getDataEntrada()));
            stmt.setDate(4, DaoUtil.sql(e.getDataValidade()));
            stmt.setInt(5, e.getCodigo());
            int linhas = stmt.executeUpdate();
            if (linhas == 0)
                throw new EstoqueNaoEncontradoException("Lote de estoque não encontrado para atualizar!");
        }
    }

    //Deletar (Delete)
    public void deletar(int cd) throws SQLException, EstoqueNaoEncontradoException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, cd);
            int linhas = stmt.executeUpdate();
            if (linhas == 0)
                throw new EstoqueNaoEncontradoException("Não existe lote de estoque para apagar!");
        }
    }
}
