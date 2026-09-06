package com.joaoneto.ecommerce.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.joaoneto.ecommerce.exceptions.APIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class ImplementacaoArquivoService implements ArquivoService {

    private static final Set<String> EXTENSOES_PERMITIDAS = Set.of(".jpg", ".jpeg", ".png", ".webp", ".gif");

    private static final Set<String> TIPOS_DE_CONTEUDO_PERMITIDOS = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif");

    private final Cloudinary cloudinary;

    public ImplementacaoArquivoService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true));
    }

    @Override
    public String carregarImagem(String pasta, MultipartFile arquivo) throws IOException {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new APIException("Nenhum arquivo de imagem foi enviado.");
        }

        String tipoDeConteudo = arquivo.getContentType();
        if (tipoDeConteudo == null || !TIPOS_DE_CONTEUDO_PERMITIDOS.contains(tipoDeConteudo.toLowerCase(Locale.ROOT))) {
            throw new APIException("Tipo de arquivo não permitido. Envie uma imagem JPG, PNG, WEBP ou GIF.");
        }

        obterExtensaoValidada(arquivo.getOriginalFilename());

        String pastaCloudinary = pasta.replaceAll("[\\\\/]+$", "");
        String idPublico = UUID.randomUUID().toString();

        Map<String, Object> resultado;
        try {
            resultado = cloudinary.uploader().upload(arquivo.getBytes(), ObjectUtils.asMap(
                    "folder", pastaCloudinary,
                    "public_id", idPublico,
                    "resource_type", "image"));
        } catch (RuntimeException excecao) {
            throw new APIException("O arquivo enviado não é uma imagem válida.");
        }

        return (String) resultado.get("secure_url");
    }

    private void obterExtensaoValidada(String nomeArquivoOriginal) {
        if (nomeArquivoOriginal == null || !nomeArquivoOriginal.contains(".")) {
            throw new APIException("O arquivo enviado não possui uma extensão válida.");
        }

        String extensao = nomeArquivoOriginal.substring(nomeArquivoOriginal.lastIndexOf('.')).toLowerCase(Locale.ROOT);

        if (!EXTENSOES_PERMITIDAS.contains(extensao)) {
            throw new APIException("Extensão de arquivo não permitida. Utilize: " + EXTENSOES_PERMITIDAS);
        }
    }
}
