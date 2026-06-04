package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.dtos.ProdutoDTO;
import com.joaoneto.ecommerce.dtos.ProdutoResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProdutoService {

    ProdutoResponseDTO buscarTodosProdutos(Integer numeroPagina, Integer tamanhoPagina, String ordenarPor, String classificarOrdem);

    ProdutoResponseDTO buscarProdutoPorCategoria(Long idCategoria, Integer numeroPagina, Integer tamanhoPagina, String ordenarPor, String classificarOrdem);

    ProdutoResponseDTO buscarProdutoPorPalavraChave(String palavraChave, Integer numeroPagina, Integer tamanhoPagina, String ordenarPor, String classificarOrdem);

    ProdutoDTO criarProduto(Long idCategoria, ProdutoDTO produtoDTO);

    ProdutoDTO atualizarProduto(Long idProduto, ProdutoDTO produtoDTO);

    ProdutoDTO deletarProduto(Long idProduto);

    ProdutoDTO atualizarImagemProduto(Long idProduto, MultipartFile imagem) throws IOException;
}

