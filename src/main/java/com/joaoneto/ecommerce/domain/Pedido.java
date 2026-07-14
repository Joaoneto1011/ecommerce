package com.joaoneto.ecommerce.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    @EqualsAndHashCode.Include
    private Long idPedido;

    @NotBlank
    @Size(max = 50)
    @Email
    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @OneToMany(mappedBy = "pedido",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ItemDoPedido> itensDoPedido = new ArrayList<>();

    @Column(name = "data_pedido", nullable = false)
    private LocalDate dataDoPedido;

    @Column(name = "valor_total", nullable = false)
    private double valorTotal;

    @OneToOne
    @JoinColumn(name = "id_pagamento")
    private Pagamento pagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pedido", nullable = false)
    private StatusPedido statusPedido;

    @ManyToOne
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;


}
