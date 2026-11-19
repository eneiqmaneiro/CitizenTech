package br.com.fiap.ecosmart.controller;

import br.com.fiap.ecosmart.dao.AlimentoDao;
import br.com.fiap.ecosmart.dao.DesperdicioDao;
import br.com.fiap.ecosmart.dto.DesperdicioRequestDto;
import br.com.fiap.ecosmart.dto.DesperdicioResponseDto;
import br.com.fiap.ecosmart.exception.RecursoNaoEncontradoException;
import br.com.fiap.ecosmart.exception.RegraNegocioException;
import br.com.fiap.ecosmart.model.Desperdicio;
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
@RequestMapping("desperdicios")
public class DesperdicioController {

    private final DesperdicioDao dao;
    private final AlimentoDao alimentoDao;

    public DesperdicioController(DesperdicioDao dao, AlimentoDao alimentoDao) {
        this.dao = dao;
        this.alimentoDao = alimentoDao;
    }

    //GET
    @GetMapping("/{cd}")
    public ResponseEntity<DesperdicioResponseDto> buscar(@PathVariable int cd)
            throws SQLException, RecursoNaoEncontradoException {
        return ResponseEntity.ok(paraResponse(dao.pesquisarPorCd(cd)));
    }

    //Ex.: GET /desperdicios?cdEstabelecimento=1
    @GetMapping
    public List<DesperdicioResponseDto> listar(@RequestParam(required = false) Integer cdEstabelecimento)
            throws SQLException {
        List<DesperdicioResponseDto> lista = new ArrayList<>();
        for (Desperdicio d : dao.listar(cdEstabelecimento))
            lista.add(paraResponse(d));
        return lista;
    }

    //POST - registra o desperdício E desconta a quantidade do estoque
    @PostMapping
    public ResponseEntity<DesperdicioResponseDto> registrar(@Valid @RequestBody DesperdicioRequestDto dto,
                                                            UriComponentsBuilder builder)
            throws SQLException, RecursoNaoEncontradoException, RegraNegocioException {
        alimentoDao.pesquisarPorCd(dto.getCodigoAlimento()); //o alimento precisa existir (404)
        Desperdicio d = paraModel(dto);
        if (d.getDataRegistro() == null)
            d.setDataRegistro(LocalDate.now());
        dao.cadastrarComBaixaDeEstoque(d); //se não houver estoque suficiente, lança RegraNegocioException (422)

        URI uri = builder.path("/desperdicios/{cd}").buildAndExpand(d.getCodigo()).toUri();
        return ResponseEntity.created(uri).body(paraResponse(d));
    }

    //PUT - só altera data e motivo (a quantidade já baixou o estoque, por isso não muda)
    @PutMapping("/{cd}")
    public ResponseEntity<DesperdicioResponseDto> atualizar(@PathVariable int cd,
                                                            @Valid @RequestBody DesperdicioRequestDto dto)
            throws SQLException, RecursoNaoEncontradoException {
        Desperdicio d = dao.pesquisarPorCd(cd); //404 se não existir
        d.setMotivo(dto.getMotivo());
        if (dto.getDataRegistro() != null)
            d.setDataRegistro(dto.getDataRegistro());
        dao.atualizar(d);
        return ResponseEntity.ok(paraResponse(d));
    }

    //DELETE - remove só o registro; o estoque não volta (o alimento foi realmente perdido)
    @DeleteMapping("/{cd}")
    public ResponseEntity<Void> remover(@PathVariable int cd) throws SQLException, RecursoNaoEncontradoException {
        dao.deletar(cd);
        return ResponseEntity.noContent().build();
    }

    //Conversões DTO <-> Model
    private Desperdicio paraModel(DesperdicioRequestDto dto) {
        Desperdicio d = new Desperdicio();
        d.setCodigoAlimento(dto.getCodigoAlimento());
        d.setQuantidade(dto.getQuantidade());
        d.setDataRegistro(dto.getDataRegistro());
        d.setMotivo(dto.getMotivo());
        return d;
    }

    private DesperdicioResponseDto paraResponse(Desperdicio d) {
        return new DesperdicioResponseDto(d.getCodigo(), d.getCodigoAlimento(), d.getQuantidade(),
                d.getDataRegistro(), d.getMotivo());
    }
}
