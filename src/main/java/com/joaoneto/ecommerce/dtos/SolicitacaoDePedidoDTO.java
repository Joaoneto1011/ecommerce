package com.joaoneto.ecommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoDePedidoDTO {

    private Long idEndereco;
    private String metodoDePagamento;
    private String nomeGateway;
    private String idPagamentoGateway;
    private String statusGateway;
    private String mensagemRespostaGateway;

}
