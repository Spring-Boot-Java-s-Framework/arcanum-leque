package br.edu.faculdade.statistics;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.faculdade.job.Vaga;
import br.edu.faculdade.job.VagaRepository;
import br.edu.faculdade.job.VagaResposta;

@Service
public class BuscaService {

    private final VagaRepository vagaRepository;

    public BuscaService(VagaRepository vagaRepository) {
        this.vagaRepository = vagaRepository;
    }

    // se o filtro vier null, ele é ignorado (deixa todas as vagas passarem)
    public List<VagaResposta> buscar(String area, String senioridade, Boolean aceitaIniciante) {
        return vagaRepository.todas().stream()
                .filter(vaga -> area == null || vaga.area().equalsIgnoreCase(area))
                .filter(vaga -> senioridade == null || vaga.senioridade().equalsIgnoreCase(senioridade))
                .filter(vaga -> aceitaIniciante == null || vaga.aceitaIniciante() == aceitaIniciante)
                .map(this::paraResposta)
                .toList();
    }

    // mesmo formato do GET /vagas da frente 1 (sem descricao)
    private VagaResposta paraResposta(Vaga v) {
        return new VagaResposta(v.id(), v.titulo(), v.area(),
                v.senioridade(), v.local(), v.aceitaIniciante(), v.empresaSlug());
    }
}
