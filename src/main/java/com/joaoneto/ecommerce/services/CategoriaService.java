package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.dtos.CategoriaDTO;
import com.joaoneto.ecommerce.dtos.RespostaDeCategoriaDTO;

public interface CategoriaService {

    RespostaDeCategoriaDTO buscarTodasCategorias(Integer numeroPagina, Integer tamanhoPagina, String ordenarPor, String classificarOrdem);

    CategoriaDTO buscarCategoriaPorID(Long id);

    CategoriaDTO criarCategoria(CategoriaDTO categoriaDTO);

    String deletarCategoriaPorID(Long id);

    CategoriaDTO atualizarCategoriaPorID(CategoriaDTO categoriaDTO, Long id);

}