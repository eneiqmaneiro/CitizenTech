package br.com.fiap.ecosmart.dto;

import java.time.LocalDate;

public class ConsumoAguaResponseDto {

    private int codigo;

    private int codigoEstabelecimento;

    private double litros;

    private LocalDate dataRegistro;

    private String setor;

    public ConsumoAguaResponseDto(int codigo, int codigoEstabelecimento, double litros, LocalDate dataRegistro, String setor) {
        this.codigo = codigo;
        this.codigoEstabelecimento = codigoEstabelecimento;
        this.litros = litros;
        this.dataRegistro = dataRegistro;
        this.setor = setor;
    }

    public ConsumoAguaResponseDto() {}

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigoEstabelecimento() {
        return codigoEstabelecimento;
    }

    public void setCodigoEstabelecimento(int codigoEstabelecimento) {
        this.codigoEstabelecimento = codigoEstabelecimento;
    }

    public double getLitros() {
        return litros;
    }

    public void setLitros(double litros) {
        this.litros = litros;
    }

    public LocalDate getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDate dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }
}
