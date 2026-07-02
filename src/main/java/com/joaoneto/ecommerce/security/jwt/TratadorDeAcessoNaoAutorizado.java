package com.joaoneto.ecommerce.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.Map;

@Component
public class TratadorDeAcessoNaoAutorizado implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(TratadorDeAcessoNaoAutorizado.class);

    @Override
    public void commence(HttpServletRequest requisicao,
                         HttpServletResponse resposta,
                         AuthenticationException excecaoDeAutenticacao) throws IOException, ServletException {
        logger.error("Erro de autorização: {}", excecaoDeAutenticacao.getMessage());
        resposta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resposta.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        final Map<String, Object> corpoResposta = new HashMap<>();
        corpoResposta.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        corpoResposta.put("erro", "Não autorizado");
        corpoResposta.put("mensagem", excecaoDeAutenticacao.getMessage());
        corpoResposta.put("caminho", requisicao.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(resposta.getOutputStream(), corpoResposta);
    }
}