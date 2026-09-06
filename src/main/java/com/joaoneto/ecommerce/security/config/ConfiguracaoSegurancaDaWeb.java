package com.joaoneto.ecommerce.security.config;

import com.joaoneto.ecommerce.domain.Perfil;
import com.joaoneto.ecommerce.domain.TipoPerfil;
import com.joaoneto.ecommerce.domain.Usuario;
import com.joaoneto.ecommerce.repositories.PerfilRepository;
import com.joaoneto.ecommerce.repositories.UsuarioRepository;
import com.joaoneto.ecommerce.security.jwt.FiltroDeTokenAutenticacao;
import com.joaoneto.ecommerce.security.jwt.TratadorDeAcessoNaoAutorizado;
import com.joaoneto.ecommerce.security.jwt.TratadorDeAcessoNegado;
import com.joaoneto.ecommerce.security.services.ImplementacaoDetalhesUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
public class ConfiguracaoSegurancaDaWeb {

    @Autowired
    private ImplementacaoDetalhesUsuarioService implementacaoDetalhesUsuarioService;

    @Autowired
    private TratadorDeAcessoNaoAutorizado tratadorDeAcessoNaoAutorizado;

    @Autowired
    private TratadorDeAcessoNegado tratadorDeAcessoNegado;

    @Bean
    public FiltroDeTokenAutenticacao filtroDeAutenticacaoPorTokenJWT() {
        return new FiltroDeTokenAutenticacao();
    }

    @Bean
    public DaoAuthenticationProvider provedorDeAutenticacao() {

        DaoAuthenticationProvider provedorDeAutenticacao = new DaoAuthenticationProvider(implementacaoDetalhesUsuarioService);
        provedorDeAutenticacao.setPasswordEncoder(codificadorDeSenha());
        return provedorDeAutenticacao;
    }

    @Bean
    public AuthenticationManager gerenciadorDeAutenticacao(AuthenticationConfiguration configuracaoDeAutenticacao) throws Exception {

        return configuracaoDeAutenticacao.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder codificadorDeSenha() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain cadeiaDeFiltros(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(excecoes -> excecoes
                        .authenticationEntryPoint(tratadorDeAcessoNaoAutorizado)
                        .accessDeniedHandler(tratadorDeAcessoNegado))
                .sessionManagement(
                        sessao ->
                                sessao.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(autenticacao ->
                        autenticacao.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/api/autenticacao/entrar", "/api/autenticacao/cadastrar",
                                        "/api/autenticacao/sair", "/api/autenticacao/nome-usuario").permitAll()
                                .requestMatchers("/error").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/api/public/**").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/api/administrador/**").hasAuthority("PERFIL_ADMINISTRADOR")
                                .requestMatchers("/imagens/**").permitAll()
                                .anyRequest().authenticated());

        http.authenticationProvider(provedorDeAutenticacao());

        http.addFilterBefore(filtroDeAutenticacaoPorTokenJWT(),
                UsernamePasswordAuthenticationFilter.class);

        http.headers(headers -> headers.frameOptions(
                frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer personalizadorDeSegurancaWeb() {

        return (web -> web.ignoring().requestMatchers(
                "/v2/api-docs",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"));
    }

    @Bean
    @Profile("!prod")
    public CommandLineRunner dadosDeInicializacao(PerfilRepository perfilRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Perfil perfilUsuario = perfilRepository.findByTipoPerfil(TipoPerfil.PERFIL_USUARIO)
                    .orElseGet(() -> {
                        Perfil novoPerfilUsuario = new Perfil(TipoPerfil.PERFIL_USUARIO);
                        return perfilRepository.save(novoPerfilUsuario);
                    });
            Perfil perfilVendedor = perfilRepository.findByTipoPerfil(TipoPerfil.PERFIL_VENDEDOR)
                    .orElseGet(() -> {
                        Perfil novoPerfilVendedor = new Perfil(TipoPerfil.PERFIL_VENDEDOR);
                        return perfilRepository.save(novoPerfilVendedor);
                    });
            Perfil perfilAdministrador = perfilRepository.findByTipoPerfil(TipoPerfil.PERFIL_ADMINISTRADOR)
                    .orElseGet(() -> {
                        Perfil novoPerfilAdministrador = new Perfil(TipoPerfil.PERFIL_ADMINISTRADOR);
                        return perfilRepository.save(novoPerfilAdministrador);
                    });

            Set<Perfil> perfisUsuario = Set.of(perfilUsuario);
            Set<Perfil> perfisVendedor = Set.of(perfilVendedor);
            Set<Perfil> perfisAdministrador = Set.of(perfilUsuario, perfilVendedor, perfilAdministrador);

            if (!usuarioRepository.existsByNomeUsuario("usuario1")) {
                Usuario usuario1 = new Usuario("usuario1", "usuario1@exemplo.com", passwordEncoder.encode("senha1"));
                usuarioRepository.save(usuario1);
            }

            if (!usuarioRepository.existsByNomeUsuario("vendedor1")) {
                Usuario vendedor1 = new Usuario("vendedor1", "vendedor1@exemplo.com", passwordEncoder.encode("senha2"));
                usuarioRepository.save(vendedor1);
            }

            if (!usuarioRepository.existsByNomeUsuario("administrador")) {
                Usuario administrador = new Usuario("administrador", "administrador@exemplo.com", passwordEncoder.encode("senhaAdministrador"));
                usuarioRepository.save(administrador);
            }

            usuarioRepository.findByNomeUsuario("usuario1").ifPresent(usuario -> {
                usuario.setPerfis(perfisUsuario);
                usuarioRepository.save(usuario);
            });

            usuarioRepository.findByNomeUsuario("vendedor1").ifPresent(vendedor -> {
                vendedor.setPerfis(perfisVendedor);
                usuarioRepository.save(vendedor);
            });

            usuarioRepository.findByNomeUsuario("administrador").ifPresent(administrador -> {
                administrador.setPerfis(perfisAdministrador);
                usuarioRepository.save(administrador);
            });

        };
    }
}