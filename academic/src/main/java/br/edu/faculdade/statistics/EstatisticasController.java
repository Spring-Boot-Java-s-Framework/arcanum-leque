package br.edu.faculdade.statistics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EstatisticasController {

    private final EstatisticasService estatisticasService;

    public EstatisticasController(EstatisticasService estatisticasService) {
        this.estatisticasService = estatisticasService;
    }

    @GetMapping("/estatisticas")
    public EstatisticasResposta estatisticas() {
        return estatisticasService.calcular();
    }
}
