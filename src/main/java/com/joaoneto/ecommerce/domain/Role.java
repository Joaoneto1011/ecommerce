package com.joaoneto.ecommerce.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer idRole;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "nome_role")
    private AppRole nomeRole;

    public Role(AppRole nomeRole) {
        this.nomeRole = nomeRole;
    }
}
