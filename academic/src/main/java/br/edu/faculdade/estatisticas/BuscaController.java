package br.edu.faculdade.estatisticas;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.faculdade.vaga.dto.VagaResposta;
import jakarta.validation.constraints.Size;

// @Validated precisa estar na classe para o @Size funcionar nos @RequestParam
@Validated
@RestController
public class BuscaController {

    private final BuscaService buscaService;

    public BuscaController(BuscaService buscaService) {
        this.buscaService = buscaService;
    }

    // busca sem resultado volta 200 com lista vazia, não é erro
    @GetMapping("/vagas/busca")
    public List<VagaResposta> buscar(
            @RequestParam(required = false) @Size(max = 50, message = "máximo de 50 caracteres") String area,
            @RequestParam(required = false) @Size(max = 50, message = "máximo de 50 caracteres") String senioridade,
            @RequestParam(required = false) Boolean aceitaIniciante) {
        return buscaService.buscar(area, senioridade, aceitaIniciante);
    }
}
