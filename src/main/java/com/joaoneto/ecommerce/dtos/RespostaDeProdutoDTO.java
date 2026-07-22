package com.joaoneto.ecommerce.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespostaDeProdutoDTO {

    @Schema(description = "Lista de produtos retornados na página atual")
    private List<ProdutoDTO> conteudo;

    @Schema(description = "Número da página atual", example = "0")
    private Integer numeroPagina;

    @Schema(description = "Quantidade de itens por página", example = "50")
    private Integer tamanhoPagina;

    @Schema(description = "Total de produtos existentes", example = "34")
    private Long totalElementos;

    @Schema(description = "Total de páginas existentes", example = "1")
    private Integer totalPaginas;

    @Schema(description = "Indica se esta é a última página", example = "true")
    private boolean paginaFinal;
}