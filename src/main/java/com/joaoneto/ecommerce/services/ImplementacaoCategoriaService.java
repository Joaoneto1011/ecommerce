package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.domain.Categoria;
import com.joaoneto.ecommerce.dtos.CategoriaDTO;
import com.joaoneto.ecommerce.dtos.RespostaDeCategoriaDTO;
import com.joaoneto.ecommerce.exceptions.APIException;
import com.joaoneto.ecommerce.exceptions.RecursoNaoEncontradoException;
import com.joaoneto.ecommerce.repositories.CategoriaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImplementacaoCategoriaService implements  CategoriaService{

    private final CategoriaRepository categoriaRepository;

    private final ModelMapper modelMapper;

    public ImplementacaoCategoriaService(CategoriaRepository categoriaRepository, ModelMapper modelMapper) {
        this.categoriaRepository = categoriaRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RespostaDeCategoriaDTO buscarTodasCategorias(Integer numeroPagina, Integer tamanhoPagina, String ordenarPor, String classificarOrdem) {

        Sort ordenacao = classificarOrdem.equalsIgnoreCase("asc")
                ? Sort.by(ordenarPor).ascending()
                : Sort.by(ordenarPor).descending();

        Pageable detalhesPagina = PageRequest.of(numeroPagina, tamanhoPagina, ordenacao);
        Page<Categoria> paginaDeCategorias = categoriaRepository.findAll(detalhesPagina);

        List<Categoria> categorias = paginaDeCategorias.getContent();

        List<CategoriaDTO> dtos = categorias.stream()
                .map(categoria -> modelMapper.map(categoria, CategoriaDTO.class))
                .toList();

        RespostaDeCategoriaDTO resposta = new RespostaDeCategoriaDTO();

        resposta.setConteudo(dtos);

        resposta.setNumeroPagina(paginaDeCategorias.getNumber());

        resposta.setTamanhoPagina(paginaDeCategorias.getSize());

        resposta.setTotalElementos(paginaDeCategorias.getTotalElements());

        resposta.setTotalPaginas(paginaDeCategorias.getTotalPages());

        resposta.setPaginaFinal(paginaDeCategorias.isLast());

        return resposta;
    }

    @Override
    public CategoriaDTO buscarCategoriaPorID(Long id) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", "id", id));

        return modelMapper.map(categoria, CategoriaDTO.class);
    }

    @Override
    public CategoriaDTO criarCategoria(CategoriaDTO categoriaDTO) {

        Categoria existente = categoriaRepository
                .findByNomeCategoria(categoriaDTO.getNomeCategoria());

        if (existente != null) {
            throw new APIException(
                    "Categoria com o nome "
                            + categoriaDTO.getNomeCategoria()
                            + " ja existe!"
            );
        }
        Categoria categoria = modelMapper.map(categoriaDTO, Categoria.class);

        Categoria categoriaSalva = categoriaRepository.save(categoria);

        return modelMapper.map(categoriaSalva, CategoriaDTO.class);
    }

    @Override
    public CategoriaDTO deletarCategoriaPorID(Long id) {

        Categoria categoria = categoriaRepository.findById(id)
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", "Id", id));

        categoriaRepository.delete(categoria);

        return modelMapper.map(categoria, CategoriaDTO.class);
    }

    @Override
    public CategoriaDTO atualizarCategoriaPorID(CategoriaDTO categoriaDTO, Long id) {

        Categoria categoria = categoriaRepository.findById(id)
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", "Id", id));

        Categoria existente = categoriaRepository.findByNomeCategoria(categoriaDTO.getNomeCategoria());

        if (existente != null && !existente.getIdCategoria().equals(id)) {
            throw new APIException("Ja existe uma categoria com esse nome!");
        }

        categoria.setNomeCategoria(categoriaDTO.getNomeCategoria());

        Categoria categoriaAtualizada = categoriaRepository.save(categoria);

        return modelMapper.map(categoriaAtualizada, CategoriaDTO.class);
    }
}
