package com.joaoneto.ecommerce.controllers;

import com.joaoneto.ecommerce.model.Categoria;
import com.joaoneto.ecommerce.services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;


    @GetMapping("api/public/categorias")
    public List<Categoria> getCategorias() {
        return categoriaService.buscarTodasCategorias();
    }

    @PostMapping("api/public/categorias")
    public String criarCategoria(@RequestBody Categoria categoria) {
        categoriaService.criarCategoria(categoria);
        return "Categoria adicionada com sucesso";
    }

}
