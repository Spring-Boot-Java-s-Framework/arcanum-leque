package br.edu.faculdade.config;

import br.edu.faculdade.vaga.exception.RegraDeNegocioException;
import br.edu.faculdade.vaga.exception.VagaDuplicadaException;
import br.edu.faculdade.vaga.exception.VagaNaoEncontradaException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class TratadorDeErros {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail campoInvalido(MethodArgumentNotValidException e) {

        ProblemDetail problema = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        "Um ou mais campos estão inválidos.");
        problema.setTitle("Dados inválidos");

        Map<String, String> erros = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(f -> erros.put(f.getField(), f.getDefaultMessage()));
        problema.setProperty("campos", erros);

        return problema;
    }

    @ExceptionHandler(VagaNaoEncontradaException.class)
    public ProblemDetail vagaNaoEncontrada(VagaNaoEncontradaException e) {
        return problema(HttpStatus.NOT_FOUND, "Vaga não encontrada", e.getMessage());
    }

    @ExceptionHandler(VagaDuplicadaException.class)
    public ProblemDetail vagaDuplicada(VagaDuplicadaException e) {
        return problema(HttpStatus.CONFLICT, "Vaga duplicada", e.getMessage());
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ProblemDetail regraDeNegocio(RegraDeNegocioException e) {
        return problema(HttpStatus.UNPROCESSABLE_ENTITY, "Regra de negócio violada", e.getMessage());
    }

    private ProblemDetail problema(HttpStatus status, String titulo, String detalhe) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, detalhe);
        problema.setTitle(titulo);
        return problema;
    }

}
