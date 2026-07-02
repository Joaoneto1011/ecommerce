package com.joaoneto.ecommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespostaDeProdutoDTO {

    private List<ProdutoDTO> conteudo;
    private Integer numeroPagina;
    private Integer tamanhoPagina;
    private Long totalElementos;
    private Integer totalPaginas;
    private boolean paginaFinal;
}
