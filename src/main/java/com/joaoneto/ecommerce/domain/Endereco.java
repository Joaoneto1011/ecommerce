package com.joaoneto.ecommerce.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "endereços")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEndereco;

    @NotBlank
    @Size(min = 5, message = "O nome da rua deve ter pelo menos 5 letras")
    private String rua;

    @NotBlank
    @Size(min = 5, message = "O nome do edificio deve ter pelo menos 5 letras")
    private String nomeEdificio;

    @NotBlank
    @Size(min = 4, message = "O nome da cidade deve ter pelo menos 4 letras")
    private String cidade;

    @NotBlank
    @Size(min = 2, message = "O nome do país deve ter pelo menos 2 letras")
    private String pais;

    @NotBlank
    @Size(min = 6, message = "O codigo postal deve ter pelo menos 6 números")
    private String codigoPostal;

    @ToString.Exclude
    @ManyToMany(mappedBy = "enderecos")
    private List<Usuario> usuarios = new ArrayList<>();

    public Endereco(String rua, String nomeEdificio, String cidade, String pais, String codigoPostal) {
        this.rua = rua;
        this.nomeEdificio = nomeEdificio;
        this.cidade = cidade;
        this.pais = pais;
        this.codigoPostal = codigoPostal;
    }
}
