package com.joaoneto.ecommerce.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_do_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemDoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_do_pedido")
    @EqualsAndHashCode.Include
    private Long idItemDoPedido;

    @ManyToOne
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "desconto", precision = 5, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(name = "preco_produto_pedido", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoProdutoPedido;
}
