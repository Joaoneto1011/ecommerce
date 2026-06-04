package com.joaoneto.ecommerce.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ArquivoService {

    String carregarImagem(String caminho, MultipartFile arquivo) throws IOException;
}
