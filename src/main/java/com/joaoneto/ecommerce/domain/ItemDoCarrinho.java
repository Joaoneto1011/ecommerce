package com.joaoneto.ecommerce.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "itens_do_carrinho")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemDoCarrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_do_carrinho")
    @EqualsAndHashCode.Include
    private Long idItemDoCarrinho;

    @ManyToOne
    @JoinColumn(name = "id_carrinho")
    private Carrinho carrinho;

    @ManyToOne
    @JoinColumn(name = "id_produto")
    private Produto produto;

    @PositiveOrZero(message = "A quantidade não pode ser negativa.")
    @Column(name = "quantidade")
    private Integer quantidade;

    @Min(value = 0, message = "O desconto não pode ser menor que 0%.")
    @Max(value = 100, message = "O desconto não pode ser maior que 100%.")
    @Column(name = "desconto")
    private double desconto;

    @PositiveOrZero(message = "O preço do produto não pode ser negativo.")
    @Column(name = "preco_produto")
    private double precoProduto;
}
