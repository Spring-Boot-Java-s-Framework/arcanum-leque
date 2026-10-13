package br.edu.faculdade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da API do Leque de Vagas.
 *
 * As 4 frentes ficam em pacotes-irmãos deste ({@code vaga}, {@code empresa},
 * {@code pessoa}, {@code estatisticas}), cada uma no trio
 * {@code controller -> service -> repository}. O container Spring faz toda a
 * ligação por construtor — nenhum {@code new} entre classes do projeto.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
