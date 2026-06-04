package com.joaoneto.ecommerce.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImplementacaoArquivoService implements ArquivoService{

    @Override
    public String carregarImagem(String caminho, MultipartFile arquivo) throws IOException {

        String nomeArquivoOriginal = arquivo.getOriginalFilename();

        String idAleatorio = UUID.randomUUID().toString();

        String nomeArquivo = idAleatorio.concat(nomeArquivoOriginal.substring(nomeArquivoOriginal.lastIndexOf('.')));

        String caminhoArquivo = caminho + File.separator + nomeArquivo;

        File pasta = new File(caminho);
        if(!pasta.exists())
            pasta.mkdir();

        Files.copy(arquivo.getInputStream(), Paths.get(caminhoArquivo));

        return nomeArquivo;
    }
}
