package br.edu.faculdade.vaga.exception;

import br.edu.faculdade.vaga.service.VagaService;

/**
 * Sinaliza que nenhuma vaga existe com o identificador informado.
 *
 * É lançada pelo {@link VagaService} e traduzida em HTTP 404 pelo
 * {@code TratadorDeErros}. Assim o controller não precisa decidir nada: ele só
 * chama o service e deixa a exceção subir.
 *
 * @author Carlos Neto
 * @since 2026-10-13
 */
public class VagaNaoEncontradaException extends RuntimeException {

    public VagaNaoEncontradaException(String id) {
        super("Não existe vaga com o id '" + id + "'.");
    }
}
