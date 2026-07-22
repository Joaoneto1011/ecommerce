package com.joaoneto.ecommerce.exceptions;

import com.joaoneto.ecommerce.dtos.RespostaDaAPI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ManipuladorGlobalDeExceptions {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespostaDaAPI> tratarErrosValidacao(MethodArgumentNotValidException excecao) {

        Map<String, String> erros = new HashMap<>();

        excecao.getBindingResult().getAllErrors().forEach(erro -> {
            String campo = ((FieldError) erro).getField();
            String mensagem = erro.getDefaultMessage();
            erros.put(campo, mensagem);
        });

        RespostaDaAPI resposta = new RespostaDaAPI(
                "Erro de validação",
                false
        );
        resposta.setErros(erros);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<RespostaDaAPI> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException excecao) {

        RespostaDaAPI resposta = new RespostaDaAPI(
                excecao.getMessage(),
                false
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<RespostaDaAPI> tratarAPIException(APIException excecao) {

        RespostaDaAPI resposta = new RespostaDaAPI(
                excecao.getMessage(),
                false
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }
}