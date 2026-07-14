package com.joaoneto.ecommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

    private Long idPedido;
    private String email;
    private List<ItemDoPedidoDTO> itensDoPedido;
    private LocalDate dataDoPedido;
    private PagamentoDTO pagamento;
    private double valorTotal;
    private String statusPedido;
    private EnderecoDTO enderecoDeEntrega;
}
