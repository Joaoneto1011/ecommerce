package com.joaoneto.ecommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDoPedidoDTO {

    private Long idItemDoPedido;
    private ProdutoDTO produto;
    private Integer quantidade;
    private double desconto;
    private double precoProdutoPedido;
}
