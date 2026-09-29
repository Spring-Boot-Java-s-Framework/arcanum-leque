package br.edu.faculdade.smoke;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Scaffolding TEMPORÁRIO — só valida que a stack sobe e responde HTTP/JSON.
 *
 * <p>Não é uma frente. <b>Remover</b> quando a frente Vaga começar de verdade
 * (fluxo em {@code .github/CONTRIBUTING.md}).</p>
 *
 * <pre>
 *   GET /smoke  ->  {"status":"ok","frente":"vaga"}
 * </pre>
 */
@RestController
class SmokeController {

    @GetMapping("/smoke")
    Map<String, String> smoke() {
        return Map.of(
                "status", "ok",
                "frente", System.getenv().getOrDefault("FRENTE", "-"));
    }
}
