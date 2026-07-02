package com.joaoneto.ecommerce.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.joaoneto.ecommerce.domain.Usuario;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class ImplementacaoDetalhesUsuario implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String nomeUsuario;

    private String email;

    @JsonIgnore
    private String senha;

    private Collection<? extends GrantedAuthority> autoridades;

    public ImplementacaoDetalhesUsuario(Long id, String nomeUsuario, String email, String senha,
                                        Collection<? extends GrantedAuthority> autoridades) {
        this.id = id;
        this.nomeUsuario = nomeUsuario;
        this.email = email;
        this.senha = senha;
        this.autoridades = autoridades;
    }

    public static ImplementacaoDetalhesUsuario criarDe(Usuario usuario) {

        List<GrantedAuthority> autoridades = usuario.getPerfis().stream()
                .map(perfil -> new SimpleGrantedAuthority(perfil.getTipoPerfil().name()))
                .collect(Collectors.toList());

        return new ImplementacaoDetalhesUsuario(
                usuario.getIdUsuario(),
                usuario.getNomeUsuario(),
                usuario.getEmail(),
                usuario.getSenha(),
                autoridades
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return autoridades;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return nomeUsuario;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ImplementacaoDetalhesUsuario that = (ImplementacaoDetalhesUsuario) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
