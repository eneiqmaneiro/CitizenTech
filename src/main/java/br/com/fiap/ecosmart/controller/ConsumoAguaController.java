package br.com.fiap.ecosmart.controller;

import br.com.fiap.ecosmart.dao.ConsumoAguaDao;
import br.com.fiap.ecosmart.dao.EstabelecimentoDao;
import br.com.fiap.ecosmart.dto.ConsumoAguaRequestDto;
import br.com.fiap.ecosmart.dto.ConsumoAguaResponseDto;
import br.com.fiap.ecosmart.exception.RecursoNaoEncontradoException;
import br.com.fiap.ecosmart.model.ConsumoAgua;
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
@RequestMapping("consumos-agua")
public class ConsumoAguaController {

    private final ConsumoAguaDao dao;
    private final EstabelecimentoDao estabelecimentoDao;

    public ConsumoAguaController(ConsumoAguaDao dao, EstabelecimentoDao estabelecimentoDao) {
        this.dao = dao;
        this.estabelecimentoDao = estabelecimentoDao;
    }

    //GET
    @GetMapping("/{cd}")
    public ResponseEntity<ConsumoAguaResponseDto> buscar(@PathVariable int cd)
            throws SQLException, RecursoNaoEncontradoException {
        return ResponseEntity.ok(paraResponse(dao.pesquisarPorCd(cd)));
    }

    //Ex.: GET /consumos-agua?cdEstabelecimento=1
    @GetMapping
    public List<ConsumoAguaResponseDto> listar(@RequestParam(required = false) Integer cdEstabelecimento)
            throws SQLException {
        List<ConsumoAguaResponseDto> lista = new ArrayList<>();
        for (ConsumoAgua c : dao.listar(cdEstabelecimento))
            lista.add(paraResponse(c));
        return lista;
    }

    //POST
    @PostMapping
    public ResponseEntity<ConsumoAguaResponseDto> cadastrar(@Valid @RequestBody ConsumoAguaRequestDto dto,
                                                            UriComponentsBuilder builder)
            throws SQLException, RecursoNaoEncontradoException {
        estabelecimentoDao.pesquisarPorCd(dto.getCodigoEstabelecimento()); //o estabelecimento precisa existir (404)
        ConsumoAgua c = paraModel(dto);
        if (c.getDataRegistro() == null)
            c.setDataRegistro(LocalDate.now());
        dao.cadastrar(c);

        URI uri = builder.path("/consumos-agua/{cd}").buildAndExpand(c.getCodigo()).toUri();
        return ResponseEntity.created(uri).body(paraResponse(c));
    }

    //PUT
    @PutMapping("/{cd}")
    public ResponseEntity<ConsumoAguaResponseDto> atualizar(@PathVariable int cd,
                                                            @Valid @RequestBody ConsumoAguaRequestDto dto)
            throws SQLException, RecursoNaoEncontradoException {
        ConsumoAgua atual = dao.pesquisarPorCd(cd); //404 se não existir
        estabelecimentoDao.pesquisarPorCd(dto.getCodigoEstabelecimento());
        ConsumoAgua c = paraModel(dto);
        c.setCodigo(cd);
        if (c.getDataRegistro() == null)
            c.setDataRegistro(atual.getDataRegistro());
        dao.atualizar(c);
        return ResponseEntity.ok(paraResponse(c));
    }

    //DELETE
    @DeleteMapping("/{cd}")
    public ResponseEntity<Void> remover(@PathVariable int cd) throws SQLException, RecursoNaoEncontradoException {
        dao.deletar(cd);
        return ResponseEntity.noContent().build();
    }

    //Conversões DTO <-> Model
    private ConsumoAgua paraModel(ConsumoAguaRequestDto dto) {
        ConsumoAgua c = new ConsumoAgua();
        c.setCodigoEstabelecimento(dto.getCodigoEstabelecimento());
        c.setLitros(dto.getLitros());
        c.setDataRegistro(dto.getDataRegistro());
        c.setSetor(dto.getSetor());
        return c;
    }

    private ConsumoAguaResponseDto paraResponse(ConsumoAgua c) {
        return new ConsumoAguaResponseDto(c.getCodigo(), c.getCodigoEstabelecimento(), c.getLitros(),
                c.getDataRegistro(), c.getSetor());
    }
}
