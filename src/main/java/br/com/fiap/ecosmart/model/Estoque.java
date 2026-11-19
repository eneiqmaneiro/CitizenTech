package br.com.fiap.ecosmart.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Estoque {

    //Lote que vence em até 3 dias entra em ALERTA
    public static final int DIAS_ALERTA = 3;

    //Atributos
    private int codigo;
    private int codigoAlimento;
    private double quantidade;
    private LocalDate dataEntrada;
    private LocalDate dataValidade;

    public Estoque(int codigo, int codigoAlimento, double quantidade, LocalDate dataEntrada,
                   LocalDate dataValidade) {
        this.codigo = codigo;
        this.codigoAlimento = codigoAlimento;
        this.quantidade = quantidade;
        this.dataEntrada = dataEntrada;
        this.dataValidade = dataValidade;
    }

    public Estoque() {}

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

    //Regras de negócio da validade (não são gravadas no banco, assim nunca ficam desatualizadas)
    //Dias até vencer (negativo = já venceu)
    public long calcularDiasParaVencer() {
        return ChronoUnit.DAYS.between(LocalDate.now(), dataValidade);
    }

    //OK, ALERTA (vence em até 3 dias) ou VENCIDO
    public String calcularStatus() {
        long dias = calcularDiasParaVencer();
        if (dias < 0)
            return "VENCIDO";
        if (dias <= DIAS_ALERTA)
            return "ALERTA";
        return "OK";
    }
}
