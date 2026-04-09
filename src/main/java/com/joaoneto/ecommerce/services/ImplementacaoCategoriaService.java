package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.model.Categoria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImplementacaoCategoriaService implements  CategoriaService{

    private List<Categoria> categorias = new ArrayList<>();

    @Override
    public List<Categoria> buscarTodasCategorias() {
        return categorias;
    }

    @Override
    public void criarCategoria(Categoria categoria) {
        categorias.add(categoria);
    }
}
