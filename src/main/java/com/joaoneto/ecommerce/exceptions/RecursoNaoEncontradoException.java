package com.joaoneto.ecommerce.exceptions;

public class RecursoNaoEncontradoException extends RuntimeException {

   public RecursoNaoEncontradoException(String nomeRecurso, String campo, Long idCampo) {
       super(String.format("%s nao encontrado com %s: %d", nomeRecurso, campo, idCampo));
   }
}
