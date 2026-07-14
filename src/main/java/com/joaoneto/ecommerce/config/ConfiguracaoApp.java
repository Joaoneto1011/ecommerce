package com.joaoneto.ecommerce.config;

import com.joaoneto.ecommerce.domain.Produto;
import com.joaoneto.ecommerce.dtos.ProdutoDTO;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoApp {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(Produto.class, ProdutoDTO.class)
                .addMapping(Produto::getQuantidade, ProdutoDTO::setQuantidadeEstoque);

        modelMapper.typeMap(ProdutoDTO.class, Produto.class)
                .addMapping(ProdutoDTO::getQuantidadeEstoque, Produto::setQuantidade);

        return modelMapper;
    }
}