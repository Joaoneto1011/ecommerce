package com.joaoneto.ecommerce.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoDTO {

    @Schema(description = "ID do produto", example = "501")
    private Long idProduto;

    @Schema(description = "Nome do produto", example = "Notebook GX")
    private String nomeProduto;

    @Schema(description = "Nome do arquivo de imagem do produto", example = "default.png")
    private String imagem;

    @Schema(description = "Descrição detalhada do produto", example = "Notebook gamer com 16GB de RAM e SSD de 512GB")
    private String descricao;

    @Schema(description = "Quantidade disponível em estoque", example = "50")
    private Integer quantidadeEstoque;

    @Schema(description = "Preço original do produto", example = "3499.90")
    private double preco;

    @Schema(description = "Percentual de desconto aplicado ao produto", example = "10.0")
    private double desconto;

    @Schema(description = "Preço final do produto já com o desconto aplicado", example = "3149.91")
    private double precoEspecial;

}