package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.domain.Usuario;
import com.joaoneto.ecommerce.dtos.EnderecoDTO;

import java.util.List;

public interface EnderecoService {

    EnderecoDTO criarEndereco(EnderecoDTO enderecoDTO, Usuario usuario);

    List<EnderecoDTO> buscarEndereco();

    EnderecoDTO buscarEnderecoPorId(Long idEndereco);

    List<EnderecoDTO> buscarEnderecoPorUsuario(Usuario usuario);

    EnderecoDTO atualizarEndereco(Long idEndereco, EnderecoDTO enderecoDTO);

    String deletarEndereco(Long idEndereco);
}
