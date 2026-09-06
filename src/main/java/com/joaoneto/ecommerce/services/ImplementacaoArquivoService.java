package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.exceptions.APIException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ImplementacaoArquivoService implements ArquivoService{

    private static final Set<String> EXTENSOES_PERMITIDAS = Set.of(".jpg", ".jpeg", ".png", ".webp", ".gif");

    private static final Set<String> TIPOS_DE_CONTEUDO_PERMITIDOS = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif");

    @Override
    public String carregarImagem(String caminho, MultipartFile arquivo) throws IOException {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new APIException("Nenhum arquivo de imagem foi enviado.");
        }

        String tipoDeConteudo = arquivo.getContentType();
        if (tipoDeConteudo == null || !TIPOS_DE_CONTEUDO_PERMITIDOS.contains(tipoDeConteudo.toLowerCase(Locale.ROOT))) {
            throw new APIException("Tipo de arquivo não permitido. Envie uma imagem JPG, PNG, WEBP ou GIF.");
        }

        String extensao = obterExtensaoValidada(arquivo.getOriginalFilename());

        String nomeArquivo = UUID.randomUUID().toString().concat(extensao);

        String caminhoArquivo = caminho + File.separator + nomeArquivo;

        File pasta = new File(caminho);
        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        Files.copy(arquivo.getInputStream(), Paths.get(caminhoArquivo));

        return nomeArquivo;
    }

    private String obterExtensaoValidada(String nomeArquivoOriginal) {
        if (nomeArquivoOriginal == null || !nomeArquivoOriginal.contains(".")) {
            throw new APIException("O arquivo enviado não possui uma extensão válida.");
        }

        String extensao = nomeArquivoOriginal.substring(nomeArquivoOriginal.lastIndexOf('.')).toLowerCase(Locale.ROOT);

        if (!EXTENSOES_PERMITIDAS.contains(extensao)) {
            throw new APIException("Extensão de arquivo não permitida. Utilize: " + EXTENSOES_PERMITIDAS);
        }

        return extensao;
    }
}
