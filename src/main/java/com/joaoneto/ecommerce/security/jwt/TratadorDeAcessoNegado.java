package com.joaoneto.ecommerce.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joaoneto.ecommerce.dtos.RespostaDaAPI;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TratadorDeAcessoNegado implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(TratadorDeAcessoNegado.class);

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest requisicao,
                        HttpServletResponse resposta,
                        AccessDeniedException excecaoDeAcessoNegado) throws IOException, ServletException {
        logger.warn("Acesso negado em {} {}: {}",
                requisicao.getMethod(), requisicao.getRequestURI(), excecaoDeAcessoNegado.getMessage());

        resposta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resposta.setStatus(HttpServletResponse.SC_FORBIDDEN);

        RespostaDaAPI corpoResposta = new RespostaDaAPI(
                "Acesso negado: você não tem permissão para acessar este recurso.",
                false
        );

        mapper.writeValue(resposta.getOutputStream(), corpoResposta);
    }
}
