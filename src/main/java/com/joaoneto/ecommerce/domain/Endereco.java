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

    @NotBlank
    @Size(min = 1, max = 10, message = "O número deve conter entre 1 e 10 caracteres.")
    @Column(name = "numero_rua", length = 10, nullable = false)
    private String numeroRua;

    @NotBlank
    @Size(min = 3, max = 60, message = "O nome da cidade deve conter entre 3 e 60 caracteres.")
    @Column(name = "cidade", length = 60, nullable = false)
    private String cidade;

    @NotBlank
    @Size(min = 2, max = 30, message = "O nome do estado deve conter entre 2 e 30 caracteres.")
    @Column(name = "estado", length = 30, nullable = false)
    private String estado;

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

    public Endereco(String rua, String numeroRua, String cidade, String estado, String pais, String cep) {
        this.rua = rua;
        this.numeroRua = numeroRua;
        this.cidade = cidade;
        this.estado = estado;
        this.pais = pais;
        this.cep = cep;
    }
}