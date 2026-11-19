package br.com.fiap.ecosmart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class ConsumoAguaRequestDto {

    @Positive(message = "Informe o código do estabelecimento")
    private int codigoEstabelecimento;

    @Positive(message = "Os litros devem ser maiores que zero")
    private double litros;

    // Opcional: se não vier, o sistema usa a data de hoje
    @PastOrPresent(message = "A data não pode estar no futuro")
    private LocalDate dataRegistro;

    @NotBlank(message = "O setor é obrigatório (cozinha, limpeza, banheiros...)")
    @Size(max = 50, message = "O setor deve ter no máximo 50 caracteres")
    private String setor;

    public ConsumoAguaRequestDto() {}

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
