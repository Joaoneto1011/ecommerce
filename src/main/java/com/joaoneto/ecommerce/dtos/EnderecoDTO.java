package com.joaoneto.ecommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoDTO {

    private Long idEndereco;
    private String rua;
    private String numeroRua;
    private String cidade;
    private String estado;
    private String pais;
    private String cep;
}
