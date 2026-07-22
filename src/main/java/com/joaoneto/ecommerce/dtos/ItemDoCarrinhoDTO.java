package com.joaoneto.ecommerce.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDoCarrinhoDTO {

    @Schema(description = "ID do item do carrinho", example = "1")
    private Long idItemDoCarrinho;

    @Schema(description = "Produto vinculado a este item do carrinho")
    private ProdutoDTO produto;

    @Schema(description = "Quantidade do produto adicionada ao carrinho", example = "2")
    private Integer quantidade;

    @Schema(description = "Percentual de desconto aplicado ao item", example = "10.0")
    private double desconto;

    @Schema(description = "Preço unitário do item com o desconto aplicado, válido enquanto o item está no carrinho (é um valor provisório — pode mudar até a compra ser finalizada)", example = "3149.91")
    private double precoComDesconto;
}