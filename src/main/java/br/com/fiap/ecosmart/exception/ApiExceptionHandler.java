package br.com.fiap.ecosmart.exception;

import br.com.fiap.ecosmart.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class ApiExceptionHandler {

    //404 - qualquer "não encontrado" (Estabelecimento, Alimento, Estoque, Desperdício...)
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponseDto> naoEncontrado(RecursoNaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseDto("Erro 404", e.getMessage()));
    }

    //422 - regra de negócio
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErrorResponseDto> regraNegocio(RegraNegocioException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                new ErrorResponseDto("Regra de negócio violada", e.getMessage()));
    }

    //Erros do banco Oracle
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponseDto> erroBanco(SQLException e) {
        switch (e.getErrorCode()) {
            case 2292: // ORA-02292: existem registros filhos
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(
                        "Erro 409", "Não é possível excluir: existem registros vinculados a este item."));
            case 1:    // ORA-00001: violação de unicidade
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(
                        "Erro 409", "Já existe um registro com esses dados (CNPJ ou e-mail duplicado)."));
            case 2291: // ORA-02291: chave estrangeira inexistente
                return ResponseEntity.badRequest().body(new ErrorResponseDto(
                        "Erro 400", "O registro pai informado não existe."));
            default:
                return ResponseEntity.internalServerError().body(new ErrorResponseDto(
                        "Procure o administrador do sistema", "Erro ao acessar o banco de dados."));
        }
    }

    //400 - validações (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacoes(MethodArgumentNotValidException e) {
        Map<String, String> erros = new HashMap<>();
        //Percorrer todos os erros de validação
        for (FieldError field : e.getFieldErrors()){
            erros.put(field.getField(), field.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(erros);
    }

    //400 - JSON malformado ou data em formato inválido no corpo
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> jsonInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(
                "Erro 400", "Corpo da requisição inválido. Verifique o JSON e o formato das datas (AAAA-MM-DD)."));
    }

    //400 - parâmetro de URL com tipo errado (ex.: data inválida em ?inicio=)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> parametroInvalido(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(
                "Erro 400", "Parâmetro '" + e.getName() + "' com valor inválido."));
    }
}
