package br.edu.faculdade.estatisticas;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.edu.faculdade.vaga.model.Vaga;
import br.edu.faculdade.vaga.repository.VagaRepository;

@Service
public class EstatisticasService {

    // usa o repository da frente 1 direto, só precisa ler as vagas
    private final VagaRepository vagaRepository;

    public EstatisticasService(VagaRepository vagaRepository) {
        this.vagaRepository = vagaRepository;
    }

    public EstatisticasResposta calcular() {
        List<Vaga> vagas = vagaRepository.todas();

        int totalDeVagas = vagas.size();

        long aceitamIniciante = vagas.stream()
                .filter(vaga -> vaga.aceitaIniciante())
                .count();

        // agrupa pela área e conta quantas vagas tem em cada uma
        Map<String, Long> porArea = vagas.stream()
                .collect(Collectors.groupingBy(vaga -> vaga.area(), Collectors.counting()));

        Map<String, Long> porSenioridade = vagas.stream()
                .collect(Collectors.groupingBy(vaga -> vaga.senioridade(), Collectors.counting()));

        return new EstatisticasResposta(totalDeVagas, aceitamIniciante, porArea, porSenioridade);
    }
}
