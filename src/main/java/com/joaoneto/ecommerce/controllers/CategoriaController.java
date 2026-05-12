package com.joaoneto.ecommerce.controllers;

import com.joaoneto.ecommerce.config.ConstantesApp;
import com.joaoneto.ecommerce.dtos.CategoriaDTO;
import com.joaoneto.ecommerce.dtos.CategoriaResponseDTO;
import com.joaoneto.ecommerce.domain.Categoria;
import com.joaoneto.ecommerce.services.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // =======================
    // BUSCAR TODAS CATEGORIAS
    // =======================
    @GetMapping
    public ResponseEntity<CategoriaResponseDTO> buscarTodasCategorias(
            @RequestParam(name = "numeroPagina", defaultValue = ConstantesApp.NUMERO_PAGINA, required = false) Integer numeroPagina,
            @RequestParam(name = "tamanhoPagina", defaultValue = ConstantesApp.TAMANHO_PAGINA, required = false) Integer tamanhoPagina,
            @RequestParam(name = "ordenarPorCategoria", defaultValue = ConstantesApp.ORDENAR_POR_CATEGORIAS, required = false) String ordenarPor,
            @RequestParam(name = "classificarOrdem", defaultValue = ConstantesApp.CLASSIFICAR_ORDEM, required = false) String direcao) {

        return ResponseEntity.ok(categoriaService.buscarTodasCategorias(numeroPagina, tamanhoPagina, ordenarPor, direcao));
    }



    // =======================
    // BUSCAR CATEGORIA POR ID
    // =======================
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> buscarCategoriaPorID(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarCategoriaPorID(id));
    }

    // ======================
    // CRIAR CATEGORIA
    // ======================
    @PostMapping
    public ResponseEntity<CategoriaDTO> criarCategoria(
            @Valid @RequestBody CategoriaDTO categoriaDTO) {

        CategoriaDTO response = categoriaService.criarCategoria(categoriaDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ======================
    // DELETAR CATEGORIA POR ID
    // ======================
    @DeleteMapping("/{id}")
    public ResponseEntity<CategoriaDTO> deletarCategoria(@PathVariable Long id) {

        CategoriaDTO categoriaDeletada = categoriaService.deletarCategoriaPorID(id);
        return ResponseEntity.ok(categoriaDeletada);
    }

    // =======================
    // ATUALIZAR CATEGORIA POR ID
    // =======================
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> atualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaDTO categoriaDTO) {

        return ResponseEntity.ok(categoriaService.atualizarCategoriaPorID(categoriaDTO, id));
    }

}
