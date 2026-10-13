package br.edu.faculdade.vaga.exception;

import br.edu.faculdade.vaga.dto.VagaEntrada;
import br.edu.faculdade.vaga.service.VagaService;

/**
 * Sinaliza que o pedido está bem formado, mas viola uma regra de negócio da
 * vaga (senioridade inexistente, slug inválido, iniciante em vaga sênior etc.).
 *
 * É lançada pelo {@link VagaService} e traduzida em HTTP 422 pelo
 * {@code TratadorDeErros}. A diferença para a validação do
 * {@link VagaEntrada} é de quem responde: lá é o formato do JSON (400); aqui é
 * o significado dos dados (422).
 *
 * @author Carlos Neto
 * @since 2026-10-13
 */
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
