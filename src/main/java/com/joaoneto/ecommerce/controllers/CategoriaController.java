package com.joaoneto.ecommerce.controllers;

import com.joaoneto.ecommerce.model.Categoria;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
public class CategoriaController {

    private List<Categoria> categorias = new ArrayList<>();

    @GetMapping("api/public/categorias")
    public List<Categoria> getCategorias() {
        return categorias;
    }

    @PostMapping("api/public/categorias")
    public String criarCategoria(@RequestBody Categoria categoria) {
        categorias.add(categoria);
        return "Categoria adicionada com sucesso";
    }

}
