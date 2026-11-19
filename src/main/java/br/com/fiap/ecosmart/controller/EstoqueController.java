package br.com.fiap.ecosmart.controller;

import br.com.fiap.ecosmart.dao.AlimentoDao;
import br.com.fiap.ecosmart.dao.EstoqueDao;
import br.com.fiap.ecosmart.dto.EstoqueRequestDto;
import br.com.fiap.ecosmart.dto.EstoqueResponseDto;
import br.com.fiap.ecosmart.exception.RecursoNaoEncontradoException;
import br.com.fiap.ecosmart.exception.RegraNegocioException;
import br.com.fiap.ecosmart.model.Estoque;
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
@RequestMapping("estoques")
public class EstoqueController {

    private final EstoqueDao dao;
    private final AlimentoDao alimentoDao;

    public EstoqueController(EstoqueDao dao, AlimentoDao alimentoDao) {
        this.dao = dao;
        this.alimentoDao = alimentoDao;
    }

    //GET
    @GetMapping("/{cd}")
    public ResponseEntity<EstoqueResponseDto> buscar(@PathVariable int cd)
            throws SQLException, RecursoNaoEncontradoException {
        return ResponseEntity.ok(paraResponse(dao.pesquisarPorCd(cd)));
    }

    //Ex.: GET /estoques?cdEstabelecimento=1 (ordenado pela validade)
    @GetMapping
    public List<EstoqueResponseDto> listar(@RequestParam(required = false) Integer cdEstabelecimento)
            throws SQLException {
        List<EstoqueResponseDto> lista = new ArrayList<>();
        for (Estoque e : dao.listar(cdEstabelecimento))
            lista.add(paraResponse(e));
        return lista;
    }

    //POST
    @PostMapping
    public ResponseEntity<EstoqueResponseDto> cadastrar(@Valid @RequestBody EstoqueRequestDto dto,
                                                        UriComponentsBuilder builder)
            throws SQLException, RecursoNaoEncontradoException, RegraNegocioException {
        Estoque e = paraModel(dto);
        validar(e);
        dao.cadastrar(e);

        URI uri = builder.path("/estoques/{cd}").buildAndExpand(e.getCodigo()).toUri();
        return ResponseEntity.created(uri).body(paraResponse(e));
    }

    //PUT
    @PutMapping("/{cd}")
    public ResponseEntity<EstoqueResponseDto> atualizar(@PathVariable int cd,
                                                        @Valid @RequestBody EstoqueRequestDto dto)
            throws SQLException, RecursoNaoEncontradoException, RegraNegocioException {
        Estoque atual = dao.pesquisarPorCd(cd); //404 se não existir
        Estoque e = paraModel(dto);
        e.setCodigo(cd);
        if (e.getDataEntrada() == null)
            e.setDataEntrada(atual.getDataEntrada());
        validar(e);
        dao.atualizar(e);
        return ResponseEntity.ok(paraResponse(e));
    }

    //DELETE
    @DeleteMapping("/{cd}")
    public ResponseEntity<Void> remover(@PathVariable int cd) throws SQLException, RecursoNaoEncontradoException {
        dao.deletar(cd);
        return ResponseEntity.noContent().build();
    }

    //Regras de negócio do lote
    private void validar(Estoque e) throws SQLException, RecursoNaoEncontradoException, RegraNegocioException {
        alimentoDao.pesquisarPorCd(e.getCodigoAlimento()); //o alimento precisa existir (404)
        if (e.getDataEntrada() == null)
            e.setDataEntrada(LocalDate.now());
        if (e.getDataValidade().isBefore(e.getDataEntrada())) //validade não pode ser anterior à entrada (422)
            throw new RegraNegocioException("A data de validade não pode ser anterior à data de entrada.");
    }

    //Conversões DTO <-> Model
    private Estoque paraModel(EstoqueRequestDto dto) {
        Estoque e = new Estoque();
        e.setCodigoAlimento(dto.getCodigoAlimento());
        e.setQuantidade(dto.getQuantidade());
        e.setDataEntrada(dto.getDataEntrada());
        e.setDataValidade(dto.getDataValidade());
        return e;
    }

    //Aqui entram os campos calculados pelo model: dias para vencer e status (OK / ALERTA / VENCIDO)
    private EstoqueResponseDto paraResponse(Estoque e) {
        return new EstoqueResponseDto(e.getCodigo(), e.getCodigoAlimento(), e.getQuantidade(),
                e.getDataEntrada(), e.getDataValidade(),
                e.calcularDiasParaVencer(), e.calcularStatus());
    }
}
