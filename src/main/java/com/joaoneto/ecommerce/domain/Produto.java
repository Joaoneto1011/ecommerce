package com.joaoneto.ecommerce.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produtos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    @EqualsAndHashCode.Include
    private Long idProduto;

    @NotBlank
    @Size(min = 3, max = 100, message = "O nome do Produto deve conter entre 3 e 100 caracteres.")
    @Column(name = "nome_produto", length = 100, nullable = false)
    private String nomeProduto;

    @Column(name = "imagem")
    private String imagem;

    @NotBlank
    @Size(min = 6, max = 500, message = "A descricao do Produto deve conter entre 6 e 500 caracteres.")
    @Column(name = "descricao", length = 500, nullable = false)
    private String descricao;

    @PositiveOrZero(message = "A quantidade não pode ser negativa.")
    @Column(name = "quantidade")
    private Integer quantidade;

    @PositiveOrZero(message = "O preço não pode ser negativo.")
    @Column(name = "preco", nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @Min(value = 0, message = "O desconto não pode ser menor que 0%.")
    @Max(value = 100, message = "O desconto não pode ser maior que 100%.")
    @Column(name = "desconto", precision = 5, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(name = "preco_especial", precision = 12, scale = 2)
    private BigDecimal precoEspecial = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "id_vendedor")
    private Usuario usuario;

    @OneToMany(mappedBy = "produto", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<ItemDoCarrinho> itensDoCarrinho = new ArrayList<>();

    @Version
    @Column(name = "versao")
    private Long versao;
}
