package br.com.fiap.ecosmart.exception;

// Classe-mãe das exceções "não encontrado": o handler trata todas de uma vez (HTTP 404)
public class RecursoNaoEncontradoException extends Exception {

    public RecursoNaoEncontradoException(String message) {
        super(message);
    }
}
