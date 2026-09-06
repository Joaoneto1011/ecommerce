package com.joaoneto.ecommerce.controllers;

import com.joaoneto.ecommerce.config.ConstantesApp;
import com.joaoneto.ecommerce.dtos.CategoriaDTO;
import com.joaoneto.ecommerce.dtos.RespostaDaAPI;
import com.joaoneto.ecommerce.dtos.RespostaDeCategoriaDTO;
import com.joaoneto.ecommerce.services.CategoriaService;
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

import java.util.List;

@Tag(name = "APIs de Categoria", description = "APIs para gerenciamento de categorias")
@RestController
@RequestMapping("/api")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(summary = "Buscar todas categorias (público)", description = "API pública para buscar a lista completa de categorias, sem paginação — indicada para menus e filtros")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categorias encontradas com sucesso",
                    content = @Content(array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @Schema(implementation = CategoriaDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @GetMapping("/public/categorias")
    public ResponseEntity<List<CategoriaDTO>> buscarTodasCategoriasPublico() {
        return ResponseEntity.ok(categoriaService.buscarTodasCategoriasSemPaginacao());
    }

    @Operation(summary = "Buscar todas categorias", description = "API para buscar todas as categorias existentes, com suporte a paginação e ordenação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categorias encontradas com sucesso",
                    content = @Content(schema = @Schema(implementation = RespostaDeCategoriaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/categorias")
    public ResponseEntity<RespostaDeCategoriaDTO> buscarTodasCategorias(
            @Parameter(description = "Número da página")
            @RequestParam(name = "numeroPagina", defaultValue = ConstantesApp.NUMERO_PAGINA, required = false) Integer numeroPagina,
            @Parameter(description = "Quantidade de itens por página")
            @RequestParam(name = "tamanhoPagina", defaultValue = ConstantesApp.TAMANHO_PAGINA, required = false) Integer tamanhoPagina,
            @Parameter(description = "Campo utilizado para ordenar o resultado")
            @RequestParam(name = "ordenarPorCategoria", defaultValue = ConstantesApp.CAMPO_ORDERNAR_CATEGORIA, required = false) String ordenarPor,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)")
            @RequestParam(name = "classificarOrdem", defaultValue = ConstantesApp.ORDEM_CLASSIFICACAO, required = false) String classificarOrdem) {

        return ResponseEntity.ok(categoriaService.buscarTodasCategorias(numeroPagina, tamanhoPagina, ordenarPor, classificarOrdem));
    }

    @Operation(summary = "Buscar categoria por ID", description = "API para buscar uma categoria específica pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria encontrada com sucesso",
                    content = @Content(schema = @Schema(implementation = CategoriaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Categoria nao encontrado com id: 101\", \"status\": false}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/categorias/{id}")
    public ResponseEntity<CategoriaDTO> buscarCategoriaPorID(
            @Parameter(description = "ID da categoria que você deseja buscar")
            @PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarCategoriaPorID(id));
    }

    @Operation(summary = "Criar categoria", description = "API para criar uma nova categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso",
                    content = @Content(schema = @Schema(implementation = CategoriaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Categoria já existe com esse nome, ou corpo da requisição inválido",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = {
                                    @ExampleObject(name = "Categoria já existente", value = "{\"mensagem\": \"Categoria com o nome Notebook GX ja existe!\", \"status\": false}"),
                                    @ExampleObject(name = "Erro de validação", value = "{\"mensagem\": \"Erro de validação\", \"status\": false, \"erros\": {\"nomeCategoria\": \"não deve estar em branco\"}}")
                            })),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/categorias")
    public ResponseEntity<CategoriaDTO> criarCategoria(
            @Valid @RequestBody CategoriaDTO categoriaDTO) {

        CategoriaDTO response = categoriaService.criarCategoria(categoriaDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Deletar categoria", description = "API para excluir uma categoria existente pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria deletada com sucesso",
                    content = @Content(examples = @ExampleObject(value = "Categoria Notebook GX deletada com sucesso !!!"))),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Categoria nao encontrado com Id: 101\", \"status\": false}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<String> deletarCategoria(
            @Parameter(description = "ID da categoria que você deseja excluir")
            @PathVariable Long id) {

        String categoriaDeletada = categoriaService.deletarCategoriaPorID(id);
        return ResponseEntity.ok(categoriaDeletada);
    }

    @Operation(summary = "Atualizar categoria", description = "API para atualizar os dados de uma categoria existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = CategoriaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Nome já utilizado por outra categoria, ou corpo da requisição inválido",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = {
                                    @ExampleObject(name = "Nome já em uso", value = "{\"mensagem\": \"Ja existe uma categoria com esse nome!\", \"status\": false}"),
                                    @ExampleObject(name = "Erro de validação", value = "{\"mensagem\": \"Erro de validação\", \"status\": false, \"erros\": {\"nomeCategoria\": \"não deve estar em branco\"}}")
                            })),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Categoria nao encontrado com Id: 101\", \"status\": false}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/categorias/{id}")
    public ResponseEntity<CategoriaDTO> atualizarCategoria(
            @Parameter(description = "ID da categoria que você deseja atualizar")
            @PathVariable Long id,
            @Valid @RequestBody CategoriaDTO categoriaDTO) {

        return ResponseEntity.ok(categoriaService.atualizarCategoriaPorID(categoriaDTO, id));
    }

}