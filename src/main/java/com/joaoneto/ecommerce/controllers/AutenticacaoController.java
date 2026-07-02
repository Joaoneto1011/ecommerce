package com.joaoneto.ecommerce.controllers;

import com.joaoneto.ecommerce.domain.TipoPerfil;
import com.joaoneto.ecommerce.domain.Perfil;
import com.joaoneto.ecommerce.domain.Usuario;
import com.joaoneto.ecommerce.repositories.PerfilRepository;
import com.joaoneto.ecommerce.repositories.UsuarioRepository;
import com.joaoneto.ecommerce.security.jwt.UtilitarioJwt;
import com.joaoneto.ecommerce.security.request.SolicitacaoDeCadastro;
import com.joaoneto.ecommerce.security.request.SolicitacaoDeLogin;
import com.joaoneto.ecommerce.security.response.MensagemDeResposta;
import com.joaoneto.ecommerce.security.response.RespostaDeInformacoesUsuario;
import com.joaoneto.ecommerce.security.services.ImplementacaoDetalhesUsuario;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/autenticacao")
public class AutenticacaoController {

    private final UtilitarioJwt utilitarioJwt;

    private final AuthenticationManager gerenciadorDeAutenticacao;

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder codificadorDeSenha;

    private final PerfilRepository perfilRepository;

    public AutenticacaoController(UtilitarioJwt utilitarioJwt, AuthenticationManager gerenciadorDeAutenticacao, UsuarioRepository usuarioRepository, PasswordEncoder codificadorDeSenha, PerfilRepository perfilRepository) {
        this.utilitarioJwt = utilitarioJwt;
        this.gerenciadorDeAutenticacao = gerenciadorDeAutenticacao;
        this.usuarioRepository = usuarioRepository;
        this.codificadorDeSenha = codificadorDeSenha;
        this.perfilRepository = perfilRepository;
    }

    @PostMapping("/entrar")
    public ResponseEntity<?> autenticarUsuario(@RequestBody SolicitacaoDeLogin solicitacaoDeLogin) {

        Authentication autenticacao;

        try {
            autenticacao = gerenciadorDeAutenticacao
                    .authenticate(new UsernamePasswordAuthenticationToken(solicitacaoDeLogin.getNomeUsuario(), solicitacaoDeLogin.getSenha()));
        } catch (AuthenticationException excecao) {
            Map<String, Object> map = new HashMap<>();
            map.put("mensagem", "Credenciais inválidas");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(autenticacao);

        ImplementacaoDetalhesUsuario detalhesUsuario = (ImplementacaoDetalhesUsuario) autenticacao.getPrincipal();

        ResponseCookie jwtCookie = utilitarioJwt.gerarCookieJwt(detalhesUsuario);

        List<String> perfis = detalhesUsuario.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RespostaDeInformacoesUsuario resposta = new RespostaDeInformacoesUsuario(detalhesUsuario.getId(), detalhesUsuario.getNomeUsuario(), perfis);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                        jwtCookie.toString())
                .body(resposta);
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody SolicitacaoDeCadastro solicitacaoDeCadastro) {

        if(usuarioRepository.existsByNomeUsuario(solicitacaoDeCadastro.getNomeUsuario())) {
            return ResponseEntity.badRequest().body(new MensagemDeResposta("Erro: Nome de usuário já está em uso!"));
        }

        if(usuarioRepository.existsByEmail(solicitacaoDeCadastro.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MensagemDeResposta("Erro: Email já está em uso!"));
        }

        Usuario usuario = new Usuario(
                solicitacaoDeCadastro.getNomeUsuario(),
                solicitacaoDeCadastro.getEmail(),
                codificadorDeSenha.encode(solicitacaoDeCadastro.getSenha())
        );

        Set<String> perfisSelecionados = solicitacaoDeCadastro.getPerfis();
        Set<Perfil> perfis = new HashSet<>();

        if(perfisSelecionados == null) {
            Perfil perfilUsuario = perfilRepository.findByTipoPerfil(TipoPerfil.PERFIL_USUARIO)
                    .orElseThrow(() -> new RuntimeException("Erro: Perfil não encontrado"));
            perfis.add(perfilUsuario);
        } else {
            perfisSelecionados.forEach(perfil -> {
                switch (perfil) {
                    case "administrador":
                        Perfil perfilAdministrador = perfilRepository.findByTipoPerfil(TipoPerfil.PERFIL_ADMINISTRADOR)
                                .orElseThrow(() -> new RuntimeException("Erro: Perfil não encontrado"));
                        perfis.add(perfilAdministrador);
                        break;
                    case "vendedor":
                        Perfil perfilVendedor = perfilRepository.findByTipoPerfil(TipoPerfil.PERFIL_VENDEDOR)
                                .orElseThrow(() -> new RuntimeException("Erro: Perfil não encontrado"));
                        perfis.add(perfilVendedor);
                        break;
                    default:
                        Perfil perfilUsuario = perfilRepository.findByTipoPerfil(TipoPerfil.PERFIL_USUARIO)
                                .orElseThrow(() -> new RuntimeException("Erro: Perfil não encontrado"));
                        perfis.add(perfilUsuario);
                }
            });
        }
        usuario.setPerfis(perfis);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(new MensagemDeResposta("Usuário registrado com sucesso!"));

    }

    @GetMapping("/nome-usuario")
    public String nomeUsuarioAtual(Authentication autenticacao) {
        if (autenticacao != null)
            return autenticacao.getName();
        else
            return "";
    }

    @GetMapping("/usuario")
    public ResponseEntity<?> buscarDetalhesUsuario(Authentication autenticacao) {
        ImplementacaoDetalhesUsuario detalhesUsuario = (ImplementacaoDetalhesUsuario) autenticacao.getPrincipal();

        List<String> perfis = detalhesUsuario.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RespostaDeInformacoesUsuario resposta = new RespostaDeInformacoesUsuario(detalhesUsuario.getId(),
                detalhesUsuario.getNomeUsuario(), perfis);

        return ResponseEntity.ok().body(resposta);
    }

    @PostMapping("sair")
    public ResponseEntity<?> sairUsuario() {
        ResponseCookie cookie = utilitarioJwt.obterCookieJwtLimpo();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                        cookie.toString())
                .body(new MensagemDeResposta("Você foi desconectado!"));
    }
}
