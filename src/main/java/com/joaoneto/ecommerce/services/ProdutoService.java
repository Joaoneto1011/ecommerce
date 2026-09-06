package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.dtos.ProdutoDTO;
import com.joaoneto.ecommerce.dtos.RespostaDeProdutoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProdutoService {

    RespostaDeProdutoDTO buscarTodosProdutos(Integer numeroPagina, Integer tamanhoPagina, String ordenarPor, String classificarOrdem, String palavraChave, String categoria);

    ProdutoDTO buscarProdutoPorId(Long idProduto);

    RespostaDeProdutoDTO buscarProdutoPorCategoria(Long idCategoria, Integer numeroPagina, Integer tamanhoPagina, String ordenarPor, String classificarOrdem);

    RespostaDeProdutoDTO buscarProdutoPorPalavraChave(String palavraChave, Integer numeroPagina, Integer tamanhoPagina, String ordenarPor, String classificarOrdem);

    ProdutoDTO criarProduto(Long idCategoria, ProdutoDTO produtoDTO);

    ProdutoDTO atualizarProduto(Long idProduto, ProdutoDTO produtoDTO);

    String deletarProduto(Long idProduto);

    ProdutoDTO atualizarImagemProduto(Long idProduto, MultipartFile imagem) throws IOException;
}

