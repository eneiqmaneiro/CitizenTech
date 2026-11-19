package br.com.fiap.ecosmart.dto;

public class AlimentoResponseDto {

    private int codigo;

    private String nome;

    private String categoria;

    private String unidadeMedida;

    private int codigoEstabelecimento;

    public AlimentoResponseDto(int codigo, String nome, String categoria, String unidadeMedida, int codigoEstabelecimento) {
        this.codigo = codigo;
        this.nome = nome;
        this.categoria = categoria;
        this.unidadeMedida = unidadeMedida;
        this.codigoEstabelecimento = codigoEstabelecimento;
    }

    public AlimentoResponseDto() {}

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

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
