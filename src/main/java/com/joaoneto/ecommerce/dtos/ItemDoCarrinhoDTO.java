package com.joaoneto.ecommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDoCarrinhoDTO {

    private Long idItemDoCarrinho;
    private CarrinhoDTO carrinho;
    private ProdutoDTO produto;
    private Integer quantidade;
    private Double desconto;
    private Double precoProduto;
}
