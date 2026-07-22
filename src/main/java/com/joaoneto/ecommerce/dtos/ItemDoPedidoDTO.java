package com.joaoneto.ecommerce.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDoPedidoDTO {

    @Schema(description = "ID do item do pedido", example = "1")
    private Long idItemDoPedido;

    @Schema(description = "Produto vinculado a este item do pedido")
    private ProdutoDTO produto;

    @Schema(description = "Quantidade do produto no pedido", example = "2")
    private Integer quantidade;

    @Schema(description = "Percentual de desconto aplicado ao item", example = "10.0")
    private double desconto;

    @Schema(description = "Preço unitário do produto com desconto, congelado no exato momento em que o pedido foi criado (não muda depois, mesmo que o preço do produto no catálogo seja alterado)", example = "3149.91")
    private double precoProdutoPedido;
}