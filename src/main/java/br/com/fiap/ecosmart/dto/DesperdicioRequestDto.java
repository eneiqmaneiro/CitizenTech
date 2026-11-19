package br.com.fiap.ecosmart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class DesperdicioRequestDto {

    @Positive(message = "Informe o código do alimento")
    private int codigoAlimento;

    @Positive(message = "A quantidade deve ser maior que zero")
    private double quantidade;

    // Opcional: se não vier, o sistema usa a data de hoje
    @PastOrPresent(message = "A data não pode estar no futuro")
    private LocalDate dataRegistro;

    @NotBlank(message = "O motivo é obrigatório")
    @Size(max = 100, message = "O motivo deve ter no máximo 100 caracteres")
    private String motivo;

    public DesperdicioRequestDto() {}

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
