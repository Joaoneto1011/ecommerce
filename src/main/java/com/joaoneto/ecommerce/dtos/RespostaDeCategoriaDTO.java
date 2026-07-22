package com.joaoneto.ecommerce.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespostaDeCategoriaDTO {

    @Schema(description = "Lista de categorias retornadas na página atual")
    private List<CategoriaDTO> conteudo;

    @Schema(description = "Número da página atual", example = "0")
    private Integer numeroPagina;

    @Schema(description = "Quantidade de itens por página", example = "50")
    private Integer tamanhoPagina;

    @Schema(description = "Total de categorias existentes", example = "12")
    private Long totalElementos;

    @Schema(description = "Total de páginas existentes", example = "1")
    private Integer totalPaginas;

    @Schema(description = "Indica se esta é a última página", example = "true")
    private boolean paginaFinal;
}