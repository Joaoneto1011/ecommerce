package com.joaoneto.ecommerce.exceptions;

import com.joaoneto.ecommerce.dtos.RespostaDaAPI;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ManipuladorGlobalDeExceptions {

    private static final Logger logger = LoggerFactory.getLogger(ManipuladorGlobalDeExceptions.class);

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespostaDaAPI> tratarJsonInvalido(HttpMessageNotReadableException excecao) {

        RespostaDaAPI resposta = new RespostaDaAPI(
                "Corpo da requisição inválido: verifique o formato dos campos enviados.",
                false
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RespostaDaAPI> tratarTipoInvalido(MethodArgumentTypeMismatchException excecao) {
        String mensagem = String.format("O valor '%s' é inválido para o parâmetro '%s'. Esperado: %s",
                excecao.getValue(), excecao.getName(),
                excecao.getRequiredType() != null ? excecao.getRequiredType().getSimpleName() : "tipo válido");

        RespostaDaAPI resposta = new RespostaDaAPI(mensagem, false);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RespostaDaAPI> tratarViolacaoDeIntegridade(DataIntegrityViolationException excecao) {
        RespostaDaAPI resposta = new RespostaDaAPI(
                "Não é possível excluir: existem registros vinculados a este recurso.",
                false
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(resposta);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RespostaDaAPI> tratarViolacaoDeConstraintDaEntidade(ConstraintViolationException excecao) {

        Map<String, String> erros = new HashMap<>();

        for (ConstraintViolation<?> violacao : excecao.getConstraintViolations()) {
            String campo = violacao.getPropertyPath().toString();
            erros.put(campo, violacao.getMessage());
        }

        RespostaDaAPI resposta = new RespostaDaAPI("Erro de validação", false);
        resposta.setErros(erros);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<RespostaDaAPI> tratarConflitoDeConcorrencia(ObjectOptimisticLockingFailureException excecao) {
        RespostaDaAPI resposta = new RespostaDaAPI(
                "Este recurso foi alterado por outra requisição enquanto você o modificava. Tente novamente.",
                false
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(resposta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespostaDaAPI> tratarErroGenerico(Exception excecao) {
        logger.error("Erro não tratado", excecao);
        RespostaDaAPI resposta = new RespostaDaAPI(
                "Erro interno do servidor. Tente novamente mais tarde.",
                false
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
    }
}