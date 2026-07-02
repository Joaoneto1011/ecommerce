package com.joaoneto.ecommerce.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "perfis")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil")
    @EqualsAndHashCode.Include
    private Integer idPerfil;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "tipo_perfil")
    private TipoPerfil tipoPerfil;

    public Perfil(TipoPerfil tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }
}
