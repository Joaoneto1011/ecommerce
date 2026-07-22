package com.joaoneto.ecommerce.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarrinhoDTO {

    @Schema(description = "ID do carrinho", example = "1")
    private Long idCarrinho;

    @Schema(description = "Preço total de todos os itens do carrinho", example = "6299.82")
    private double precoTotal = 0.0;

    @Schema(description = "Lista de itens presentes no carrinho")
    private List<ItemDoCarrinhoDTO> itens = new ArrayList<>();
}