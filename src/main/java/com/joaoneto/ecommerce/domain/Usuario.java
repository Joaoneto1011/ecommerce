package com.joaoneto.ecommerce.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "usuarios",
       uniqueConstraints = {
        @UniqueConstraint(columnNames = "nomeUsuario"),
        @UniqueConstraint(columnNames = "email")
       })
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    @EqualsAndHashCode.Include
    private Long idUsuario;

    @NotBlank
    @Size(max = 20)
    @Column(name = "nome_usuario", length = 20, nullable = false)
    private String nomeUsuario;

    @NotBlank
    @Size(max = 50)
    @Email
    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @NotBlank
    @Size(max = 120)
    @Column(name = "senha", length = 120, nullable = false)
    private String senha;

    public Usuario(String nomeUsuario, String email, String senha) {
        this.nomeUsuario = nomeUsuario;
        this.email = email;
        this.senha = senha;
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
    fetch = FetchType.EAGER)
    @JoinTable(name = "perfil_usuario",
                joinColumns = @JoinColumn(name = "id_usuario"),
                inverseJoinColumns = @JoinColumn(name = "id_role"))
    private Set<Perfil> perfis = new HashSet<>();

    @OneToMany(mappedBy = "usuario",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        orphanRemoval = true)
    private List<Endereco> enderecos = new ArrayList<>();


    @OneToMany(mappedBy = "usuario",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private Set<Produto> produtos = new HashSet<>();

}
