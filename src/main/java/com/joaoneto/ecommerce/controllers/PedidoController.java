package com.joaoneto.ecommerce.controllers;

import com.joaoneto.ecommerce.dtos.PedidoDTO;
import com.joaoneto.ecommerce.dtos.RespostaDaAPI;
import com.joaoneto.ecommerce.dtos.SolicitacaoDePedidoDTO;
import com.joaoneto.ecommerce.services.PedidoService;
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

@Tag(name = "APIs de Pedido", description = "APIs para gerenciamento de pedidos")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api")
public class PedidoController {

    private final PedidoService pedidoService;

    private final UtilitarioDeAutenticacao utilitarioDeAutenticacao;

    public PedidoController(PedidoService pedidoService, UtilitarioDeAutenticacao utilitarioDeAutenticacao) {
        this.pedidoService = pedidoService;
        this.utilitarioDeAutenticacao = utilitarioDeAutenticacao;
    }

    @Operation(summary = "Criar pedido", description = "API para finalizar a compra, criando um pedido a partir do carrinho do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso",
                    content = @Content(schema = @Schema(implementation = PedidoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Carrinho vazio",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"O carrinho está vazio\", \"status\": false}"))),
            @ApiResponse(responseCode = "404", description = "Carrinho do usuário ou endereço não encontrado",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = {
                                    @ExampleObject(name = "Carrinho não encontrado", value = "{\"mensagem\": \"Carrinho nao encontrado com email: usuario@exemplo.com\", \"status\": false}"),
                                    @ExampleObject(name = "Endereço não encontrado", value = "{\"mensagem\": \"Endereco nao encontrado com idEndereco: 1\", \"status\": false}")
                            })),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @PostMapping("/pedido/usuarios/pagamentos/{metodoDePagamento}")
    public ResponseEntity<PedidoDTO> criarPedido(
            @Parameter(description = "Método de pagamento utilizado (ex: cartao_credito, pix, boleto)")
            @PathVariable String metodoDePagamento,
            @RequestBody SolicitacaoDePedidoDTO solicitacaoDePedidoDTO) {

        String email = utilitarioDeAutenticacao.emailDoUsuarioLogado();
        PedidoDTO pedido = pedidoService.realizarPedido(
                email,
                solicitacaoDePedidoDTO.getIdEndereco(),
                metodoDePagamento,
                solicitacaoDePedidoDTO.getNomeGateway(),
                solicitacaoDePedidoDTO.getIdPagamentoGateway(),
                solicitacaoDePedidoDTO.getStatusGateway(),
                solicitacaoDePedidoDTO.getMensagemRespostaGateway()
        );

        return new ResponseEntity<>(pedido, HttpStatus.CREATED);
    }
}