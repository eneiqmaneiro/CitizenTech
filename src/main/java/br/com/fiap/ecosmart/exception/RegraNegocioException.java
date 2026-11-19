package br.com.fiap.ecosmart.exception;

// Violação de regra de negócio (HTTP 422)
public class RegraNegocioException extends Exception {

    public RegraNegocioException(String message) {
        super(message);
    }
}
