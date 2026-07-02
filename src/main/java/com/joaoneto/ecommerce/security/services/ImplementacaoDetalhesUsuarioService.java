package com.joaoneto.ecommerce.security.services;

import com.joaoneto.ecommerce.domain.Usuario;
import com.joaoneto.ecommerce.exceptions.NomeUsuarioNaoEncontradoException;
import com.joaoneto.ecommerce.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImplementacaoDetalhesUsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String nomeUsuario) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByNomeUsuario(nomeUsuario)
                .orElseThrow(() ->
                        new NomeUsuarioNaoEncontradoException("Usuario não encontrado com nome de usuario: " + nomeUsuario));

        return ImplementacaoDetalhesUsuario.criarDe(usuario);
    }
}
