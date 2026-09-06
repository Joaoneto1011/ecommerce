package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.domain.Carrinho;
import com.joaoneto.ecommerce.domain.ItemDoCarrinho;
import com.joaoneto.ecommerce.domain.Produto;
import com.joaoneto.ecommerce.dtos.CarrinhoDTO;
import com.joaoneto.ecommerce.dtos.ItemDoCarrinhoDTO;
import com.joaoneto.ecommerce.dtos.ProdutoDTO;
import com.joaoneto.ecommerce.exceptions.APIException;
import com.joaoneto.ecommerce.exceptions.RecursoNaoEncontradoException;
import com.joaoneto.ecommerce.repositories.CarrinhoRepository;
import com.joaoneto.ecommerce.repositories.ItemDoCarrinhoRepository;
import com.joaoneto.ecommerce.repositories.ProdutoRepository;
import com.joaoneto.ecommerce.util.UtilitarioDeAutenticacao;
import com.joaoneto.ecommerce.util.UtilitarioDeImagem;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ImplementacaoCarrinhoService implements CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;

    private final ProdutoRepository produtoRepository;

    private final ItemDoCarrinhoRepository itemDoCarrinhoRepository;

    private final ModelMapper modelMapper;

    private final UtilitarioDeAutenticacao utilitarioDeAutenticacao;

    private final UtilitarioDeImagem utilitarioDeImagem;

    public ImplementacaoCarrinhoService(CarrinhoRepository carrinhoRepository, ProdutoRepository produtoRepository, ItemDoCarrinhoRepository itemDoCarrinhoRepository, ModelMapper modelMapper, UtilitarioDeAutenticacao utilitarioDeAutenticacao, UtilitarioDeImagem utilitarioDeImagem) {
        this.carrinhoRepository = carrinhoRepository;
        this.produtoRepository = produtoRepository;
        this.itemDoCarrinhoRepository = itemDoCarrinhoRepository;
        this.modelMapper = modelMapper;
        this.utilitarioDeAutenticacao = utilitarioDeAutenticacao;
        this.utilitarioDeImagem = utilitarioDeImagem;
    }

    private ItemDoCarrinhoDTO converterParaItemDoCarrinhoDTO(ItemDoCarrinho item) {
        ItemDoCarrinhoDTO itemDTO = new ItemDoCarrinhoDTO();
        itemDTO.setIdItemDoCarrinho(item.getIdItemDoCarrinho());
        ProdutoDTO produtoDTO = modelMapper.map(item.getProduto(), ProdutoDTO.class);
        produtoDTO.setImagem(utilitarioDeImagem.construirUrl(item.getProduto().getImagem()));
        itemDTO.setProduto(produtoDTO);
        itemDTO.setQuantidade(item.getQuantidade());
        itemDTO.setDesconto(item.getDesconto());
        itemDTO.setPrecoComDesconto(item.getPrecoComDesconto());
        return itemDTO;
    }

    private CarrinhoDTO converterParaCarrinhoDTO(Carrinho carrinho) {
        CarrinhoDTO carrinhoDTO = modelMapper.map(carrinho, CarrinhoDTO.class);
        List<ItemDoCarrinhoDTO> itens = carrinho.getItensDoCarrinho().stream()
                .map(this::converterParaItemDoCarrinhoDTO)
                .toList();
        carrinhoDTO.setItens(itens);
        return carrinhoDTO;
    }

    @Override
    public CarrinhoDTO adicionarProdutoAoCarrinho(Long idProduto, Integer quantidade) {

        Carrinho carrinho = criarCarrinho();

        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "idProduto", idProduto));

        Optional<ItemDoCarrinho> itemDoCarrinho = itemDoCarrinhoRepository.findByCarrinho_IdCarrinhoAndProduto_IdProduto(
                carrinho.getIdCarrinho(),
                idProduto
        );

        if (itemDoCarrinho.isPresent()) {
            throw new APIException("Produto " + produto.getNomeProduto() + " já existe no carrinho.");
        }

        if (produto.getQuantidade() == 0) {
            throw new APIException(produto.getNomeProduto() + " não está disponível.");
        }

        if (produto.getQuantidade() < quantidade) {
            throw new APIException("Por favor, faça um pedido do " + produto.getNomeProduto()
                    + " menor ou igual à quantidade " + produto.getQuantidade() + ".");
        }

        ItemDoCarrinho novoItemDoCarrinho = new ItemDoCarrinho();

        novoItemDoCarrinho.setProduto(produto);
        novoItemDoCarrinho.setCarrinho(carrinho);
        novoItemDoCarrinho.setQuantidade(quantidade);
        novoItemDoCarrinho.setDesconto(produto.getDesconto());
        novoItemDoCarrinho.setPrecoComDesconto(produto.getPrecoEspecial());

        itemDoCarrinhoRepository.save(novoItemDoCarrinho);

        carrinho.getItensDoCarrinho().add(novoItemDoCarrinho);

        produto.setQuantidade(produto.getQuantidade() - quantidade);
        produtoRepository.save(produto);

        BigDecimal subtotal = produto.getPrecoEspecial().multiply(BigDecimal.valueOf(quantidade));
        carrinho.setPrecoTotal(carrinho.getPrecoTotal().add(subtotal));
        carrinhoRepository.save(carrinho);

        return converterParaCarrinhoDTO(carrinho);
    }

    @Override
    public List<CarrinhoDTO> obterTodosCarrinhos() {

        List<Carrinho> carrinhos = carrinhoRepository.findAll();

        if (carrinhos.isEmpty()) {
            throw new APIException("Não há itens no carrinho.");
        }

        return carrinhos.stream()
                .map(this::converterParaCarrinhoDTO)
                .toList();
    }

    @Override
    public CarrinhoDTO obterCarrinho(String email, Long idCarrinho) {

        Carrinho carrinho = carrinhoRepository.findByUsuario_EmailAndIdCarrinho(email, idCarrinho)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Carrinho", "idCarrinho", idCarrinho));

        return converterParaCarrinhoDTO(carrinho);
    }

    @Override
    public CarrinhoDTO atualizarQuantidadeDoProdutoNoCarrinho(Long idProduto, Integer quantidade) {

        String email = utilitarioDeAutenticacao.emailDoUsuarioLogado();

        Carrinho carrinho = carrinhoRepository.findByUsuario_Email(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Carrinho", "email", email));

        Long idCarrinho = carrinho.getIdCarrinho();

        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "idProduto", idProduto));

        if (quantidade > 0 && produto.getQuantidade() < quantidade) {
            throw new APIException("Por favor, faça um pedido do " + produto.getNomeProduto()
                    + " menor ou igual à quantidade " + produto.getQuantidade() + ".");
        }

        ItemDoCarrinho item = itemDoCarrinhoRepository.findByCarrinho_IdCarrinhoAndProduto_IdProduto(idCarrinho, idProduto)
                .orElseThrow(() -> new APIException("Produto " + produto.getNomeProduto() + " não está disponível no carrinho"));

        int novaQuantidade = item.getQuantidade() + quantidade;

        if (novaQuantidade < 0) {
            throw new APIException("A quantidade resultante não pode ser negativa.");
        }

        if (novaQuantidade == 0) {
            deletarProdutoDoCarrinho(idCarrinho, idProduto);
            return obterCarrinho(email, idCarrinho);
        }

        item.setPrecoComDesconto(produto.getPrecoEspecial());
        item.setQuantidade(novaQuantidade);
        item.setDesconto(produto.getDesconto());
        itemDoCarrinhoRepository.save(item);

        produto.setQuantidade(produto.getQuantidade() - quantidade);
        produtoRepository.save(produto);

        BigDecimal variacao = item.getPrecoComDesconto().multiply(BigDecimal.valueOf(quantidade));
        carrinho.setPrecoTotal(carrinho.getPrecoTotal().add(variacao));
        carrinhoRepository.save(carrinho);

        return converterParaCarrinhoDTO(carrinho);
    }

    @Override
    public String deletarProdutoDoCarrinho(Long idCarrinho, Long idProduto) {

        String email = utilitarioDeAutenticacao.emailDoUsuarioLogado();

        Carrinho carrinho = carrinhoRepository.findByUsuario_EmailAndIdCarrinho(email, idCarrinho)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Carrinho", "idCarrinho", idCarrinho));

        ItemDoCarrinho itemDoCarrinho = itemDoCarrinhoRepository.findByCarrinho_IdCarrinhoAndProduto_IdProduto(idCarrinho, idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "idProduto", idProduto));

        BigDecimal valorDoItem = itemDoCarrinho.getPrecoComDesconto().multiply(BigDecimal.valueOf(itemDoCarrinho.getQuantidade()));
        carrinho.setPrecoTotal(carrinho.getPrecoTotal().subtract(valorDoItem));

        carrinho.getItensDoCarrinho().remove(itemDoCarrinho);

        Produto produto = itemDoCarrinho.getProduto();
        produto.setQuantidade(produto.getQuantidade() + itemDoCarrinho.getQuantidade());
        produtoRepository.save(produto);

        carrinhoRepository.save(carrinho);

        return "Produto " + itemDoCarrinho.getProduto().getNomeProduto() + " removido do carrinho !!!";
    }

    @Override
    public void atualizarProdutoEmTodosOsCarrinhos(Long idProduto) {

        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "idProduto", idProduto));

        List<ItemDoCarrinho> itens = itemDoCarrinhoRepository.findByProduto_IdProduto(idProduto);

        for (ItemDoCarrinho item : itens) {
            Carrinho carrinho = item.getCarrinho();

            BigDecimal valorAtualDoItem = item.getPrecoComDesconto().multiply(BigDecimal.valueOf(item.getQuantidade()));
            BigDecimal precoCarrinhoSemItem = carrinho.getPrecoTotal().subtract(valorAtualDoItem);

            item.setDesconto(produto.getDesconto());
            item.setPrecoComDesconto(produto.getPrecoEspecial());

            BigDecimal novoValorDoItem = item.getPrecoComDesconto().multiply(BigDecimal.valueOf(item.getQuantidade()));
            carrinho.setPrecoTotal(precoCarrinhoSemItem.add(novoValorDoItem));

            itemDoCarrinhoRepository.save(item);
            carrinhoRepository.save(carrinho);
        }
    }

    // ImplementacaoCarrinhoService.java
    @Override
    public void limparCarrinhoAposPedido(Long idCarrinho) {
        Carrinho carrinho = carrinhoRepository.findById(idCarrinho)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Carrinho", "idCarrinho", idCarrinho));

        carrinho.getItensDoCarrinho().clear();
        carrinho.setPrecoTotal(BigDecimal.ZERO);
        carrinhoRepository.save(carrinho);
    }

    private Carrinho criarCarrinho() {
        Optional<Carrinho> carrinhoUsuario = carrinhoRepository.findByUsuario_Email(utilitarioDeAutenticacao.emailDoUsuarioLogado());

        if (carrinhoUsuario.isPresent()) {
            return carrinhoUsuario.get();
        }

        Carrinho carrinho = new Carrinho();

        carrinho.setPrecoTotal(BigDecimal.ZERO);
        carrinho.setUsuario(utilitarioDeAutenticacao.usuarioLogado());

        return carrinhoRepository.save(carrinho);
    }
}