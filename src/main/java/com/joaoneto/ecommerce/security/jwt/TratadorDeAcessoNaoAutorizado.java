package com.joaoneto.ecommerce.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joaoneto.ecommerce.dtos.RespostaDaAPI;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TratadorDeAcessoNaoAutorizado implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(TratadorDeAcessoNaoAutorizado.class);

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest requisicao,
                         HttpServletResponse resposta,
                         AuthenticationException excecaoDeAutenticacao) throws IOException, ServletException {
        logger.warn("Requisição não autenticada em {} {}: {}",
                requisicao.getMethod(), requisicao.getRequestURI(), excecaoDeAutenticacao.getMessage());

        resposta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resposta.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        RespostaDaAPI corpoResposta = new RespostaDaAPI(
                "Não autorizado: é necessário estar autenticado para acessar este recurso.",
                false
        );

        mapper.writeValue(resposta.getOutputStream(), corpoResposta);
    }
}
