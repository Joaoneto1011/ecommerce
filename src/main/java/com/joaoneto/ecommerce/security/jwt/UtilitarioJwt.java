package com.joaoneto.ecommerce.security.jwt;

import com.joaoneto.ecommerce.security.services.ImplementacaoDetalhesUsuario;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class UtilitarioJwt {

    private static final Logger logger = LoggerFactory.getLogger((UtilitarioJwt.class));

    @Value("${aplicacao.jwt.segredo}")
    private String jwtSecret;

    @Value("${aplicacao.jwt.expiracao-ms}")
    private int jwtExpirationMs;

    @Value("${aplicacao.jwt.nome-cookie}")
    private String jwtCookie;

    public String obterJwtDosCookies(HttpServletRequest requisicao) {
        Cookie cookie = WebUtils.getCookie(requisicao, jwtCookie);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    public String obterJwtDosCabecalhos(HttpServletRequest requisicao) {
        String bearerToken = requisicao.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    public ResponseCookie gerarCookieJwt(ImplementacaoDetalhesUsuario usuarioPrincipal) {
        String jwt = gerarTokenComNomeUsuario(usuarioPrincipal.getNomeUsuario());
        return ResponseCookie.from(jwtCookie, jwt)
                .path("/api")
                .maxAge(24 * 60 * 60)
                .httpOnly(false)
                .build();
    }

    public ResponseCookie obterCookieJwtLimpo() {
        return ResponseCookie.from(jwtCookie, null)
                .path("/api")
                .build();
    }

    public String gerarTokenComNomeUsuario(String nomeUsuario) {
        return Jwts.builder()
                .subject(nomeUsuario)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + jwtExpirationMs)))
                .signWith(chave())
                .compact();
    }

    public String obterNomeUsuarioDoToken(String token) {
        return Jwts.parser()
                .verifyWith(chave())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public SecretKey chave() {
        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public boolean validarTokenJwt(String tokenAutenticacao) {
        try {
            Jwts.parser()
                    .verifyWith(chave())
                    .build()
                    .parseSignedClaims(tokenAutenticacao);
            return true;
        } catch (MalformedJwtException excecao) {
            logger.error("Token JWT inválido: {}", excecao.getMessage());
        } catch (ExpiredJwtException excecao) {
            logger.error("Token JWT expirado: {}", excecao.getMessage());
        } catch (UnsupportedJwtException excecao) {
            logger.error("Token JWT não suportado: {}", excecao.getMessage());
        } catch (IllegalArgumentException excecao) {
            logger.error("Claims do JWT vazias: {}", excecao.getMessage());
        }
        return false;
    }
}
