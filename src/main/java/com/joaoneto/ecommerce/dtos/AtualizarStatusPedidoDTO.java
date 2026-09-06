package com.joaoneto.ecommerce.dtos;

import com.joaoneto.ecommerce.domain.StatusPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarStatusPedidoDTO {

    @NotNull(message = "O novo status do pedido é obrigatório")
    @Schema(description = "Novo status do pedido", example = "PAGO")
    private StatusPedido novoStatus;
}
