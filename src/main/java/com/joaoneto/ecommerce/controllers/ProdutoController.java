package com.joaoneto.ecommerce.controllers;

import com.joaoneto.ecommerce.config.ConstantesApp;
import com.joaoneto.ecommerce.dtos.ProdutoDTO;
import com.joaoneto.ecommerce.dtos.RespostaDaAPI;
import com.joaoneto.ecommerce.dtos.RespostaDeProdutoDTO;
import com.joaoneto.ecommerce.services.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "APIs de Produto", description = "APIs para gerenciamento e consulta de produtos")
@RestController
@RequestMapping("/api")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @Operation(summary = "Buscar todos os produtos", description = "API pública para buscar todos os produtos, com suporte a paginação e ordenação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso",
                    content = @Content(schema = @Schema(implementation = RespostaDeProdutoDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @GetMapping("/public/produtos")
    public ResponseEntity<RespostaDeProdutoDTO> buscarTodosProdutos(

            @Parameter(description = "Palavra chave para filtrar")
            @RequestParam(name = "palavraChave", required = false) String palavraChave,
            @Parameter(description = "Categoria do Produto")
            @RequestParam(name = "categoria", required = false) String categoria,
            @Parameter(description = "Número da página")
            @RequestParam(name = "numeroPagina", defaultValue = ConstantesApp.NUMERO_PAGINA, required = false) Integer numeroPagina,
            @Parameter(description = "Quantidade de itens por página")
            @RequestParam(name = "tamanhoPagina", defaultValue = ConstantesApp.TAMANHO_PAGINA, required = false) Integer tamanhoPagina,
            @Parameter(description = "Campo utilizado para ordenar o resultado")
            @RequestParam(name = "ordenarPorProduto", defaultValue = ConstantesApp.CAMPO_ORDENAR_PRODUTO, required = false) String ordenarPor,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)")
            @RequestParam(name = "classificarOrdem", defaultValue = ConstantesApp.ORDEM_CLASSIFICACAO, required = false) String classificarOrdem)
    {
        RespostaDeProdutoDTO respostaDeProduto = produtoService.buscarTodosProdutos(numeroPagina, tamanhoPagina, ordenarPor, classificarOrdem, palavraChave, categoria);
        return new ResponseEntity<>(respostaDeProduto, HttpStatus.OK);
    }

    @Operation(summary = "Buscar produto por ID", description = "API pública para buscar um produto específico pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProdutoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Produto nao encontrado com idProduto: 501\", \"status\": false}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @GetMapping("/public/produtos/{idProduto}")
    public ResponseEntity<ProdutoDTO> buscarProdutoPorId(
            @Parameter(description = "ID do produto que você deseja buscar")
            @PathVariable Long idProduto) {

        ProdutoDTO produtoDTO = produtoService.buscarProdutoPorId(idProduto);

        return new ResponseEntity<>(produtoDTO, HttpStatus.OK);
    }

    @Operation(summary = "Buscar produtos por categoria", description = "API pública para buscar todos os produtos de uma categoria específica")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso",
                    content = @Content(schema = @Schema(implementation = RespostaDeProdutoDTO.class))),
            @ApiResponse(responseCode = "400", description = "A categoria não possui produtos cadastrados",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"A categoria Notebook GX não possui produtos.\", \"status\": false}"))),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Categoria nao encontrado com idCategoria: 101\", \"status\": false}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @GetMapping("/public/categorias/{idCategoria}/produtos")
    public ResponseEntity<RespostaDeProdutoDTO> buscarProdutoPorCategoria(
            @Parameter(description = "ID da categoria que você deseja consultar")
            @PathVariable Long idCategoria,
            @Parameter(description = "Número da página")
            @RequestParam(name = "numeroPagina", defaultValue = ConstantesApp.NUMERO_PAGINA, required = false) Integer numeroPagina,
            @Parameter(description = "Quantidade de itens por página")
            @RequestParam(name = "tamanhoPagina", defaultValue = ConstantesApp.TAMANHO_PAGINA, required = false) Integer tamanhoPagina,
            @Parameter(description = "Campo utilizado para ordenar o resultado")
            @RequestParam(name = "ordenarPorProduto", defaultValue = ConstantesApp.CAMPO_ORDENAR_PRODUTO, required = false) String ordenarPor,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)")
            @RequestParam(name = "classificarOrdem", defaultValue = ConstantesApp.ORDEM_CLASSIFICACAO, required = false) String classificarOrdem) {

        RespostaDeProdutoDTO respostaDeProduto = produtoService.buscarProdutoPorCategoria(idCategoria, numeroPagina, tamanhoPagina, ordenarPor, classificarOrdem);

        return new ResponseEntity<>(respostaDeProduto, HttpStatus.OK);

    }

    @Operation(summary = "Buscar produtos por palavra-chave", description = "API pública para buscar produtos cujo nome contenha a palavra-chave informada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso",
                    content = @Content(schema = @Schema(implementation = RespostaDeProdutoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Nenhum produto encontrado com a palavra-chave informada",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Nenhum produto encontrado com a palavra-chave: notebook\", \"status\": false}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @GetMapping("/public/produtos/palavra-chave/{palavraChave}")
    public ResponseEntity<RespostaDeProdutoDTO> buscarProdutoPorPalavraChave(
            @Parameter(description = "Palavra-chave utilizada para buscar produtos pelo nome")
            @PathVariable("palavraChave") String palavraChave,
            @Parameter(description = "Número da página")
            @RequestParam(name = "numeroPagina", defaultValue = ConstantesApp.NUMERO_PAGINA, required = false) Integer numeroPagina,
            @Parameter(description = "Quantidade de itens por página")
            @RequestParam(name = "tamanhoPagina", defaultValue = ConstantesApp.TAMANHO_PAGINA, required = false) Integer tamanhoPagina,
            @Parameter(description = "Campo utilizado para ordenar o resultado")
            @RequestParam(name = "ordenarPorProduto", defaultValue = ConstantesApp.CAMPO_ORDENAR_PRODUTO, required = false) String ordenarPor,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)")
            @RequestParam(name = "classificarOrdem", defaultValue = ConstantesApp.ORDEM_CLASSIFICACAO, required = false) String classificarOrdem) {

        RespostaDeProdutoDTO respostaDeProduto = produtoService.buscarProdutoPorPalavraChave(palavraChave, numeroPagina, tamanhoPagina, ordenarPor, classificarOrdem);

        return new ResponseEntity<>(respostaDeProduto, HttpStatus.OK);
    }

    @Operation(summary = "Criar produto", description = "API para cadastrar um novo produto vinculado a uma categoria (requer perfil de administrador)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProdutoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Produto já existe nessa categoria, ou corpo da requisição inválido",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = {
                                    @ExampleObject(name = "Produto já existente", value = "{\"mensagem\": \"Produto já existe!\", \"status\": false}"),
                                    @ExampleObject(name = "Erro de validação", value = "{\"mensagem\": \"Erro de validação\", \"status\": false, \"erros\": {\"nomeProduto\": \"não deve estar em branco\", \"preco\": \"deve ser maior que zero\"}}")
                            })),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Categoria nao encontrado com idCategoria: 101\", \"status\": false}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/administrador/categorias/{idCategoria}/produto")
    public ResponseEntity<ProdutoDTO> criarProduto(
            @Valid @RequestBody ProdutoDTO produtoDTO,
            @Parameter(description = "ID da categoria à qual o produto será vinculado")
            @PathVariable Long idCategoria){
        ProdutoDTO criarProdutoDTO = produtoService.criarProduto(idCategoria, produtoDTO);

        return new ResponseEntity<>(criarProdutoDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Atualizar produto", description = "API para atualizar os dados de um produto existente (requer perfil de administrador)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProdutoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Corpo da requisição inválido: um ou mais campos não passaram na validação",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Erro de validação\", \"status\": false, \"erros\": {\"nomeProduto\": \"não deve estar em branco\"}}"))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Produto nao encontrado com idProduto: 501\", \"status\": false}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/administrador/produtos/{idProduto}")
    public ResponseEntity<ProdutoDTO> atualizarProduto(
            @Valid @RequestBody ProdutoDTO produtoDTO,
            @Parameter(description = "ID do produto que você deseja atualizar")
            @PathVariable Long idProduto) {

        ProdutoDTO atualizarProdutoDTO = produtoService.atualizarProduto(idProduto, produtoDTO);

        return new ResponseEntity<>(atualizarProdutoDTO, HttpStatus.OK);
    }

    @Operation(summary = "Deletar produto", description = "API para excluir um produto existente (requer perfil de administrador)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto deletado com sucesso",
                    content = @Content(examples = @ExampleObject(value = "Produto Notebook GX deletado com sucesso !!!"))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Produto nao encontrado com idProduto: 501\", \"status\": false}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/administrador/produtos/{idProduto}")
    public ResponseEntity<String> deletarProduto(
            @Parameter(description = "ID do produto que você deseja excluir")
            @PathVariable Long idProduto) {

        String produtoDeletado = produtoService.deletarProduto(idProduto);

        return new ResponseEntity<>(produtoDeletado, HttpStatus.OK);
    }

    @Operation(summary = "Atualizar imagem do produto", description = "API para atualizar a imagem de um produto existente (requer perfil de administrador)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagem do produto atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = ProdutoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Produto nao encontrado com idProduto: 501\", \"status\": false}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao processar a imagem", content = @Content),
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/administrador/produtos/{idProduto}/imagem")
    public ResponseEntity<ProdutoDTO> atualizarImagemProduto(
            @Parameter(description = "ID do produto que você deseja atualizar a imagem")
            @PathVariable Long idProduto,
            @Parameter(description = "Arquivo de imagem a ser enviado")
            @RequestParam("imagem")MultipartFile imagem) throws IOException {

        ProdutoDTO atualizarImagemProdutoDTO = produtoService.atualizarImagemProduto(idProduto, imagem);

        return new ResponseEntity<>(atualizarImagemProdutoDTO, HttpStatus.OK);
    }
}