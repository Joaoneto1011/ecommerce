package com.joaoneto.ecommerce.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduto;

    @NotBlank
    @Size(min = 3, message = "O nome do Produto deve conter no minimo 3 letras")
    private String nomeProduto;
    private String imagem;

    @NotBlank
    @Size(min = 6, message = "A descricao do Produto deve conter no minimo 6 letras")
    private String descricao;
    private Integer quantidade;
    private double preco;
    private double desconto;
    private double precoEspecial;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
}
