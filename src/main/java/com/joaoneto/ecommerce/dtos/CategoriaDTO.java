package com.joaoneto.ecommerce.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {

    @Schema(description = "ID Categoria", example = "101")
    private Long idCategoria;

    @Schema(description = "Nome da categoria que você deseja criar", example = "Notebook GX")
    private String nomeCategoria;

}
