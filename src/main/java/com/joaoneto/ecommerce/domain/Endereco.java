package com.joaoneto.ecommerce.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "enderecos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_endereco")
    @EqualsAndHashCode.Include
    private Long idEndereco;

    @NotBlank
    @Size(min = 5, max = 100, message = "O nome da rua deve conter entre 5 e 100 caracteres.")
    @Column(name = "rua", length = 100, nullable = false)
    private String rua;

    @Size(max = 100, message = "O nome do edifício deve conter no maximo 100 caracteres")
    @Column(name = "nome_edificio", length = 100)
    private String nomeEdificio;

    @NotBlank
    @Size(min = 3, max = 60, message = "O nome da cidade deve conter entre 3 e 60 caracteres.")
    @Column(name = "cidade", length = 60, nullable = false)
    private String cidade;

    @NotBlank
    @Size(min = 2, max = 60, message = "O nome do país deve conter entre 2 e 60 caracteres.")
    @Column(name = "pais", length = 60, nullable = false)
    private String pais;

    @NotBlank
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "O CEP deve estar no formato 00000-000 ou 00000000.")
    @Column(name = "cep", length = 9, nullable = false)
    private String cep;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    public Endereco(String rua, String nomeEdificio, String cidade, String pais, String cep) {
        this.rua = rua;
        this.nomeEdificio = nomeEdificio;
        this.cidade = cidade;
        this.pais = pais;
        this.cep = cep;
    }
}