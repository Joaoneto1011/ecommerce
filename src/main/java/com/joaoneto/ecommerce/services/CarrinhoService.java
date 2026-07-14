package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.dtos.CarrinhoDTO;

import java.util.List;

public interface CarrinhoService {

    CarrinhoDTO adicionarProdutoAoCarrinho(Long idProduto, Integer quantidade);

    List<CarrinhoDTO> obterTodosCarrinhos();

    CarrinhoDTO obterCarrinho(String email, Long idCarrinho);

    CarrinhoDTO atualizarQuantidadeDoProdutoNoCarrinho(Long idProduto, Integer quantidade);

    String deletarProdutoDoCarrinho(Long idCarrinho, Long idProduto);

    void atualizarProdutoNosCarrinhos(Long idCarrinho, Long idProduto);
}
