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
    public ResponseEntity<Map<String, String>> tratarErrosValidacao(MethodArgumentNotValidException excecao) {

        Map<String, String> erros = new HashMap<>();

        excecao.getBindingResult().getAllErrors().forEach(erro -> {
            String campo = ((FieldError) erro).getField();
            String mensagem = erro.getDefaultMessage();
            erros.put(campo, mensagem);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<RespostaDaAPI> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException excecao) {

        String mensagem = excecao.getMessage();
        RespostaDaAPI respostaDaAPI = new RespostaDaAPI(mensagem, false);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respostaDaAPI);

    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<RespostaDaAPI> tratarAPIException(APIException excecao) {

        String mensagem = excecao.getMessage();
        RespostaDaAPI respostaDaAPI = new RespostaDaAPI(mensagem, false);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respostaDaAPI);
    }
}
