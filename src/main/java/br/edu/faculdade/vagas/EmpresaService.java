package br.edu.faculdade.vagas;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmpresaService {

    private final EmpresaRepository repositorio;

    public EmpresaService(EmpresaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Empresa> listar() {
        return repositorio.todas();
    }

    public Optional<Empresa> buscarPorSlug(String slug) {
        return repositorio.porSlug(slug);
    }

    public Empresa criar(Empresa nova) {
        Empresa comId = new Empresa(
            UUID.randomUUID().toString(),
            nova.nome(), nova.slug(), nova.site(), nova.descricao());
        return repositorio.salvar(comId);
    }

    public boolean apagar(String slug) {
        return repositorio.remover(slug);
    }
}