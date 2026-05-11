package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.domain.Categoria;
import com.joaoneto.ecommerce.dtos.CategoriaDTO;
import com.joaoneto.ecommerce.dtos.CategoriaResponseDTO;

public interface CategoriaService {

    CategoriaResponseDTO buscarTodasCategorias();

    CategoriaDTO buscarCategoriaPorID(Long id);

    CategoriaDTO criarCategoria(CategoriaDTO categoriaDTO);

    CategoriaDTO deletarCategoriaPorID(Long id);

    CategoriaDTO atualizarCategoriaPorID(CategoriaDTO categoriaDTO, Long id);

}