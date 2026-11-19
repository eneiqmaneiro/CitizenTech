package br.com.fiap.ecosmart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class AlimentoRequestDto {

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "A categoria é obrigatória")
    @Size(max = 50, message = "A categoria deve ter no máximo 50 caracteres")
    private String categoria;

    @NotBlank(message = "A unidade de medida é obrigatória")
    @Pattern(regexp = "(?i)kg|g|l|ml|un", message = "Unidade inválida. Use: kg, g, L, ml ou un")
    private String unidadeMedida;

    @Positive(message = "Informe o código do estabelecimento")
    private int codigoEstabelecimento;

    public AlimentoRequestDto() {}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public int getCodigoEstabelecimento() {
        return codigoEstabelecimento;
    }

    public void setCodigoEstabelecimento(int codigoEstabelecimento) {
        this.codigoEstabelecimento = codigoEstabelecimento;
    }
}
