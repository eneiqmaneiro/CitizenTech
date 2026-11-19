package br.com.fiap.ecosmart.dao;

import br.com.fiap.ecosmart.exception.EstabelecimentoNaoEncontradoException;
import br.com.fiap.ecosmart.exception.RegraNegocioException;
import br.com.fiap.ecosmart.model.Estabelecimentos;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EstabelecimentoDao {

    private final DataSource dataSource;
    private static final String INSERT_SQL = "insert into t_ecosmart_estabelecimento (cd_estabelecimento, nm_estabelecimento, nr_cnpj, tp_estabelecimento, nm_cidade, ds_email, dt_cadastro) values (sq_t_ecosmart_estabelecimento.nextval,?,?,?,?,?,?)";
    private static final String SELECT_SQL = "select * from t_ecosmart_estabelecimento order by nm_estabelecimento";
    private static final String SELECT_BY_CD_SQL = "select * from t_ecosmart_estabelecimento where cd_estabelecimento = ?";
    private static final String UPDATE_SQL = "update t_ecosmart_estabelecimento set nm_estabelecimento = ?, nr_cnpj = ?, tp_estabelecimento = ?, nm_cidade = ?, ds_email = ? where cd_estabelecimento = ?";
    private static final String DELETE_SQL = "delete from t_ecosmart_estabelecimento where cd_estabelecimento = ?";
    private static final String COUNT_CNPJ_SQL = "select count(*) from t_ecosmart_estabelecimento where nr_cnpj = ? and cd_estabelecimento <> ?";

    public EstabelecimentoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //GetEstabelecimento
    private Estabelecimentos getEstabelecimento(ResultSet rs) throws SQLException {
        return new Estabelecimentos(
                rs.getInt("cd_estabelecimento"),
                rs.getString("nm_estabelecimento"),
                rs.getString("nr_cnpj"),
                rs.getString("tp_estabelecimento"),
                rs.getString("nm_cidade"),
                rs.getString("ds_email"),
                DaoUtil.data(rs, "dt_cadastro"));
    }

    //Cadastrar (Create)
    public void cadastrar(Estabelecimentos e) throws SQLException, RegraNegocioException {
        //Regra de negócio: o CNPJ não pode se repetir
        if (existePorCnpj(e.getCnpj(), 0))
            throw new RegraNegocioException("Já existe um estabelecimento cadastrado com este CNPJ.");
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[]{"cd_estabelecimento"})) {
            stmt.setString(1, e.getNome());
            stmt.setString(2, e.getCnpj());
            stmt.setString(3, e.getTipo());
            stmt.setString(4, e.getCidade());
            stmt.setString(5, e.getEmail());
            stmt.setDate(6, DaoUtil.sql(e.getDataCadastro()));
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next())
                    e.setCodigo(keys.getInt(1));
            }
        }
    }

    //Listar (Read)
    public List<Estabelecimentos> listar() throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL);
             ResultSet rs = stmt.executeQuery()) {
            List<Estabelecimentos> lista = new ArrayList<>();
            while (rs.next())
                lista.add(getEstabelecimento(rs));
            return lista;
        }
    }

    //Listar (Read com código)
    public Estabelecimentos pesquisarPorCd(int cd) throws SQLException, EstabelecimentoNaoEncontradoException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_CD_SQL)) {
            stmt.setInt(1, cd);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next())
                    throw new EstabelecimentoNaoEncontradoException("Estabelecimento não encontrado!");
                return getEstabelecimento(rs);
            }
        }
    }

    //Atualizar (Update)
    public void atualizar(Estabelecimentos e) throws SQLException, EstabelecimentoNaoEncontradoException, RegraNegocioException {
        //Regra de negócio: o CNPJ não pode pertencer a outro estabelecimento
        if (existePorCnpj(e.getCnpj(), e.getCodigo()))
            throw new RegraNegocioException("Já existe outro estabelecimento cadastrado com este CNPJ.");
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, e.getNome());
            stmt.setString(2, e.getCnpj());
            stmt.setString(3, e.getTipo());
            stmt.setString(4, e.getCidade());
            stmt.setString(5, e.getEmail());
            stmt.setInt(6, e.getCodigo());
            int linhas = stmt.executeUpdate();
            if (linhas == 0)
                throw new EstabelecimentoNaoEncontradoException("Estabelecimento não encontrado para atualizar!");
        }
    }

    //Deletar (Delete)
    public void deletar(int cd) throws SQLException, EstabelecimentoNaoEncontradoException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, cd);
            int linhas = stmt.executeUpdate();
            if (linhas == 0)
                throw new EstabelecimentoNaoEncontradoException("Não existe estabelecimento para apagar!");
        }
    }

    //Verifica se já existe outro estabelecimento com o mesmo CNPJ (ignora o próprio código)
    private boolean existePorCnpj(String cnpj, int ignorarCodigo) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(COUNT_CNPJ_SQL)) {
            stmt.setString(1, cnpj);
            stmt.setInt(2, ignorarCodigo);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }
}
