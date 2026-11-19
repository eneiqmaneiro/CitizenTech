package br.com.fiap.ecosmart.model;

import java.time.LocalDate;

public class Estabelecimentos {

    //Atributos
    private int codigo;
    private String nome;
    private String cnpj;
    private String tipo;
    private String cidade;
    private String email;
    private LocalDate dataCadastro;

    public Estabelecimentos(int codigo, String nome, String cnpj, String tipo, String cidade,
                            String email, LocalDate dataCadastro) {
        this.codigo = codigo;
        this.nome = nome;
        this.cnpj = cnpj;
        this.tipo = tipo;
        this.cidade = cidade;
        this.email = email;
        this.dataCadastro = dataCadastro;
    }



    public Estabelecimentos() {}

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

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
