package br.com.fiap.ecosmart.dao;

import br.com.fiap.ecosmart.exception.AlimentoNaoEncontradoException;
import br.com.fiap.ecosmart.model.Alimentos;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AlimentoDao {

    private final DataSource dataSource;
    private static final String INSERT_SQL = "insert into t_ecosmart_alimento (cd_alimento, nm_alimento, ds_categoria, sg_unidade_medida, cd_estabelecimento) values (sq_t_ecosmart_alimento.nextval,?,?,?,?)";
    private static final String SELECT_SQL = "select * from t_ecosmart_alimento order by nm_alimento";
    private static final String SELECT_BY_ESTAB_SQL = "select * from t_ecosmart_alimento where cd_estabelecimento = ? order by nm_alimento";
    private static final String SELECT_BY_CD_SQL = "select * from t_ecosmart_alimento where cd_alimento = ?";
    private static final String UPDATE_SQL = "update t_ecosmart_alimento set nm_alimento = ?, ds_categoria = ?, sg_unidade_medida = ?, cd_estabelecimento = ? where cd_alimento = ?";
    private static final String DELETE_SQL = "delete from t_ecosmart_alimento where cd_alimento = ?";

    public AlimentoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //GetAlimento
    private Alimentos getAlimento(ResultSet rs) throws SQLException {
        return new Alimentos(
                rs.getInt("cd_alimento"),
                rs.getString("nm_alimento"),
                rs.getString("ds_categoria"),
                rs.getString("sg_unidade_medida"),
                rs.getInt("cd_estabelecimento"));
    }

    //Cadastrar (Create)
    public void cadastrar(Alimentos a) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[]{"cd_alimento"})) {
            stmt.setString(1, a.getNome());
            stmt.setString(2, a.getCategoria());
            stmt.setString(3, a.getUnidadeMedida());
            stmt.setInt(4, a.getCodigoEstabelecimento());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next())
                    a.setCodigo(keys.getInt(1));
            }
        }
    }

    //Listar (Read) - se cdEstabelecimento for null, lista todos
    public List<Alimentos> listar(Integer cdEstabelecimento) throws SQLException {
        String sql = cdEstabelecimento == null ? SELECT_SQL : SELECT_BY_ESTAB_SQL;
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            if (cdEstabelecimento != null)
                stmt.setInt(1, cdEstabelecimento);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Alimentos> lista = new ArrayList<>();
                while (rs.next())
                    lista.add(getAlimento(rs));
                return lista;
            }
        }
    }

    //Listar (Read com código)
    public Alimentos pesquisarPorCd(int cd) throws SQLException, AlimentoNaoEncontradoException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_CD_SQL)) {
            stmt.setInt(1, cd);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next())
                    throw new AlimentoNaoEncontradoException("Alimento não encontrado!");
                return getAlimento(rs);
            }
        }
    }

    //Atualizar (Update)
    public void atualizar(Alimentos a) throws SQLException, AlimentoNaoEncontradoException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, a.getNome());
            stmt.setString(2, a.getCategoria());
            stmt.setString(3, a.getUnidadeMedida());
            stmt.setInt(4, a.getCodigoEstabelecimento());
            stmt.setInt(5, a.getCodigo());
            int linhas = stmt.executeUpdate();
            if (linhas == 0)
                throw new AlimentoNaoEncontradoException("Alimento não encontrado para atualizar!");
        }
    }

    //Deletar (Delete)
    public void deletar(int cd) throws SQLException, AlimentoNaoEncontradoException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, cd);
            int linhas = stmt.executeUpdate();
            if (linhas == 0)
                throw new AlimentoNaoEncontradoException("Não existe alimento para apagar!");
        }
    }
}
