package com.joaoneto.ecommerce.domain;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "desconto")
    private double desconto;

    @Column(name = "preco_produto_pedido", nullable = false)
    private double precoProdutoPedido;
}
