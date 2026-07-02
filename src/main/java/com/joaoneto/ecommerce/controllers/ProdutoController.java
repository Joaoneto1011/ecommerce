package com.joaoneto.ecommerce.controllers;

import com.joaoneto.ecommerce.config.ConstantesApp;
import com.joaoneto.ecommerce.dtos.ProdutoDTO;
import com.joaoneto.ecommerce.dtos.RespostaDeProdutoDTO;
import com.joaoneto.ecommerce.services.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/public/produtos")
    public ResponseEntity<RespostaDeProdutoDTO> buscarTodosProdutos(
            @RequestParam(name = "numeroPagina", defaultValue = ConstantesApp.NUMERO_PAGINA, required = false) Integer numeroPagina,
            @RequestParam(name = "tamanhoPagina", defaultValue = ConstantesApp.TAMANHO_PAGINA, required = false) Integer tamanhoPagina,
            @RequestParam(name = "ordenarPorProduto", defaultValue = ConstantesApp.CAMPO_ORDENAR_PRODUTO, required = false) String ordenarPor,
            @RequestParam(name = "classificarOrdem", defaultValue = ConstantesApp.ORDEM_CLASSIFICACAO, required = false) String classificarOrdem)
    {
        RespostaDeProdutoDTO respostaDeProduto = produtoService.buscarTodosProdutos(numeroPagina, tamanhoPagina, ordenarPor, classificarOrdem);
        return new ResponseEntity<>(respostaDeProduto, HttpStatus.OK);
    }

    @GetMapping("/public/categorias/{idCategoria}/produtos")
    public ResponseEntity<RespostaDeProdutoDTO> buscarProdutoPorCategoria(
            @PathVariable Long idCategoria,
            @RequestParam(name = "numeroPagina", defaultValue = ConstantesApp.NUMERO_PAGINA, required = false) Integer numeroPagina,
            @RequestParam(name = "tamanhoPagina", defaultValue = ConstantesApp.TAMANHO_PAGINA, required = false) Integer tamanhoPagina,
            @RequestParam(name = "ordenarPorProduto", defaultValue = ConstantesApp.CAMPO_ORDENAR_PRODUTO, required = false) String ordenarPor,
            @RequestParam(name = "classificarOrdem", defaultValue = ConstantesApp.ORDEM_CLASSIFICACAO, required = false) String classificarOrdem) {

        RespostaDeProdutoDTO respostaDeProduto = produtoService.buscarProdutoPorCategoria(idCategoria, numeroPagina, tamanhoPagina, ordenarPor, classificarOrdem);

        return new ResponseEntity<>(respostaDeProduto, HttpStatus.OK);

    }

    @GetMapping("/public/produtos/palavra-chave/{palavraChave}")
    public ResponseEntity<RespostaDeProdutoDTO> buscarProdutoPorPalavraChave(
            @PathVariable("palavraChave") String palavraChave,
            @RequestParam(name = "numeroPagina", defaultValue = ConstantesApp.NUMERO_PAGINA, required = false) Integer numeroPagina,
            @RequestParam(name = "tamanhoPagina", defaultValue = ConstantesApp.TAMANHO_PAGINA, required = false) Integer tamanhoPagina,
            @RequestParam(name = "ordenarPorProduto", defaultValue = ConstantesApp.CAMPO_ORDENAR_PRODUTO, required = false) String ordenarPor,
            @RequestParam(name = "classificarOrdem", defaultValue = ConstantesApp.ORDEM_CLASSIFICACAO, required = false) String classificarOrdem) {

        RespostaDeProdutoDTO respostaDeProduto = produtoService.buscarProdutoPorPalavraChave(palavraChave, numeroPagina, tamanhoPagina, ordenarPor, classificarOrdem);

        return new ResponseEntity<>(respostaDeProduto, HttpStatus.FOUND);
    }

    @PostMapping("/administrador/categorias/{idCategoria}/produto")
    public ResponseEntity<ProdutoDTO> criarProduto(@Valid @RequestBody ProdutoDTO produtoDTO,
                                                       @PathVariable Long idCategoria){
        ProdutoDTO criarProdutoDTO = produtoService.criarProduto(idCategoria, produtoDTO);

        return new ResponseEntity<>(criarProdutoDTO, HttpStatus.CREATED);
    }

    @PutMapping("/administrador/produtos/{idProduto}")
    public ResponseEntity<ProdutoDTO> atualizarProduto(@Valid @RequestBody ProdutoDTO produtoDTO,
                                                       @PathVariable Long idProduto) {

        ProdutoDTO atualizarProdutoDTO = produtoService.atualizarProduto(idProduto, produtoDTO);

        return new ResponseEntity<>(atualizarProdutoDTO, HttpStatus.OK);
    }

    @DeleteMapping("/administrador/produtos/{idProduto}")
    public ResponseEntity<ProdutoDTO> deletarProduto(@PathVariable Long idProduto) {

        ProdutoDTO deletarProdutoDTO = produtoService.deletarProduto(idProduto);

        return new ResponseEntity<>(deletarProdutoDTO, HttpStatus.OK);
    }

    @PutMapping("/produtos/{idProduto}/imagem")
    public ResponseEntity<ProdutoDTO> atualizarImagemProduto(@PathVariable Long idProduto,
                                                             @RequestParam("imagem")MultipartFile imagem) throws IOException {

        ProdutoDTO atualizarImagemProdutoDTO = produtoService.atualizarImagemProduto(idProduto, imagem);

        return new ResponseEntity<>(atualizarImagemProdutoDTO, HttpStatus.OK);
    }
}
