package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.model.Categoria;

import java.util.List;

public interface CategoriaService {

    List<Categoria> buscarTodasCategorias();
    Categoria buscarCategoriaPorID(Long id);
    String criarCategoria(Categoria categoria);
    String deletarCategoriaPorID(Long id);
    String atualizarCategoriaPorID(Categoria categoria, Long id);

}