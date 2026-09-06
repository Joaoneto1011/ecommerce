package com.joaoneto.ecommerce.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {

    @Schema(description = "ID Categoria", example = "101")
    private Long idCategoria;

    @NotBlank(message = "O nome da categoria não deve estar em branco")
    @Size(min = 5, max = 50, message = "O nome da categoria deve ter entre 5 e 50 caracteres")
    @Schema(description = "Nome da categoria que você deseja criar", example = "Notebook GX")
    private String nomeCategoria;

}
