package com.joaoneto.ecommerce.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoDTO {

    @Schema(description = "ID do produto", example = "501")
    private Long idProduto;

    @NotBlank(message = "O nome do produto não deve estar em branco")
    @Size(min = 3, max = 100, message = "O nome do produto deve ter entre 3 e 100 caracteres")
    @Schema(description = "Nome do produto", example = "Notebook GX")
    private String nomeProduto;

    @Schema(description = "Nome do arquivo de imagem do produto", example = "default.png")
    private String imagem;

    @NotBlank(message = "A descrição não deve estar em branco")
    @Size(min = 6, message = "A descrição deve ter no mínimo 6 caracteres")
    @Schema(description = "Descrição detalhada do produto", example = "Notebook gamer com 16GB de RAM e SSD de 512GB")
    private String descricao;

    @NotNull(message = "A quantidade em estoque é obrigatória")
    @PositiveOrZero(message = "A quantidade em estoque não pode ser negativa")
    @Schema(description = "Quantidade disponível em estoque", example = "50")
    private Integer quantidadeEstoque;

    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    @Schema(description = "Preço original do produto", example = "3499.90")
    private double preco;

    @DecimalMin(value = "0.0", message = "O desconto não pode ser negativo")
    @DecimalMax(value = "100.0", message = "O desconto não pode ser maior que 100%")
    @Schema(description = "Percentual de desconto aplicado ao produto", example = "10.0")
    private double desconto;

    @Schema(description = "Preço final do produto já com o desconto aplicado", example = "3149.91")
    private double precoEspecial;

}