package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.domain.StatusPedido;
import com.joaoneto.ecommerce.dtos.PedidoDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PedidoService {

    @Transactional
    PedidoDTO realizarPedido(String email, Long idEndereco, String metodoDePagamento, String nomeGateway, String idPagamentoGateway, String statusGateway, String mensagemRespostaGateway);

    List<PedidoDTO> buscarPedidosDoUsuarioLogado(String email);

    List<PedidoDTO> listarTodosPedidos();

    @Transactional
    PedidoDTO atualizarStatusPedido(Long idPedido, StatusPedido novoStatus);
}
