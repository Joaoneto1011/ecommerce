package com.joaoneto.ecommerce.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RespostaDaAPI {

    @Schema(
            description = "Mensagem descritiva do resultado da operação",
            example = "Usuário registrado com sucesso!")
    private String mensagem;

    @Schema(
            description = "Indica se a operação foi bem-sucedida",
            example = "true")
    private boolean status;

    @Schema(
            description = "Lista de erros de validação, onde a chave representa o campo e o valor representa a mensagem de erro",
            nullable = true)
    private Map<String, String> erros;

    public RespostaDaAPI(String mensagem, boolean status) {
        this.mensagem = mensagem;
        this.status = status;
    }

}