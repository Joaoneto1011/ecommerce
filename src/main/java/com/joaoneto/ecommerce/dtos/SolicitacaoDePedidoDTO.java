package com.joaoneto.ecommerce.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoDePedidoDTO {

    @Schema(description = "ID do endereço de entrega selecionado", example = "1")
    private Long idEndereco;

    @Schema(description = "Método de pagamento utilizado", example = "cartao_credito")
    private String metodoDePagamento;

    @Schema(description = "Nome do gateway de pagamento utilizado", example = "Stripe")
    private String nomeGateway;

    @Schema(description = "ID do pagamento gerado pelo gateway", example = "pay_1a2b3c4d")
    private String idPagamentoGateway;

    @Schema(description = "Status retornado pelo gateway de pagamento", example = "aprovado")
    private String statusGateway;

    @Schema(description = "Mensagem retornada pelo gateway de pagamento", example = "Pagamento aprovado com sucesso")
    private String mensagemRespostaGateway;

}