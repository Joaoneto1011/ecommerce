package com.joaoneto.ecommerce.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "carrinhos")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Carrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrinho")
    @EqualsAndHashCode.Include
    private Long idCarrinho;

    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @OneToMany(mappedBy = "carrinho", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    private List<ItemDoCarrinho> itensDoCarrinho  = new ArrayList<>();

    @Column(name = "preco_total")
    private double precoTotal = 0.0;

}
