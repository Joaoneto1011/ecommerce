package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.model.Categoria;

import java.util.ArrayList;
import java.util.List;

public interface CategoriaService {

    List<Categoria> buscarTodasCategorias();
    void criarCategoria(Categoria categoria);
}
