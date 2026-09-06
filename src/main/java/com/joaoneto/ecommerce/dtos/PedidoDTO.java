package com.joaoneto.ecommerce.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

    @Schema(description = "ID do pedido", example = "1")
    private Long idPedido;

    @Schema(description = "Email do usuário que realizou o pedido", example = "usuario@exemplo.com")
    private String email;

    @Schema(description = "Lista de itens que compõem o pedido")
    private List<ItemDoPedidoDTO> itensDoPedido;

    @Schema(description = "Data em que o pedido foi realizado", example = "2026-07-17")
    private LocalDate dataDoPedido;

    @Schema(description = "Dados de pagamento do pedido")
    private PagamentoDTO pagamento;

    @Schema(description = "Valor total do pedido", example = "6299.82")
    private BigDecimal valorTotal;

    @Schema(description = "Status atual do pedido", example = "PENDENTE")
    private String statusPedido;

    @Schema(description = "Endereço para o qual o pedido será entregue")
    private EnderecoDTO enderecoDeEntrega;
}