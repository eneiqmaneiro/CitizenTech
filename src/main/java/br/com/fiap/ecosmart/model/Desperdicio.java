package br.com.fiap.ecosmart.model;

import java.time.LocalDate;

public class Desperdicio {

    //Atributos
    private int codigo;
    private int codigoAlimento;
    private double quantidade;
    private LocalDate dataRegistro;
    private String motivo;

    public Desperdicio(int codigo, int codigoAlimento, double quantidade, LocalDate dataRegistro,
                       String motivo) {
        this.codigo = codigo;
        this.codigoAlimento = codigoAlimento;
        this.quantidade = quantidade;
        this.dataRegistro = dataRegistro;
        this.motivo = motivo;
    }

    public Desperdicio() {}

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

    public LocalDate getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDate dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
