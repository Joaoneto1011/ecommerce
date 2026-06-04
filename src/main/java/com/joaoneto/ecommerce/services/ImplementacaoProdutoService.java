package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.domain.Categoria;
import com.joaoneto.ecommerce.domain.Produto;
import com.joaoneto.ecommerce.dtos.ProdutoDTO;
import com.joaoneto.ecommerce.dtos.ProdutoResponseDTO;
import com.joaoneto.ecommerce.exceptions.APIException;
import com.joaoneto.ecommerce.exceptions.RecursoNaoEncontradoException;
import com.joaoneto.ecommerce.repositories.CategoriaRepository;
import com.joaoneto.ecommerce.repositories.ProdutoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ImplementacaoProdutoService implements ProdutoService{

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ArquivoService arquivoService;

    @Value("${project.image}")
    private String caminho;

    @Override
    public ProdutoDTO criarProduto(Long idCategoria, ProdutoDTO produtoDTO) {

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Categoria", "idCategoria", idCategoria));

        boolean isProductNotPresent = true;

        List<Produto> produtos = categoria.getProdutos();
        for (Produto value : produtos) {
            if (value.getNomeProduto().equals(produtoDTO.getNomeProduto())) {
                isProductNotPresent = false;
                break;
            }
        }

        if (isProductNotPresent) {

            Produto produto = modelMapper.map(produtoDTO, Produto.class);
            produto.setImagem("default.png");
            produto.setCategoria(categoria);
            double precoEspecial = produto.getPreco() -
                    ((produto.getDesconto() * 0.01) * produto.getPreco());
            produto.setPrecoEspecial(precoEspecial);
            Produto salvarProduto = produtoRepository.save(produto);

            return modelMapper.map(salvarProduto, ProdutoDTO.class);
        } else {
            throw new APIException("Product already exist!!");
        }
    }

    @Override
    public ProdutoResponseDTO buscarTodosProdutos(Integer numeroPagina, Integer tamanhoPagina, String ordenarPor, String classificarOrdem) {

        Sort sortByAndOrder = classificarOrdem.equalsIgnoreCase("asc")
                ? Sort.by(ordenarPor).ascending()
                : Sort.by(ordenarPor).descending();

        Pageable pageDetails = PageRequest.of(numeroPagina, tamanhoPagina, sortByAndOrder);
        Page<Produto> produtoPage = produtoRepository.findAll(pageDetails);

        List<Produto> produtos = produtoPage.getContent();

        List<ProdutoDTO> produtoDTOS = produtos.stream()
                .map(produto -> modelMapper.map(produto, ProdutoDTO.class))
                .toList();

        ProdutoResponseDTO produtoResponse = new ProdutoResponseDTO();

        produtoResponse.setConteudo(produtoDTOS);
        produtoResponse.setNumeroPagina(produtoPage.getNumber());
        produtoResponse.setTamanhoPagina(produtoPage.getSize());
        produtoResponse.setTotalPaginas(produtoPage.getTotalPages());
        produtoResponse.setTotalElementos(produtoPage.getTotalElements());
        produtoResponse.setPaginaFinal(produtoPage.isLast());
        return produtoResponse;
    }

    @Override
    public ProdutoResponseDTO buscarProdutoPorCategoria(Long idCategoria, Integer numeroPagina, Integer tamanhoPagina, String ordenarPor, String classificarOrdem) {

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Categoria", "idCategoria", idCategoria));

        Sort sortByAndOrder = classificarOrdem.equalsIgnoreCase("asc")
                ? Sort.by(ordenarPor).ascending()
                : Sort.by(ordenarPor).descending();

        Pageable pageDetails = PageRequest.of(numeroPagina, tamanhoPagina, sortByAndOrder);
        Page<Produto> produtoPage = produtoRepository.findByCategoriaOrderByPrecoAsc(categoria, pageDetails);

        List<Produto> produtos = produtoPage.getContent();

        List<ProdutoDTO> produtoDTOS = produtos.stream()
                .map(produto -> modelMapper.map(produto, ProdutoDTO.class))
                .toList();

        if(produtos.isEmpty()){
            throw new APIException(categoria.getNomeCategoria() + " categoria does not have any products");
        }

        ProdutoResponseDTO produtoResponse = new ProdutoResponseDTO();
        produtoResponse.setConteudo(produtoDTOS);
        produtoResponse.setNumeroPagina(produtoPage.getNumber());
        produtoResponse.setTamanhoPagina(produtoPage.getSize());
        produtoResponse.setTotalPaginas(produtoPage.getTotalPages());
        produtoResponse.setTotalElementos(produtoPage.getTotalElements());
        produtoResponse.setPaginaFinal(produtoPage.isLast());
        return produtoResponse;
    }

    @Override
    public ProdutoResponseDTO buscarProdutoPorPalavraChave(String keyword, Integer numeroPagina, Integer tamanhoPagina,  String ordenarPor, String classificarOrdem) {

        Sort sortByAndOrder = classificarOrdem.equalsIgnoreCase("asc")
                ? Sort.by(ordenarPor).ascending()
                : Sort.by(ordenarPor).descending();

        Pageable pageDetails = PageRequest.of(numeroPagina, tamanhoPagina, sortByAndOrder);
        Page<Produto> produtoPage = produtoRepository.findByNomeProdutoLikeIgnoreCase('%' + keyword + '%', pageDetails);

        List<Produto> produtos = produtoPage.getContent();

        List<ProdutoDTO> produtoDTOS = produtos.stream()
                .map(produto -> modelMapper.map(produto, ProdutoDTO.class))
                .toList();

        if(produtos.isEmpty()){
            throw new APIException("Products not found with keyword: " + keyword);
        }

        ProdutoResponseDTO produtoResponse = new ProdutoResponseDTO();
        produtoResponse.setConteudo(produtoDTOS);
        produtoResponse.setNumeroPagina(produtoPage.getNumber());
        produtoResponse.setTamanhoPagina(produtoPage.getSize());
        produtoResponse.setTotalPaginas(produtoPage.getTotalPages());
        produtoResponse.setTotalElementos(produtoPage.getTotalElements());
        produtoResponse.setPaginaFinal(produtoPage.isLast());
        return produtoResponse;
    }

    @Override
    public ProdutoDTO atualizarProduto(Long idProduto, ProdutoDTO produtoDTO) {

        Produto produtoFromDb = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "idProduto", idProduto));

        Produto produto = modelMapper.map(produtoDTO, Produto.class);

        produtoFromDb.setNomeProduto(produto.getNomeProduto());
        produtoFromDb.setDescricao(produto.getDescricao());
        produtoFromDb.setQuantidade(produto.getQuantidade());
        produtoFromDb.setPreco(produto.getPreco());
        produtoFromDb.setDesconto(produto.getDesconto());
        produtoFromDb.setPrecoEspecial(produto.getPrecoEspecial());

        Produto produtoSalvo = produtoRepository.save(produtoFromDb);

        return modelMapper.map(produtoSalvo, ProdutoDTO.class);
    }

    @Override
    public ProdutoDTO deletarProduto(Long idProduto) {

        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "idProduto", idProduto));

        produtoRepository.delete(produto);

        return modelMapper.map(produto, ProdutoDTO.class);
    }

    @Override
    public ProdutoDTO atualizarImagemProduto(Long idProduto, MultipartFile imagem) throws IOException {

        Produto produtoFromDb = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "idProduto", idProduto));

        String nomeArquivo = arquivoService.carregarImagem(caminho, imagem);

        produtoFromDb.setImagem(nomeArquivo);

        Produto atualizarProduto = produtoRepository.save(produtoFromDb);

        return modelMapper.map(atualizarProduto, ProdutoDTO.class);

    }

}
