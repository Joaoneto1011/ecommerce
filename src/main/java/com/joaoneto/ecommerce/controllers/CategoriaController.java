package com.joaoneto.ecommerce.controllers;

import com.joaoneto.ecommerce.model.Categoria;
import com.joaoneto.ecommerce.services.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }


    @GetMapping
    public ResponseEntity<List<Categoria>> buscarTodasCategorias() {
        List<Categoria> categorias = categoriaService.buscarTodasCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarCategoriaPorID(@PathVariable Long id) {
        Categoria categoria = categoriaService.buscarCategoriaPorID(id);
        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    public ResponseEntity<String> criarCategoria(@Valid @RequestBody Categoria categoria) {
        String mensagem = categoriaService.criarCategoria(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(mensagem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarCategoria(@PathVariable Long id) {
        String mensagem = categoriaService.deletarCategoriaPorID(id);
        return ResponseEntity.ok(mensagem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody Categoria novaCategoria) {
        String mensagem = categoriaService.atualizarCategoriaPorID(novaCategoria, id);
        return ResponseEntity.ok(mensagem);
    }

}
