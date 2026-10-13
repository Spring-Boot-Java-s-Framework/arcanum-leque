package br.edu.faculdade.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;

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

    // frente 4: parâmetro da URL inválido (ex.: area com mais de 50 caracteres)
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail parametroInvalido(ConstraintViolationException e) {

        ProblemDetail problema = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        "Um ou mais parâmetros estão inválidos.");
        problema.setTitle("Dados inválidos");

        Map<String, String> erros = new LinkedHashMap<>();
        e.getConstraintViolations().forEach(v -> {
            // o caminho vem como "buscar.area", então pego só o que vem depois do ponto
            String caminho = v.getPropertyPath().toString();
            String parametro = caminho.substring(caminho.lastIndexOf('.') + 1);
            erros.put(parametro, v.getMessage());
        });
        problema.setProperty("campos", erros);

        return problema;
    }

    // frente 4: parâmetro da URL com tipo errado (ex.: aceitaIniciante=talvez)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail tipoInvalido(MethodArgumentTypeMismatchException e) {

        ProblemDetail problema = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        "Um ou mais parâmetros estão inválidos.");
        problema.setTitle("Dados inválidos");

        Map<String, String> erros = new LinkedHashMap<>();
        erros.put(e.getName(), "valor inválido: " + e.getValue());
        problema.setProperty("campos", erros);

        return problema;
    }

}
