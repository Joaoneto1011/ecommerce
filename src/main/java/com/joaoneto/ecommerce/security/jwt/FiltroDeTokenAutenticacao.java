package com.joaoneto.ecommerce.security.jwt;

import com.joaoneto.ecommerce.security.services.ImplementacaoDetalhesUsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FiltroDeTokenAutenticacao extends OncePerRequestFilter {

    @Autowired
    private UtilitarioJwt utilitarioJwt;

    @Autowired
    private ImplementacaoDetalhesUsuarioService implementacaoDetalhesUsuarioService;

    private static final Logger logger = LoggerFactory.getLogger(FiltroDeTokenAutenticacao.class);

    @Override
    protected void doFilterInternal(HttpServletRequest requisicao,
                                    HttpServletResponse resposta,
                                    FilterChain cadeiaDeFiltros) throws ServletException, IOException {
        logger.debug("FiltroDeTokenAutenticacao chamado para a URI: {}", requisicao.getRequestURI());

        try {
            String jwt = obterTokenDaRequisicao(requisicao);
            if(jwt != null && utilitarioJwt.validarTokenJwt(jwt)) {
                String nomeUsuario = utilitarioJwt.obterNomeUsuarioDoToken(jwt);
                UserDetails detalhesUsuario = implementacaoDetalhesUsuarioService.loadUserByUsername(nomeUsuario);
                UsernamePasswordAuthenticationToken autenticacao = new UsernamePasswordAuthenticationToken(
                        detalhesUsuario, null, detalhesUsuario.getAuthorities()
                );


                autenticacao.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(requisicao));
                SecurityContextHolder.getContext().setAuthentication(autenticacao);
                logger.debug("Perfis obtidos do JWT: {}", detalhesUsuario.getAuthorities());
            }
        } catch (Exception excecao) {
            logger.error("Não foi possível definir a autenticação do usuário: {}", excecao.getMessage());
        }
        cadeiaDeFiltros.doFilter(requisicao, resposta);
    }

    //private String obterTokenDaRequisicao(HttpServletRequest requisicao) {
        //String jwt = utilitarioJwt.obterJwtDosCookies(requisicao);
        //logger.debug("FiltroDeTokenAutenticacao.java: {}", jwt);
        //return jwt;
    //}

    private String obterTokenDaRequisicao(HttpServletRequest requisicao) {
        String jwtDoCookie = utilitarioJwt.obterJwtDosCookies(requisicao);
        if (jwtDoCookie != null) {
            return jwtDoCookie;
        }

        String jwtDoCabecalho = utilitarioJwt.obterJwtDosCabecalhos(requisicao);
        if (jwtDoCabecalho != null) {
            return jwtDoCabecalho;
        }

        return null;
    }
}
