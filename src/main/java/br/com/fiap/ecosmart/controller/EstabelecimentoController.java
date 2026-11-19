package br.com.fiap.ecosmart.controller;

import br.com.fiap.ecosmart.dao.EstabelecimentoDao;
import br.com.fiap.ecosmart.dto.EstabelecimentoRequestDto;
import br.com.fiap.ecosmart.dto.EstabelecimentoResponseDto;
import br.com.fiap.ecosmart.exception.RecursoNaoEncontradoException;
import br.com.fiap.ecosmart.exception.RegraNegocioException;
import br.com.fiap.ecosmart.model.Estabelecimentos;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("estabelecimentos")
public class EstabelecimentoController {

    private final EstabelecimentoDao dao;

    public EstabelecimentoController(EstabelecimentoDao dao) {
        this.dao = dao;
    }

    //GET
    @GetMapping("/{cd}")
    public ResponseEntity<EstabelecimentoResponseDto> buscar(@PathVariable int cd)
            throws SQLException, RecursoNaoEncontradoException {
        return ResponseEntity.ok(paraResponse(dao.pesquisarPorCd(cd))); //Retorna 200 OK
    }

    @GetMapping
    public List<EstabelecimentoResponseDto> listar() throws SQLException {
        List<EstabelecimentoResponseDto> lista = new ArrayList<>();
        for (Estabelecimentos e : dao.listar())
            lista.add(paraResponse(e));
        return lista;
    }

    //POST
    @PostMapping
    public ResponseEntity<EstabelecimentoResponseDto> cadastrar(@Valid @RequestBody EstabelecimentoRequestDto dto,
                                                                UriComponentsBuilder builder)
            throws SQLException, RegraNegocioException {
        Estabelecimentos e = paraModel(dto);
        e.setDataCadastro(LocalDate.now()); //Preenchida pelo sistema
        dao.cadastrar(e); //Regra de negócio: CNPJ único (dentro do DAO)

        URI uri = builder.path("/estabelecimentos/{cd}").buildAndExpand(e.getCodigo()).toUri();
        return ResponseEntity.created(uri).body(paraResponse(e)); //Retorna 201 Created
    }

    //PUT
    @PutMapping("/{cd}")
    public ResponseEntity<EstabelecimentoResponseDto> atualizar(@PathVariable int cd,
                                                                @Valid @RequestBody EstabelecimentoRequestDto dto)
            throws SQLException, RecursoNaoEncontradoException, RegraNegocioException {
        Estabelecimentos atual = dao.pesquisarPorCd(cd); //404 se não existir
        Estabelecimentos e = paraModel(dto);
        e.setCodigo(cd);
        e.setDataCadastro(atual.getDataCadastro());
        dao.atualizar(e);
        return ResponseEntity.ok(paraResponse(e));
    }

    //DELETE
    @DeleteMapping("/{cd}")
    public ResponseEntity<Void> remover(@PathVariable int cd) throws SQLException, RecursoNaoEncontradoException {
        //Se houver alimentos ou consumos vinculados, o Oracle recusa (ORA-02292) e o handler responde 409
        dao.deletar(cd);
        return ResponseEntity.noContent().build(); //Retorna 204 No Content
    }

    //Conversões DTO <-> Model
    private Estabelecimentos paraModel(EstabelecimentoRequestDto dto) {
        Estabelecimentos e = new Estabelecimentos();
        e.setNome(dto.getNome());
        e.setCnpj(dto.getCnpj());
        e.setTipo(dto.getTipo());
        e.setCidade(dto.getCidade());
        e.setEmail(dto.getEmail());
        return e;
    }

    private EstabelecimentoResponseDto paraResponse(Estabelecimentos e) {
        return new EstabelecimentoResponseDto(e.getCodigo(), e.getNome(), e.getCnpj(), e.getTipo(),
                e.getCidade(), e.getEmail(), e.getDataCadastro());
    }
}
