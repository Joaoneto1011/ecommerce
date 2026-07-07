package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.domain.Carrinho;
import com.joaoneto.ecommerce.domain.ItemDoCarrinho;
import com.joaoneto.ecommerce.domain.Produto;
import com.joaoneto.ecommerce.dtos.CarrinhoDTO;
import com.joaoneto.ecommerce.dtos.ProdutoDTO;
import com.joaoneto.ecommerce.exceptions.APIException;
import com.joaoneto.ecommerce.exceptions.RecursoNaoEncontradoException;
import com.joaoneto.ecommerce.repositories.CarrinhoRepository;
import com.joaoneto.ecommerce.repositories.ItemDoCarrinhoRepository;
import com.joaoneto.ecommerce.repositories.ProdutoRepository;
import com.joaoneto.ecommerce.util.UtilitarioDeAutenticacao;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public ImplementacaoCarrinhoService(CarrinhoRepository carrinhoRepository, ProdutoRepository produtoRepository, ItemDoCarrinhoRepository itemDoCarrinhoRepository, ModelMapper modelMapper, UtilitarioDeAutenticacao utilitarioDeAutenticacao) {
        this.carrinhoRepository = carrinhoRepository;
        this.produtoRepository = produtoRepository;
        this.itemDoCarrinhoRepository = itemDoCarrinhoRepository;
        this.modelMapper = modelMapper;
        this.utilitarioDeAutenticacao = utilitarioDeAutenticacao;
    }

    private ProdutoDTO converterParaProdutoDTO(ItemDoCarrinho item) {
        ProdutoDTO produtoDTO = modelMapper.map(item.getProduto(), ProdutoDTO.class);
        produtoDTO.setQuantidade(item.getQuantidade());
        return produtoDTO;
    }

    private CarrinhoDTO converterParaCarrinhoDTO(Carrinho carrinho) {
        CarrinhoDTO carrinhoDTO = modelMapper.map(carrinho, CarrinhoDTO.class);
        List<ProdutoDTO> produtos = carrinho.getItensDoCarrinho().stream()
                .map(this::converterParaProdutoDTO)
                .toList();
        carrinhoDTO.setProdutos(produtos);
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
        novoItemDoCarrinho.setPrecoProduto(produto.getPrecoEspecial());

        itemDoCarrinhoRepository.save(novoItemDoCarrinho);

        carrinho.getItensDoCarrinho().add(novoItemDoCarrinho);

        produto.setQuantidade(produto.getQuantidade() - quantidade);
        produtoRepository.save(produto);

        carrinho.setPrecoTotal(carrinho.getPrecoTotal() + (produto.getPrecoEspecial() * quantidade));
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
    public CarrinhoDTO obterCarrinho(String idEmail, Long idCarrinho) {

        Carrinho carrinho = carrinhoRepository.findByUsuario_EmailAndIdCarrinho(idEmail, idCarrinho)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Carrinho", "idCarrinho", idCarrinho));

        return converterParaCarrinhoDTO(carrinho);
    }

    @Override
    public CarrinhoDTO atualizarQuantidadeDoProdutoNoCarrinho(Long idProduto, Integer quantidade) {

        String idEmail = utilitarioDeAutenticacao.emailDoUsuarioLogado();

        Carrinho carrinho = carrinhoRepository.findByUsuario_Email(idEmail)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Carrinho", "email", idEmail));

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
            return obterCarrinho(idEmail, idCarrinho);
        }

        item.setPrecoProduto(produto.getPrecoEspecial());
        item.setQuantidade(novaQuantidade);
        item.setDesconto(produto.getDesconto());
        itemDoCarrinhoRepository.save(item);

        produto.setQuantidade(produto.getQuantidade() - quantidade);
        produtoRepository.save(produto);

        carrinho.setPrecoTotal(carrinho.getPrecoTotal() + (item.getPrecoProduto() * quantidade));
        carrinhoRepository.save(carrinho);

        return converterParaCarrinhoDTO(carrinho);
    }

    @Override
    public String deletarProdutoDoCarrinho(Long idCarrinho, Long idProduto) {

        Carrinho carrinho = carrinhoRepository.findById(idCarrinho)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Carrinho", "idCarrinho", idCarrinho));

        ItemDoCarrinho itemDoCarrinho = itemDoCarrinhoRepository.findByCarrinho_IdCarrinhoAndProduto_IdProduto(idCarrinho, idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "idProduto", idProduto));

        carrinho.setPrecoTotal(carrinho.getPrecoTotal() -
                (itemDoCarrinho.getPrecoProduto() * itemDoCarrinho.getQuantidade()));

        carrinho.getItensDoCarrinho().remove(itemDoCarrinho);

        carrinhoRepository.save(carrinho);

        return "Produto " + itemDoCarrinho.getProduto().getNomeProduto() + " removido do carrinho !!!";
    }

    @Override
    public void atualizarProdutoNosCarrinhos(Long idCarrinho, Long idProduto) {

        Carrinho carrinho = carrinhoRepository.findById(idCarrinho)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Carrinho", "idCarrinho", idCarrinho));

        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "idProduto", idProduto));

        ItemDoCarrinho itemDoCarrinho = itemDoCarrinhoRepository.findByCarrinho_IdCarrinhoAndProduto_IdProduto(idCarrinho, idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", "idProduto", idProduto));

        if (itemDoCarrinho == null) {
            throw new APIException("Produto " + produto.getNomeProduto() + "não está disponível no carrinho!!! ");
        }

        double precoCarrinho = carrinho.getPrecoTotal() -
                (itemDoCarrinho.getPrecoProduto() * itemDoCarrinho.getQuantidade());

        itemDoCarrinho.setPrecoProduto(produto.getPrecoEspecial());

        carrinho.setPrecoTotal(precoCarrinho +
                (itemDoCarrinho.getPrecoProduto() * itemDoCarrinho.getQuantidade()));

        itemDoCarrinho = itemDoCarrinhoRepository.save(itemDoCarrinho);
    }

    private Carrinho criarCarrinho() {
        Optional<Carrinho> carrinhoUsuario = carrinhoRepository.findByUsuario_Email(utilitarioDeAutenticacao.emailDoUsuarioLogado());

        if (carrinhoUsuario.isPresent()) {
            return carrinhoUsuario.get();
        }

        Carrinho carrinho = new Carrinho();

        carrinho.setPrecoTotal(0.00);
        carrinho.setUsuario(utilitarioDeAutenticacao.usuarioLogado());

        return carrinhoRepository.save(carrinho);
    }
}