package br.com.fiap.ecosmart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public class EstoqueRequestDto {

    @Positive(message = "Informe o código do alimento")
    private int codigoAlimento;

    @Positive(message = "A quantidade deve ser maior que zero")
    private double quantidade;

    // Opcional: se não vier, o sistema usa a data de hoje
    private LocalDate dataEntrada;

    @NotNull(message = "A data de validade é obrigatória")
    private LocalDate dataValidade;

    public EstoqueRequestDto() {}

    public int getCodigoAlimento() {
        return codigoAlimento;
    }

    public void setCodigoAlimento(int codigoAlimento) {
        this.codigoAlimento = codigoAlimento;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }
}
