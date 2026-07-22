package com.joaoneto.ecommerce.controllers;

import com.joaoneto.ecommerce.domain.Carrinho;
import com.joaoneto.ecommerce.domain.Usuario;
import com.joaoneto.ecommerce.dtos.CarrinhoDTO;
import com.joaoneto.ecommerce.dtos.RespostaDaAPI;
import com.joaoneto.ecommerce.exceptions.APIException;
import com.joaoneto.ecommerce.exceptions.RecursoNaoEncontradoException;
import com.joaoneto.ecommerce.repositories.CarrinhoRepository;
import com.joaoneto.ecommerce.services.CarrinhoService;
import com.joaoneto.ecommerce.util.UtilitarioDeAutenticacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "APIs de Carrinho", description = "APIs para gerenciamento do carrinho de compras")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api")
public class CarrinhoController {

    private final CarrinhoRepository carrinhoRepository;

    private final UtilitarioDeAutenticacao utilitarioDeAutenticacao;

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoRepository carrinhoRepository, UtilitarioDeAutenticacao utilitarioDeAutenticacao, CarrinhoService carrinhoService) {
        this.carrinhoRepository = carrinhoRepository;
        this.utilitarioDeAutenticacao = utilitarioDeAutenticacao;
        this.carrinhoService = carrinhoService;
    }

    @Operation(summary = "Adicionar produto ao carrinho", description = "API para adicionar um produto com uma determinada quantidade ao carrinho do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto adicionado ao carrinho com sucesso",
                    content = @Content(schema = @Schema(implementation = CarrinhoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Produto já existe no carrinho, indisponível em estoque, ou quantidade solicitada maior que o estoque disponível",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = {
                                    @ExampleObject(name = "Produto já existe no carrinho", value = "{\"mensagem\": \"Produto Notebook GX já existe no carrinho.\", \"status\": false}"),
                                    @ExampleObject(name = "Produto indisponível", value = "{\"mensagem\": \"Notebook GX não está disponível.\", \"status\": false}"),
                                    @ExampleObject(name = "Quantidade maior que o estoque", value = "{\"mensagem\": \"Por favor, faça um pedido do Notebook GX menor ou igual à quantidade 5.\", \"status\": false}")
                            })),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Produto nao encontrado com idProduto: 501\", \"status\": false}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @PostMapping("/carrinhos/produtos/{idProduto}/quantidade/{quantidade}")
    public ResponseEntity<CarrinhoDTO> adicionarProdutoAoCarrinho(
            @Parameter(description = "ID do produto que você deseja adicionar ao carrinho")
            @PathVariable Long idProduto,
            @Parameter(description = "Quantidade do produto que você deseja adicionar")
            @PathVariable Integer quantidade) {
        CarrinhoDTO carrinhoDTO = carrinhoService.adicionarProdutoAoCarrinho(idProduto, quantidade);
        return new ResponseEntity<CarrinhoDTO>(carrinhoDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Buscar todos os carrinhos", description = "API para buscar todos os carrinhos cadastrados no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrinhos encontrados com sucesso",
                    content = @Content(array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @Schema(implementation = CarrinhoDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Não há carrinhos cadastrados",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Não há itens no carrinho.\", \"status\": false}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @GetMapping("/carrinhos")
    public ResponseEntity<List<CarrinhoDTO>> obterCarrinhos() {

        List<CarrinhoDTO> carrinhoDTOS = carrinhoService.obterTodosCarrinhos();

        return new ResponseEntity<List<CarrinhoDTO>>(carrinhoDTOS, HttpStatus.OK);
    }

    @Operation(summary = "Buscar carrinho do usuário logado", description = "API para buscar o carrinho vinculado ao usuário autenticado na requisição")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrinho encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = CarrinhoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado para o usuário logado",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Carrinho nao encontrado com idUsuario: 3\", \"status\": false}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @GetMapping("/carrinhos/usuarios/carrinho")
    public ResponseEntity<CarrinhoDTO> obterCarrinhoPorId() {

        Usuario usuario = utilitarioDeAutenticacao.usuarioLogado();

        Carrinho carrinho = carrinhoRepository.findByUsuario_Email(usuario.getEmail())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Carrinho", "idUsuario", usuario.getIdUsuario()));

        CarrinhoDTO carrinhoDTO = carrinhoService.obterCarrinho(usuario.getEmail(), carrinho.getIdCarrinho());

        return new ResponseEntity<CarrinhoDTO>(carrinhoDTO, HttpStatus.OK);
    }

    @Operation(summary = "Atualizar quantidade de produto no carrinho", description = "API para incrementar ou decrementar em uma unidade a quantidade de um produto no carrinho")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrinho atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = CarrinhoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Operação inválida, quantidade solicitada maior que o estoque, produto indisponível no carrinho, ou resultado da operação seria negativo",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = {
                                    @ExampleObject(name = "Operação inválida", value = "{\"mensagem\": \"Operação inválida. Utilize 'adicionar' ou 'deletar'.\", \"status\": false}"),
                                    @ExampleObject(name = "Quantidade maior que o estoque", value = "{\"mensagem\": \"Por favor, faça um pedido do Notebook GX menor ou igual à quantidade 5.\", \"status\": false}"),
                                    @ExampleObject(name = "Produto não está no carrinho", value = "{\"mensagem\": \"Produto Notebook GX não está disponível no carrinho\", \"status\": false}"),
                                    @ExampleObject(name = "Quantidade resultante negativa", value = "{\"mensagem\": \"A quantidade resultante não pode ser negativa.\", \"status\": false}")
                            })),
            @ApiResponse(responseCode = "404", description = "Carrinho do usuário logado ou produto não encontrado",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Produto nao encontrado com idProduto: 501\", \"status\": false}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @PutMapping("/carrinho/produtos/{idProduto}/quantidade/{operacao}")
    public ResponseEntity<CarrinhoDTO> atualizarProdutoDoCarrinho(
            @Parameter(description = "ID do produto que você deseja atualizar no carrinho")
            @PathVariable Long idProduto,
            @Parameter(description = "Operação desejada: 'adicionar' para incrementar uma unidade, ou 'deletar' para remover uma unidade")
            @PathVariable String operacao) {

        if (!operacao.equalsIgnoreCase("adicionar") && !operacao.equalsIgnoreCase("deletar")) {
            throw new APIException("Operação inválida. Utilize 'adicionar' ou 'deletar'.");
        }

        CarrinhoDTO carrinhoDTO = carrinhoService.atualizarQuantidadeDoProdutoNoCarrinho(idProduto,
                operacao.equalsIgnoreCase("deletar") ? -1 : 1);

        return new ResponseEntity<CarrinhoDTO>(carrinhoDTO, HttpStatus.OK);
    }

    @Operation(summary = "Deletar produto do carrinho", description = "API para remover um produto específico de um carrinho")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto removido do carrinho com sucesso",
                    content = @Content(examples = @ExampleObject(value = "Produto Notebook GX removido do carrinho !!!"))),
            @ApiResponse(responseCode = "404", description = "Carrinho ou produto não encontrado",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = {
                                    @ExampleObject(name = "Carrinho não encontrado", value = "{\"mensagem\": \"Carrinho nao encontrado com idCarrinho: 1\", \"status\": false}"),
                                    @ExampleObject(name = "Produto não encontrado no carrinho", value = "{\"mensagem\": \"Produto nao encontrado com idProduto: 501\", \"status\": false}")
                            })),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @DeleteMapping("/carrinhos/{idCarrinho}/produto/{idProduto}")
    public ResponseEntity<String> deletarProdutoDoCarrinho(
            @Parameter(description = "ID do carrinho que você deseja remover algum produto")
            @PathVariable Long idCarrinho,
            @Parameter(description = "ID do produto que você deseja remover do carrinho")
            @PathVariable Long idProduto) {

        String status = carrinhoService.deletarProdutoDoCarrinho(idCarrinho, idProduto);

        return new ResponseEntity<String>(status, HttpStatus.OK);

    }
}