package com.joaoneto.ecommerce.controllers;

import com.joaoneto.ecommerce.model.Categoria;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
