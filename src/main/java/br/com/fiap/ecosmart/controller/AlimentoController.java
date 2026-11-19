package br.com.fiap.ecosmart.controller;

import br.com.fiap.ecosmart.dao.AlimentoDao;
import br.com.fiap.ecosmart.dao.EstabelecimentoDao;
import br.com.fiap.ecosmart.dto.AlimentoRequestDto;
import br.com.fiap.ecosmart.dto.AlimentoResponseDto;
import br.com.fiap.ecosmart.exception.RecursoNaoEncontradoException;
import br.com.fiap.ecosmart.model.Alimentos;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("alimentos")
public class AlimentoController {

    private final AlimentoDao dao;
    private final EstabelecimentoDao estabelecimentoDao;

    public AlimentoController(AlimentoDao dao, EstabelecimentoDao estabelecimentoDao) {
        this.dao = dao;
        this.estabelecimentoDao = estabelecimentoDao;
    }

    //GET
    @GetMapping("/{cd}")
    public ResponseEntity<AlimentoResponseDto> buscar(@PathVariable int cd)
            throws SQLException, RecursoNaoEncontradoException {
        return ResponseEntity.ok(paraResponse(dao.pesquisarPorCd(cd)));
    }

    //Ex.: GET /alimentos?cdEstabelecimento=1
    @GetMapping
    public List<AlimentoResponseDto> listar(@RequestParam(required = false) Integer cdEstabelecimento)
            throws SQLException {
        List<AlimentoResponseDto> lista = new ArrayList<>();
        for (Alimentos a : dao.listar(cdEstabelecimento))
            lista.add(paraResponse(a));
        return lista;
    }

    //POST
    @PostMapping
    public ResponseEntity<AlimentoResponseDto> cadastrar(@Valid @RequestBody AlimentoRequestDto dto,
                                                         UriComponentsBuilder builder)
            throws SQLException, RecursoNaoEncontradoException {
        estabelecimentoDao.pesquisarPorCd(dto.getCodigoEstabelecimento()); //Regra: o estabelecimento precisa existir (404)
        Alimentos a = paraModel(dto);
        dao.cadastrar(a);

        URI uri = builder.path("/alimentos/{cd}").buildAndExpand(a.getCodigo()).toUri();
        return ResponseEntity.created(uri).body(paraResponse(a));
    }

    //PUT
    @PutMapping("/{cd}")
    public ResponseEntity<AlimentoResponseDto> atualizar(@PathVariable int cd,
                                                         @Valid @RequestBody AlimentoRequestDto dto)
            throws SQLException, RecursoNaoEncontradoException {
        estabelecimentoDao.pesquisarPorCd(dto.getCodigoEstabelecimento());
        Alimentos a = paraModel(dto);
        a.setCodigo(cd);
        dao.atualizar(a);
        return ResponseEntity.ok(paraResponse(a));
    }

    //DELETE
    @DeleteMapping("/{cd}")
    public ResponseEntity<Void> remover(@PathVariable int cd) throws SQLException, RecursoNaoEncontradoException {
        //Se houver estoque ou desperdício vinculado, o handler responde 409 (ORA-02292)
        dao.deletar(cd);
        return ResponseEntity.noContent().build();
    }

    //Conversões DTO <-> Model
    private Alimentos paraModel(AlimentoRequestDto dto) {
        Alimentos a = new Alimentos();
        a.setNome(dto.getNome());
        a.setCategoria(dto.getCategoria());
        a.setUnidadeMedida(dto.getUnidadeMedida().toLowerCase());
        a.setCodigoEstabelecimento(dto.getCodigoEstabelecimento());
        return a;
    }

    private AlimentoResponseDto paraResponse(Alimentos a) {
        return new AlimentoResponseDto(a.getCodigo(), a.getNome(), a.getCategoria(),
                a.getUnidadeMedida(), a.getCodigoEstabelecimento());
    }
}
