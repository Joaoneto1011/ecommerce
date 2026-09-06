package com.joaoneto.ecommerce.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoDTO {

    @Schema(description = "ID do endereço", example = "1")
    private Long idEndereco;

    @NotBlank(message = "A rua não deve estar em branco")
    @Size(min = 5, max = 100, message = "O nome da rua deve conter entre 5 e 100 caracteres")
    @Schema(description = "Nome da rua", example = "Rua das Flores")
    private String rua;

    @NotBlank(message = "O número da residência não deve estar em branco")
    @Size(min = 1, max = 10, message = "O número deve conter entre 1 e 10 caracteres")
    @Schema(description = "Número da residência", example = "123")
    private String numeroRua;

    @NotBlank(message = "A cidade não deve estar em branco")
    @Size(min = 3, max = 60, message = "A cidade deve conter entre 3 e 60 caracteres")
    @Schema(description = "Cidade do endereço", example = "Uberlândia")
    private String cidade;

    @NotBlank(message = "O estado não deve estar em branco")
    @Size(min = 2, max = 30, message = "O estado deve conter entre 2 e 30 caracteres")
    @Schema(description = "Estado do endereço", example = "MG")
    private String estado;

    @NotBlank(message = "O país não deve estar em branco")
    @Size(min = 2, max = 60, message = "O país deve conter entre 2 e 60 caracteres")
    @Schema(description = "País do endereço", example = "Brasil")
    private String pais;

    @NotBlank(message = "O CEP não deve estar em branco")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido. Formato esperado: 00000-000")
    @Schema(description = "CEP do endereço", example = "38400-000")
    private String cep;
}