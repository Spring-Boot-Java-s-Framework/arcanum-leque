package br.edu.faculdade.pessoa;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class PessoaRepository {
    private final List<Pessoa> pessoas = new ArrayList<>(List.of(
            new Pessoa("1", "Andre", "andre@neckel.tech", "Backend", Senioridade.SENIOR),
            new Pessoa("2", "Rosane", "rosane@neckel.tech", "Dados", Senioridade.ESTAGIARIO),
            new Pessoa("3", "Ana", "ana@hotmail.com", "Frontend", Senioridade.PLENO),
            new Pessoa("4", "Rodrigo", "rodrigo@hotmail.com", "Backend", Senioridade.JUNIOR),
            new Pessoa("5", "Ricardo", "ricardo@hotmail.com", "Backend", Senioridade.SENIOR),
            new Pessoa("6", "Joana", "joana@hotmail.com", "Backend", Senioridade.ESTAGIARIO)));

    public List<Pessoa> todasPessoas(){
        return pessoas;
    }

    public Optional<Pessoa> buscaPessoaPorId(String id) {
        return pessoas.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public Optional<Pessoa> atualizarPessoa(Pessoa pessoa, String id) {
        for (int i = 0; i < pessoas.size(); i++) {
            if (pessoas.get(i).getId().equals(id)) {
                pessoas.set(i, pessoa);
                return Optional.of(pessoa);
            }
        }
        return Optional.empty();
    }

    public Pessoa criarPessoa(Pessoa pessoa) {
        System.out.println("Criando pessoa: " + pessoa.getNome());
        pessoas.add(pessoa);
        return pessoa;
    }

    public boolean removePessoa(String id) {
        return pessoas.removeIf(p -> p.getId().equals(id));
    }
}
