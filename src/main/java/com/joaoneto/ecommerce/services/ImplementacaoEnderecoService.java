package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.domain.Endereco;
import com.joaoneto.ecommerce.domain.Usuario;
import com.joaoneto.ecommerce.dtos.EnderecoDTO;
import com.joaoneto.ecommerce.exceptions.RecursoNaoEncontradoException;
import com.joaoneto.ecommerce.repositories.EnderecoRepository;
import com.joaoneto.ecommerce.repositories.UsuarioRepository;
import com.joaoneto.ecommerce.util.UtilitarioDeAutenticacao;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ImplementacaoEnderecoService implements EnderecoService {

    private final ModelMapper modelMapper;

    private final EnderecoRepository enderecoRepository;

    private final UsuarioRepository usuarioRepository;

    private final UtilitarioDeAutenticacao utilitarioDeAutenticacao;

    public ImplementacaoEnderecoService(ModelMapper modelMapper, EnderecoRepository enderecoRepository,
                                        UsuarioRepository usuarioRepository, UtilitarioDeAutenticacao utilitarioDeAutenticacao) {
        this.modelMapper = modelMapper;
        this.enderecoRepository = enderecoRepository;
        this.usuarioRepository = usuarioRepository;
        this.utilitarioDeAutenticacao = utilitarioDeAutenticacao;
    }

    @Override
    @Transactional
    public EnderecoDTO criarEndereco(EnderecoDTO enderecoDTO, Usuario usuario) {

        Endereco endereco = modelMapper.map(enderecoDTO, Endereco.class);

        List<Endereco> listaEnderecos = usuario.getEnderecos();
        listaEnderecos.add(endereco);
        usuario.setEnderecos(listaEnderecos);

        endereco.setUsuario(usuario);
        Endereco salvarEndereco = enderecoRepository.save(endereco);

        return modelMapper.map(salvarEndereco, EnderecoDTO.class);
    }

    @Override
    public List<EnderecoDTO> buscarEndereco() {

        List<Endereco> enderecos = enderecoRepository.findAll();

        return enderecos.stream()
                .map(endereco -> modelMapper.map(endereco, EnderecoDTO.class))
                .toList();
    }

    @Override
    public EnderecoDTO buscarEnderecoPorId(Long idEndereco) {

        Endereco endereco = enderecoRepository.findById(idEndereco)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereco", "idEndereco", idEndereco));

        validarPropriedadeDoEndereco(endereco, idEndereco);

        return modelMapper.map(endereco, EnderecoDTO.class);
    }

    @Override
    public List<EnderecoDTO> buscarEnderecoPorUsuario(Usuario usuario) {

        List<Endereco> enderecos = usuario.getEnderecos();

        return enderecos.stream()
                .map(endereco -> modelMapper.map(endereco, EnderecoDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public EnderecoDTO atualizarEndereco(Long idEndereco, EnderecoDTO enderecoDTO) {

        Endereco enderecoDoBancoDeDados = enderecoRepository.findById(idEndereco)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereco", "idEndereco", idEndereco));

        validarPropriedadeDoEndereco(enderecoDoBancoDeDados, idEndereco);

        enderecoDoBancoDeDados.setCidade(enderecoDTO.getCidade());
        enderecoDoBancoDeDados.setCep(enderecoDTO.getCep());
        enderecoDoBancoDeDados.setEstado(enderecoDTO.getEstado());
        enderecoDoBancoDeDados.setPais(enderecoDTO.getPais());
        enderecoDoBancoDeDados.setRua(enderecoDTO.getRua());
        enderecoDoBancoDeDados.setNumeroRua(enderecoDTO.getNumeroRua());

        Endereco enderecoAtualizado = enderecoRepository.save(enderecoDoBancoDeDados);

        Usuario usuario = enderecoDoBancoDeDados.getUsuario();

        usuario.getEnderecos().removeIf(endereco -> endereco.getIdEndereco().equals(idEndereco));
        usuario.getEnderecos().add(enderecoAtualizado);
        usuarioRepository.save(usuario);

        return modelMapper.map(enderecoAtualizado, EnderecoDTO.class);
    }

    @Override
    @Transactional
    public String deletarEndereco(Long idEndereco) {

        Endereco enderecoDoBancoDeDados = enderecoRepository.findById(idEndereco)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereco", "idEndereco", idEndereco));

        validarPropriedadeDoEndereco(enderecoDoBancoDeDados, idEndereco);

        Usuario usuario = enderecoDoBancoDeDados.getUsuario();

        usuario.getEnderecos().removeIf(endereco -> endereco.getIdEndereco().equals(idEndereco));
        usuarioRepository.save(usuario);

        enderecoRepository.delete(enderecoDoBancoDeDados);

        return "Endereço deletado com sucesso com o idEndereço: " + idEndereco;
    }

    private void validarPropriedadeDoEndereco(Endereco endereco, Long idEndereco) {
        Usuario usuarioLogado = utilitarioDeAutenticacao.usuarioLogado();

        if (!endereco.getUsuario().getIdUsuario().equals(usuarioLogado.getIdUsuario())) {
            throw new RecursoNaoEncontradoException("Endereco", "idEndereco", idEndereco);
        }
    }
}