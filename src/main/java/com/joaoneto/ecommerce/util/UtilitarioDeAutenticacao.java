package com.joaoneto.ecommerce.util;

import com.joaoneto.ecommerce.domain.Usuario;
import com.joaoneto.ecommerce.repositories.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UtilitarioDeAutenticacao {

    private final UsuarioRepository usuarioRepository;

    public UtilitarioDeAutenticacao(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public String emailDoUsuarioLogado() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioRepository.findByNomeUsuario(autenticacao.getName())
                .orElseThrow(() -> new UsernameNotFoundException(("Usuário não encontrado com o nome de usuário: " + autenticacao.getName())));

        return usuario.getEmail();
    }

    public Usuario usuarioLogado() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();

        Usuario usuario = usuarioRepository.findByNomeUsuario(autenticacao.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario não encontrado com o nome de usuário: " + autenticacao.getName()));

        return usuario;
    }
}
