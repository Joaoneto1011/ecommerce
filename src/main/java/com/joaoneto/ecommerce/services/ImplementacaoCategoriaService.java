package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.config.ConstantesApp;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

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

        validarCampoOrdenacao(ordenarPor);

        Sort ordenacao = classificarOrdem.equalsIgnoreCase("asc")
                ? Sort.by(ordenarPor).ascending()
                : Sort.by(ordenarPor).descending();

        Integer tamanhoPaginaSeguro = Math.min(tamanhoPagina, ConstantesApp.TAMANHO_MAXIMO_PAGINA);
        Pageable detalhesPagina = PageRequest.of(numeroPagina, tamanhoPaginaSeguro, ordenacao);
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
    public List<CategoriaDTO> buscarTodasCategoriasSemPaginacao() {

        return categoriaRepository.findAll().stream()
                .map(categoria -> modelMapper.map(categoria, CategoriaDTO.class))
                .toList();
    }

    @Override
    public CategoriaDTO buscarCategoriaPorID(Long id) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", "id", id));

        return modelMapper.map(categoria, CategoriaDTO.class);
    }

    @Override
    @Transactional
    public CategoriaDTO criarCategoria(CategoriaDTO categoriaDTO) {

        if (categoriaRepository.findByNomeCategoria(categoriaDTO.getNomeCategoria()).isPresent()) {
            throw new APIException("Categoria com o nome " + categoriaDTO.getNomeCategoria() + " ja existe!");
        }

        Categoria categoria = modelMapper.map(categoriaDTO, Categoria.class);

        Categoria categoriaSalva = categoriaRepository.save(categoria);

        return modelMapper.map(categoriaSalva, CategoriaDTO.class);
    }

    @Override
    @Transactional
    public String deletarCategoriaPorID(Long id) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", "Id", id));

        categoriaRepository.delete(categoria);

        return "Categoria " + categoria.getNomeCategoria() + " deletada com sucesso !!!";
    }

    @Override
    @Transactional
    public CategoriaDTO atualizarCategoriaPorID(CategoriaDTO categoriaDTO, Long id) {

        Categoria categoria = categoriaRepository.findById(id)
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", "Id", id));

        categoriaRepository.findByNomeCategoria(categoriaDTO.getNomeCategoria())
                .filter(existente -> !existente.getIdCategoria().equals(id))
                .ifPresent(existente -> {
                    throw new APIException("Ja existe uma categoria com esse nome!");
                });

        categoria.setNomeCategoria(categoriaDTO.getNomeCategoria());

        Categoria categoriaAtualizada = categoriaRepository.save(categoria);

        return modelMapper.map(categoriaAtualizada, CategoriaDTO.class);
    }

    private static final Set<String> CAMPOS_ORDENACAO_PERMITIDOS = Set.of("idCategoria", "nomeCategoria");

    private void validarCampoOrdenacao(String campo) {
        if (!CAMPOS_ORDENACAO_PERMITIDOS.contains(campo)) {
            throw new APIException("Campo de ordenação inválido: '" + campo + "'. Utilize um de: " + CAMPOS_ORDENACAO_PERMITIDOS);
        }
    }
}
