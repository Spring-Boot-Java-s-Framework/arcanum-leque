package br.edu.faculdade.vaga.exception;

import br.edu.faculdade.vaga.service.VagaService;

/**
 * Sinaliza que já existe uma vaga com o mesmo título para a mesma empresa.
 *
 * É lançada pelo {@link VagaService} e traduzida em HTTP 409 (conflito) pelo
 * {@code TratadorDeErros}: o pedido é válido, mas colide com algo que já
 * existe.
 *
 * @author Carlos Neto
 * @since 2026-10-13
 */
public class VagaDuplicadaException extends RuntimeException {

    public VagaDuplicadaException(String titulo, String empresaSlug) {
        super("A empresa '" + empresaSlug + "' já tem uma vaga chamada '" + titulo + "'.");
    }
}
