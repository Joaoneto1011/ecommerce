package com.joaoneto.ecommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoDTO {

    private Long idProduto;
    private String nomeProduto;
    private String imagem;
    private String descricao;
    private Integer quantidadeEstoque;
    private double preco;
    private double desconto;
    private double precoEspecial;

}
