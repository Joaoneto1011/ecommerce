package com.joaoneto.ecommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarrinhoDTO {

    private Long idCarrinho;
    private double precoTotal = 0.0;
    private List<ItemDoCarrinhoDTO> itens = new ArrayList<>();
}
