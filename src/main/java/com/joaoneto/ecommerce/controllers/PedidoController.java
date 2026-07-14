package com.joaoneto.ecommerce.controllers;

import com.joaoneto.ecommerce.dtos.PedidoDTO;
import com.joaoneto.ecommerce.dtos.SolicitacaoDePedidoDTO;
import com.joaoneto.ecommerce.services.PedidoService;
import com.joaoneto.ecommerce.util.UtilitarioDeAutenticacao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PedidoController {

    private final PedidoService pedidoService;

    private final UtilitarioDeAutenticacao utilitarioDeAutenticacao;

    public PedidoController(PedidoService pedidoService, UtilitarioDeAutenticacao utilitarioDeAutenticacao) {
        this.pedidoService = pedidoService;
        this.utilitarioDeAutenticacao = utilitarioDeAutenticacao;
    }

    @PostMapping("/pedido/usuarios/pagamentos/{metodoDePagamento}")
    public ResponseEntity<PedidoDTO> criarPedido(@PathVariable String metodoDePagamento,
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
