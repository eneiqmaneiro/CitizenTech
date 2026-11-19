package br.com.fiap.ecosmart.dto;

import java.time.LocalDate;

public class EstoqueResponseDto {

    private int codigo;

    private int codigoAlimento;

    private double quantidade;

    private LocalDate dataEntrada;

    private LocalDate dataValidade;

    // Calculado: dias até vencer (negativo = já venceu)
    private long diasParaVencer;

    // Calculado: OK, ALERTA ou VENCIDO
    private String status;

    public EstoqueResponseDto(int codigo, int codigoAlimento, double quantidade, LocalDate dataEntrada, LocalDate dataValidade, long diasParaVencer, String status) {
        this.codigo = codigo;
        this.codigoAlimento = codigoAlimento;
        this.quantidade = quantidade;
        this.dataEntrada = dataEntrada;
        this.dataValidade = dataValidade;
        this.diasParaVencer = diasParaVencer;
        this.status = status;
    }

    public EstoqueResponseDto() {}

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

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

    public long getDiasParaVencer() {
        return diasParaVencer;
    }

    public void setDiasParaVencer(long diasParaVencer) {
        this.diasParaVencer = diasParaVencer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
