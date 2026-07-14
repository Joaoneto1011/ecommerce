package com.joaoneto.ecommerce.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "pagamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pagamento")
    @EqualsAndHashCode.Include
    private Long idPagamento;

    @OneToOne(mappedBy = "pagamento", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Pedido pedido;

    @NotBlank
    @Size(min = 4, message = "O método de pagamento deve conter pelo menos 4 caracteres.")
    @Column(name = "metodo_pagamento", nullable = false)
    private String metodoDePagamento;

    @Column(name = "id_transacao_gateway")
    private String idPagamentoGateway;

    @Column(name = "status_gateway")
    private String statusGateway;

    @Column(name = "mensagem_resposta_gateway")
    private String mensagemRespostaGateway;

    @Column(name = "nome_gateway")
    private String nomeGateway;

    public Pagamento(String metodoDePagamento, String idPagamentoGateway, String statusGateway, String mensagemRespostaGateway, String nomeGateway) {
        this.metodoDePagamento = metodoDePagamento;
        this.idPagamentoGateway = idPagamentoGateway;
        this.statusGateway = statusGateway;
        this.mensagemRespostaGateway = mensagemRespostaGateway;
        this.nomeGateway = nomeGateway;
    }
}
