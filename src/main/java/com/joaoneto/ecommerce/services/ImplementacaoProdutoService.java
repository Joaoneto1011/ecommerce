package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.domain.Carrinho;
import com.joaoneto.ecommerce.domain.Categoria;
import com.joaoneto.ecommerce.domain.Produto;
import com.joaoneto.ecommerce.dtos.ProdutoDTO;
import com.joaoneto.ecommerce.dtos.RespostaDeProdutoDTO;
import com.joaoneto.ecommerce.exceptions.APIException;
import com.joaoneto.ecommerce.exceptions.RecursoNaoEncontradoException;
import com.joaoneto.ecommerce.repositories.CategoriaRepository;
import com.joaoneto.ecommerce.repositories.ProdutoRepository;
import org.modelmapper.ModelMapper;
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

    private final ProdutoRepository produtoRepository;

    private final CategoriaRepository categoriaRepository;

    private final ModelMapper modelMapper;

    private final ArquivoService arquivoService;

    private final String caminho;

    public ImplementacaoProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository, ModelMapper modelMapper, ArquivoService arquivoService, @Value("${aplicacao.caminho.imagens}") String caminho) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.modelMapper = modelMapper;
        this.arquivoService = arquivoService;
        this.caminho = caminho;
    }

    @Override
    public ProdutoDTO criarProduto(Long idCategoria, ProdutoDTO produtoDTO) {

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Categoria", "idCategoria", idCategoria));

        boolean produtoJaExiste = false;

        List<Produto> produtos = categoria.getProdutos();
        for (Produto produto : produtos) {
            if (produto.getNomeProduto().equals(produtoDTO.getNomeProduto())) {
                produtoJaExiste = true;
                break;
            }
        }

        if (produtoJaExiste) {
            throw new APIException("Produto já existe!");
        }

        Produto produto = modelMapper.map(produtoDTO, Produto.class);
        produto.setImagem("default.png");
        produto.setCategoria(categoria);
        double precoEspecial = produto.getPreco() -
                ((produto.getDesconto() * 0.01) * produto.getPreco());
        produto.setPrecoEspecial(precoEspecial);
        Produto produtoSalvo = produtoRepository.save(produto);

        return modelMapper.map(produtoSalvo, ProdutoDTO.class);
    }

    @Override
    public RespostaDeProdutoDTO buscarTodosProdutos(Integer numeroPagina, Integer tamanhoPagina, String ordenarPor, String classificarOrdem) {

        Sort ordenacao = classificarOrdem.equalsIgnoreCase("asc")
                ? Sort.by(ordenarPor).ascending()
                : Sort.by(ordenarPor).descending();

        Pageable detalhesPagina = PageRequest.of(numeroPagina, tamanhoPagina, ordenacao);
        Page<Produto> paginaDeProdutos = produtoRepository.findAll(detalhesPagina);

        List<Produto> produtos = paginaDeProdutos.getContent();

        List<ProdutoDTO> produtoDTOS = produtos.stream()
                .map(produto -> modelMapper.map(produto, ProdutoDTO.class))
                .toList();

        RespostaDeProdutoDTO respostaDeProduto = new RespostaDeProdutoDTO();

        respostaDeProduto.setConteudo(produtoDTOS);
        respostaDeProduto.setNumeroPagina(paginaDeProdutos.getNumber());
        respostaDeProduto.setTamanhoPagina(paginaDeProdutos.getSize());
        respostaDeProduto.setTotalPaginas(paginaDeProdutos.getTotalPages());
        respostaDeProduto.setTotalElementos(paginaDeProdutos.getTotalElements());
        respostaDeProduto.setPaginaFinal(paginaDeProdutos.isLast());
        return respostaDeProduto;
    }

    @Override
    public RespostaDeProdutoDTO buscarProdutoPorCategoria(Long idCategoria, Integer numeroPagina, Integer tamanhoPagina, String ordenarPor, String classificarOrdem) {

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Categoria", "idCategoria", idCategoria));

        Sort ordenacao = classificarOrdem.equalsIgnoreCase("asc")
                ? Sort.by(ordenarPor).ascending()
                : Sort.by(ordenarPor).descending();

        Pageable detalhesPagina = PageRequest.of(numeroPagina, tamanhoPagina, ordenacao);
        Page<Produto> paginaDeProdutos = produtoRepository.findByCategoriaOrderByPrecoAsc(categoria, detalhesPagina);

        List<Produto> produtos = paginaDeProdutos.getContent();

        if (produtos.isEmpty()) {
            throw new APIException("A categoria " + categoria.getNomeCategoria() + " não possui produtos.");
        }

        List<ProdutoDTO> produtoDTOS = produtos.stream()
                .map(produto -> modelMapper.map(produto, ProdutoDTO.class))
                .toList();

        RespostaDeProdutoDTO respostaDeProduto = new RespostaDeProdutoDTO();
        respostaDeProduto.setConteudo(produtoDTOS);
        respostaDeProduto.setNumeroPagina(paginaDeProdutos.getNumber());
        respostaDeProduto.setTamanhoPagina(paginaDeProdutos.getSize());
        respostaDeProduto.setTotalPaginas(paginaDeProdutos.getTotalPages());
        respostaDeProduto.setTotalElementos(paginaDeProdutos.getTotalElements());
        respostaDeProduto.setPaginaFinal(paginaDeProdutos.isLast());
        return respostaDeProduto;
    }

    @Override
    public RespostaDeProdutoDTO buscarProdutoPorPalavraChave(String palavraChave, Integer numeroPagina, Integer tamanhoPagina, String ordenarPor, String classificarOrdem) {

        Sort ordenacao = classificarOrdem.equalsIgnoreCase("asc")
                ? Sort.by(ordenarPor).ascending()
                : Sort.by(ordenarPor).descending();

        Pageable detalhesPagina = PageRequest.of(numeroPagina, tamanhoPagina, ordenacao);
        Page<Produto> paginaDeProdutos = produtoRepository.findByNomeProdutoLikeIgnoreCase('%' + palavraChave + '%', detalhesPagina);

        List<Produto> produtos = paginaDeProdutos.getContent();

        if (produtos.isEmpty()) {
            throw new APIException("Nenhum produto encontrado com a palavra-chave: " + palavraChave);
        }

        List<ProdutoDTO> produtoDTOS = produtos.stream()
                .map(produto -> modelMapper.map(produto, ProdutoDTO.class))
                .toList();

        RespostaDeProdutoDTO respostaDeProduto = new RespostaDeProdutoDTO();
        respostaDeProduto.setConteudo(produtoDTOS);
        respostaDeProduto.setNumeroPagina(paginaDeProdutos.getNumber());
        respostaDeProduto.setTamanhoPagina(paginaDeProdutos.getSize());
        respostaDeProduto.setTotalPaginas(paginaDeProdutos.getTotalPages());
        respostaDeProduto.setTotalElementos(paginaDeProdutos.getTotalElements());
        respostaDeProduto.setPaginaFinal(paginaDeProdutos.isLast());
        return respostaDeProduto;
    }

    @Override
    public ProdutoDTO atualizarProduto(Long idProduto, ProdutoDTO produtoDTO) {

        Produto produtoDoBanco = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "idProduto", idProduto));

        Produto produto = modelMapper.map(produtoDTO, Produto.class);

        produtoDoBanco.setNomeProduto(produto.getNomeProduto());
        produtoDoBanco.setDescricao(produto.getDescricao());
        produtoDoBanco.setQuantidade(produto.getQuantidade());
        produtoDoBanco.setPreco(produto.getPreco());
        produtoDoBanco.setDesconto(produto.getDesconto());
        produtoDoBanco.setPrecoEspecial(produto.getPrecoEspecial());

        Produto produtoSalvo = produtoRepository.save(produtoDoBanco);

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

        Produto produtoDoBanco = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "idProduto", idProduto));

        String nomeArquivo = arquivoService.carregarImagem(caminho, imagem);

        produtoDoBanco.setImagem(nomeArquivo);

        Produto atualizarProduto = produtoRepository.save(produtoDoBanco);

        return modelMapper.map(atualizarProduto, ProdutoDTO.class);

    }

}
