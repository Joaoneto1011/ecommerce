package com.joaoneto.ecommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoDTO {

    private Long idPagamento;
    private String metodoDePagamento;
    private String idPagamentoGateway;
    private String statusGateway;
    private String mensagemRespostaGateway;
    private String nomeGateway;
}
