package br.edu.faculdade.pessoa;

public class Pessoa {
    private String id;
    private String nome;
    private String email;
    private String area;
    private Senioridade senioridade;

    public Pessoa(String id, String nome, String email, String area, Senioridade senioridade) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.area = area;
        this.senioridade = senioridade;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getArea() {
        return area;
    }

    public Senioridade getSenioridade() {
        return senioridade;
    }
}



