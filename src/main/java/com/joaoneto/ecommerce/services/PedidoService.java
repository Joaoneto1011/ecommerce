package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.dtos.PedidoDTO;
import org.springframework.transaction.annotation.Transactional;

public interface PedidoService {

    @Transactional
    PedidoDTO realizarPedido(String email, Long idEndereco, String metodoDePagamento, String nomeGateway, String idPagamentoGateway, String statusGateway, String mensagemRespostaGateway);
}
