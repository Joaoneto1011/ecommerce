package com.joaoneto.ecommerce.controllers;

import com.joaoneto.ecommerce.domain.Perfil;
import com.joaoneto.ecommerce.domain.TipoPerfil;
import com.joaoneto.ecommerce.domain.Usuario;
import com.joaoneto.ecommerce.dtos.RespostaDaAPI;
import com.joaoneto.ecommerce.repositories.PerfilRepository;
import com.joaoneto.ecommerce.repositories.UsuarioRepository;
import com.joaoneto.ecommerce.security.jwt.UtilitarioJwt;
import com.joaoneto.ecommerce.security.request.SolicitacaoDeCadastro;
import com.joaoneto.ecommerce.security.request.SolicitacaoDeLogin;
import com.joaoneto.ecommerce.security.response.RespostaDeInformacoesUsuario;
import com.joaoneto.ecommerce.security.services.ImplementacaoDetalhesUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "APIs de Autenticação", description = "APIs públicas para login, cadastro e sessão do usuário")
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

    @Operation(summary = "Autenticar usuário", description = "API para autenticar um usuário e retornar um cookie JWT válido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso",
                    content = @Content(schema = @Schema(implementation = RespostaDeInformacoesUsuario.class))),
            @ApiResponse(responseCode = "404", description = "Credenciais inválidas (nome de usuário ou senha incorretos)",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Credenciais inválidas\", \"status\": false}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @PostMapping("/entrar")
    public ResponseEntity<?> autenticarUsuario(@RequestBody SolicitacaoDeLogin solicitacaoDeLogin) {

        Authentication autenticacao;

        try {
            autenticacao = gerenciadorDeAutenticacao
                    .authenticate(new UsernamePasswordAuthenticationToken(solicitacaoDeLogin.getNomeUsuario(), solicitacaoDeLogin.getSenha()));
        } catch (AuthenticationException excecao) {
            return new ResponseEntity<>(new RespostaDaAPI("Credenciais inválidas", false), HttpStatus.NOT_FOUND);
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

    @Operation(summary = "Cadastrar usuário", description = "API para registrar um novo usuário na aplicação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Usuário registrado com sucesso!\"}"))),
            @ApiResponse(responseCode = "400", description = "Nome de usuário/email já em uso, ou corpo da requisição inválido (campos obrigatórios ausentes)",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = { @ExampleObject(name = "Nome de usuário já está em uso", value = "{\"mensagem\": \"Erro: Nome de usuário já está em uso!\", \"status\": false}"),
                                         @ExampleObject(name = "Email já está em uso", value = "{\"mensagem\": \"Erro: Email já está em uso!\", \"status\": false}"),
                                         @ExampleObject(name = "Corpo da requisição inválido", value = "{\"mensagem\": \"Erro de validação\", \"status\": false, \"erros\": {\"nomeUsuario\": \"O nome de usuário é obrigatório\", \"email\": \"O email deve ser válido\", \"senha\": \"A senha deve possuir no mínimo 6 caracteres\"}}")
                            })),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @PostMapping("/cadastrar")
    public ResponseEntity<RespostaDaAPI> registrarUsuario(@Valid @RequestBody SolicitacaoDeCadastro solicitacaoDeCadastro) {

        if(usuarioRepository.existsByNomeUsuario(solicitacaoDeCadastro.getNomeUsuario())) {
            return ResponseEntity.badRequest().body(new RespostaDaAPI("Erro: Nome de usuário já está em uso!", false));
        }

        if(usuarioRepository.existsByEmail(solicitacaoDeCadastro.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new RespostaDaAPI("Erro: Email já está em uso!", false));
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
        return ResponseEntity.ok(new RespostaDaAPI("Usuário registrado com sucesso!", true));

    }

    @Operation(summary = "Nome do usuário atual", description = "API para retornar o nome do usuário autenticado na requisição")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nome do usuário retornado com sucesso (string vazia se não autenticado)",
                    content = @Content(schema = @Schema(type = "string"), examples = @ExampleObject(value = "joaoneto"))),
    })
    @GetMapping("/nome-usuario")
    public String nomeUsuarioAtual(Authentication autenticacao) {
        if (autenticacao != null)
            return autenticacao.getName();
        else
            return "";
    }

    @Operation(summary = "Detalhes do usuário atual", description = "API para buscar as informações do usuário autenticado na requisição")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalhes do usuário retornados com sucesso",
                    content = @Content(schema = @Schema(implementation = RespostaDeInformacoesUsuario.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @GetMapping("/usuario")
    public ResponseEntity<RespostaDeInformacoesUsuario> buscarDetalhesUsuario(Authentication autenticacao) {
        ImplementacaoDetalhesUsuario detalhesUsuario = (ImplementacaoDetalhesUsuario) autenticacao.getPrincipal();

        List<String> perfis = detalhesUsuario.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RespostaDeInformacoesUsuario resposta = new RespostaDeInformacoesUsuario(detalhesUsuario.getId(),
                detalhesUsuario.getNomeUsuario(), perfis);

        return ResponseEntity.ok().body(resposta);
    }

    @Operation(summary = "Sair da sessão", description = "API para desconectar o usuário, limpando o cookie JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário desconectado com sucesso",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Você foi desconectado!\"}"))),
    })
    @PostMapping("sair")
    public ResponseEntity<RespostaDaAPI> sairUsuario() {
        ResponseCookie cookie = utilitarioJwt.obterCookieJwtLimpo();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                        cookie.toString())
                .body(new RespostaDaAPI("Você foi desconectado!", true));
    }
}