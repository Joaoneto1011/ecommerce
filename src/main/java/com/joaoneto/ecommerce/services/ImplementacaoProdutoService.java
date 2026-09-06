package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.config.ConstantesApp;
import com.joaoneto.ecommerce.domain.Categoria;
import com.joaoneto.ecommerce.domain.Produto;
import com.joaoneto.ecommerce.dtos.ProdutoDTO;
import com.joaoneto.ecommerce.dtos.RespostaDeProdutoDTO;
import com.joaoneto.ecommerce.exceptions.APIException;
import com.joaoneto.ecommerce.exceptions.RecursoNaoEncontradoException;
import com.joaoneto.ecommerce.repositories.CategoriaRepository;
import com.joaoneto.ecommerce.repositories.ProdutoRepository;
import com.joaoneto.ecommerce.util.UtilitarioDeAutenticacao;
import com.joaoneto.ecommerce.util.UtilitarioDeImagem;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

@Service
public class ImplementacaoProdutoService implements ProdutoService{

    private final ProdutoRepository produtoRepository;

    private final CategoriaRepository categoriaRepository;

    private final ModelMapper modelMapper;

    private final ArquivoService arquivoService;

    private final String caminho;

    private final UtilitarioDeImagem utilitarioDeImagem;

    private final CarrinhoService carrinhoService;

    private final UtilitarioDeAutenticacao utilitarioDeAutenticacao;

    public ImplementacaoProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository, ModelMapper modelMapper, ArquivoService arquivoService, @Value("${aplicacao.caminho.imagens}") String caminho, UtilitarioDeImagem utilitarioDeImagem, CarrinhoService carrinhoService, UtilitarioDeAutenticacao utilitarioDeAutenticacao) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.modelMapper = modelMapper;
        this.arquivoService = arquivoService;
        this.caminho = caminho;
        this.utilitarioDeImagem = utilitarioDeImagem;
        this.carrinhoService = carrinhoService;
        this.utilitarioDeAutenticacao = utilitarioDeAutenticacao;
    }

    @Override
    @Transactional
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
        produto.setUsuario(utilitarioDeAutenticacao.usuarioLogado());
        produto.setPrecoEspecial(calcularPrecoComDesconto(produto.getPreco(), produto.getDesconto()));
        Produto produtoSalvo = produtoRepository.save(produto);

        ProdutoDTO produtoSalvoDTO = modelMapper.map(produtoSalvo, ProdutoDTO.class);
        produtoSalvoDTO.setImagem(utilitarioDeImagem.construirUrl(produtoSalvo.getImagem()));
        return produtoSalvoDTO;
    }

    @Override
    public RespostaDeProdutoDTO buscarTodosProdutos(Integer numeroPagina, Integer tamanhoPagina, String ordenarPor, String classificarOrdem, String palavraChave, String categoria) {

        validarCampoOrdenacao(ordenarPor);

        Sort ordenacao = classificarOrdem.equalsIgnoreCase("asc")
                ? Sort.by(ordenarPor).ascending()
                : Sort.by(ordenarPor).descending();

        Integer tamanhoPaginaSeguro = Math.min(tamanhoPagina, ConstantesApp.TAMANHO_MAXIMO_PAGINA);
        Pageable detalhesPagina = PageRequest.of(numeroPagina, tamanhoPaginaSeguro, ordenacao);
        Specification<Produto> specification = Specification.allOf();

        if (palavraChave != null && !palavraChave.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("nomeProduto")), "%" + palavraChave.toLowerCase() + "%"));
        }

        if (categoria != null && !categoria.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("categoria").get("nomeCategoria")),
                            "%" + categoria.toLowerCase() + "%"));
        }

        Page<Produto> paginaDeProdutos = produtoRepository.findAll(specification, detalhesPagina);

        List<Produto> produtos = paginaDeProdutos.getContent();

        List<ProdutoDTO> produtoDTOS = produtos.stream()
                .map(produto -> {
                    ProdutoDTO produtoDTO = modelMapper.map(produto, ProdutoDTO.class);
                    produtoDTO.setImagem(utilitarioDeImagem.construirUrl(produto.getImagem()));
                    return produtoDTO;
                })
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
    public ProdutoDTO buscarProdutoPorId(Long idProduto) {

        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "idProduto", idProduto));

        ProdutoDTO produtoDTO = modelMapper.map(produto, ProdutoDTO.class);
        produtoDTO.setImagem(utilitarioDeImagem.construirUrl(produto.getImagem()));
        return produtoDTO;
    }

    @Override
    public RespostaDeProdutoDTO buscarProdutoPorCategoria(Long idCategoria, Integer numeroPagina, Integer tamanhoPagina, String ordenarPor, String classificarOrdem) {

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Categoria", "idCategoria", idCategoria));

        validarCampoOrdenacao(ordenarPor);

        Sort ordenacao = classificarOrdem.equalsIgnoreCase("asc")
                ? Sort.by(ordenarPor).ascending()
                : Sort.by(ordenarPor).descending();

        Integer tamanhoPaginaSeguro = Math.min(tamanhoPagina, ConstantesApp.TAMANHO_MAXIMO_PAGINA);
        Pageable detalhesPagina = PageRequest.of(numeroPagina, tamanhoPaginaSeguro, ordenacao);
        Page<Produto> paginaDeProdutos = produtoRepository.findByCategoriaOrderByPrecoAsc(categoria, detalhesPagina);

        List<Produto> produtos = paginaDeProdutos.getContent();

        if (produtos.isEmpty()) {
            throw new APIException("A categoria " + categoria.getNomeCategoria() + " não possui produtos.");
        }

        List<ProdutoDTO> produtoDTOS = produtos.stream()
                .map(produto -> {
                    ProdutoDTO produtoDTO = modelMapper.map(produto, ProdutoDTO.class);
                    produtoDTO.setImagem(utilitarioDeImagem.construirUrl(produto.getImagem()));
                    return produtoDTO;
                })
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

        validarCampoOrdenacao(ordenarPor);

        Sort ordenacao = classificarOrdem.equalsIgnoreCase("asc")
                ? Sort.by(ordenarPor).ascending()
                : Sort.by(ordenarPor).descending();

        Integer tamanhoPaginaSeguro = Math.min(tamanhoPagina, ConstantesApp.TAMANHO_MAXIMO_PAGINA);
        Pageable detalhesPagina = PageRequest.of(numeroPagina, tamanhoPaginaSeguro, ordenacao);
        String palavraChaveEscapada = palavraChave.replace("%", "\\%").replace("_", "\\_");
        Page<Produto> paginaDeProdutos = produtoRepository.findByNomeProdutoLikeIgnoreCase('%' + palavraChaveEscapada + '%', detalhesPagina);

        List<Produto> produtos = paginaDeProdutos.getContent();

        if (produtos.isEmpty()) {
            throw new APIException("Nenhum produto encontrado com a palavra-chave: " + palavraChave);
        }

        List<ProdutoDTO> produtoDTOS = produtos.stream()
                .map(produto -> {
                    ProdutoDTO produtoDTO = modelMapper.map(produto, ProdutoDTO.class);
                    produtoDTO.setImagem(utilitarioDeImagem.construirUrl(produto.getImagem()));
                    return produtoDTO;
                })
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
    @Transactional
    public ProdutoDTO atualizarProduto(Long idProduto, ProdutoDTO produtoDTO) {

        Produto produtoDoBanco = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "idProduto", idProduto));

        Produto produto = modelMapper.map(produtoDTO, Produto.class);

        produtoDoBanco.setNomeProduto(produto.getNomeProduto());
        produtoDoBanco.setDescricao(produto.getDescricao());
        produtoDoBanco.setQuantidade(produto.getQuantidade());
        produtoDoBanco.setPreco(produto.getPreco());
        produtoDoBanco.setDesconto(produto.getDesconto());

        produtoDoBanco.setPrecoEspecial(calcularPrecoComDesconto(produtoDoBanco.getPreco(), produtoDoBanco.getDesconto()));

        Produto produtoSalvo = produtoRepository.save(produtoDoBanco);

        carrinhoService.atualizarProdutoEmTodosOsCarrinhos(produtoSalvo.getIdProduto());

        ProdutoDTO produtoSalvoDTO = modelMapper.map(produtoSalvo, ProdutoDTO.class);
        produtoSalvoDTO.setImagem(utilitarioDeImagem.construirUrl(produtoSalvo.getImagem()));
        return produtoSalvoDTO;
    }

    @Override
    @Transactional
    public String deletarProduto(Long idProduto) {

        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "idProduto", idProduto));

        produtoRepository.delete(produto);

        return "Produto " + produto.getNomeProduto() + " deletado com sucesso !!!";
    }


    @Override
    @Transactional
    public ProdutoDTO atualizarImagemProduto(Long idProduto, MultipartFile imagem) throws IOException {

        Produto produtoDoBanco = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "idProduto", idProduto));

        String nomeArquivo = arquivoService.carregarImagem(caminho, imagem);

        produtoDoBanco.setImagem(nomeArquivo);

        Produto atualizarProduto = produtoRepository.save(produtoDoBanco);

        ProdutoDTO atualizarProdutoDTO = modelMapper.map(atualizarProduto, ProdutoDTO.class);
        atualizarProdutoDTO.setImagem(utilitarioDeImagem.construirUrl(atualizarProduto.getImagem()));
        return atualizarProdutoDTO;

    }

    private static final Set<String> CAMPOS_ORDENACAO_PERMITIDOS =
            Set.of("idProduto", "nomeProduto", "preco", "quantidade", "desconto", "precoEspecial");

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    private void validarCampoOrdenacao(String campo) {
        if (!CAMPOS_ORDENACAO_PERMITIDOS.contains(campo)) {
            throw new APIException("Campo de ordenação inválido: '" + campo + "'. Utilize um de: " + CAMPOS_ORDENACAO_PERMITIDOS);
        }
    }

    private BigDecimal calcularPrecoComDesconto(BigDecimal preco, BigDecimal desconto) {
        BigDecimal descontoSeguro = desconto != null ? desconto : BigDecimal.ZERO;
        BigDecimal fatorDesconto = BigDecimal.ONE.subtract(descontoSeguro.divide(CEM, 4, RoundingMode.HALF_UP));
        return preco.multiply(fatorDesconto).setScale(2, RoundingMode.HALF_UP);
    }

}
