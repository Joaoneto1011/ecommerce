package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.dtos.CategoriaDTO;
import com.joaoneto.ecommerce.dtos.RespostaDeCategoriaDTO;

import java.util.List;

public interface CategoriaService {

    RespostaDeCategoriaDTO buscarTodasCategorias(Integer numeroPagina, Integer tamanhoPagina, String ordenarPor, String classificarOrdem);

    List<CategoriaDTO> buscarTodasCategoriasSemPaginacao();

    CategoriaDTO buscarCategoriaPorID(Long id);

    CategoriaDTO criarCategoria(CategoriaDTO categoriaDTO);

    String deletarCategoriaPorID(Long id);

    CategoriaDTO atualizarCategoriaPorID(CategoriaDTO categoriaDTO, Long id);

}