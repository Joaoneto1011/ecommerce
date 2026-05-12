package com.joaoneto.ecommerce.exceptions;

import com.joaoneto.ecommerce.dtos.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ManipuladorGlobalDeExcecoes {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarErrosValidacao(MethodArgumentNotValidException e) {

        Map<String, String> erros = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach(err -> {
            String campo = ((FieldError) err).getField();
            String mensagem = err.getDefaultMessage();
            erros.put(campo, mensagem);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<APIResponse> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException e) {

        String mensagem = e.getMessage();
        APIResponse apiResponse = new APIResponse(mensagem, false);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);

    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> tratarAPIException(APIException e) {

        String mensagem = e.getMessage();
        APIResponse apiResponse = new APIResponse(mensagem, false);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }
}
