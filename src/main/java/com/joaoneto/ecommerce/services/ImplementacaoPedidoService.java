package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.domain.*;
import com.joaoneto.ecommerce.dtos.EnderecoDTO;
import com.joaoneto.ecommerce.dtos.ItemDoPedidoDTO;
import com.joaoneto.ecommerce.dtos.PedidoDTO;
import com.joaoneto.ecommerce.exceptions.APIException;
import com.joaoneto.ecommerce.exceptions.RecursoNaoEncontradoException;
import com.joaoneto.ecommerce.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImplementacaoPedidoService implements PedidoService{

    private final CarrinhoRepository carrinhoRepository;

    private final EnderecoRepository enderecoRepository;

    private final PagamentoRepository pagamentoRepository;

    private final PedidoRepository pedidoRepository;

    private final ItemDoPedidoRepository itemDoPedidoRepository;

    private final ProdutoRepository produtoRepository;

    private final CarrinhoService carrinhoService;

    private final ModelMapper modelMapper;

    public ImplementacaoPedidoService(CarrinhoRepository carrinhoRepository, EnderecoRepository enderecoRepository, PagamentoRepository pagamentoRepository, PedidoRepository pedidoRepository, ItemDoPedidoRepository itemDoPedidoRepository, ProdutoRepository produtoRepository, CarrinhoService carrinhoService, ModelMapper modelMapper) {
        this.carrinhoRepository = carrinhoRepository;
        this.enderecoRepository = enderecoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
        this.itemDoPedidoRepository = itemDoPedidoRepository;
        this.produtoRepository = produtoRepository;
        this.carrinhoService = carrinhoService;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public PedidoDTO realizarPedido(String email, Long idEndereco, String metodoDePagamento, String nomeGateway, String idPagamentoGateway, String statusGateway, String mensagemRespostaGateway) {

        Carrinho carrinho = carrinhoRepository.findByUsuario_Email(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Carrinho", "email", email));

        Endereco endereco = enderecoRepository.findById(idEndereco)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereco", "idEndereco", idEndereco));

        Pedido pedido = new Pedido();
        pedido.setEmail(email);
        pedido.setDataDoPedido(LocalDate.now());
        pedido.setValorTotal(carrinho.getPrecoTotal());
        pedido.setStatusPedido(StatusPedido.PENDENTE);
        pedido.setEndereco(endereco);

        Pagamento pagamento = new Pagamento(metodoDePagamento, idPagamentoGateway, statusGateway, mensagemRespostaGateway, nomeGateway);
        pagamento.setPedido(pedido);
        pagamento = pagamentoRepository.save(pagamento);
        pedido.setPagamento(pagamento);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        List<ItemDoCarrinho> itensDoCarrinho = carrinho.getItensDoCarrinho();
        if (itensDoCarrinho.isEmpty()) {
            throw new APIException("O carrinho está vazio");
        }

        List<ItemDoPedido> itensDoPedido = new ArrayList<>();
        for (ItemDoCarrinho itemDoCarrinho : itensDoCarrinho) {
            ItemDoPedido itemDoPedido = new ItemDoPedido();
            itemDoPedido.setProduto(itemDoCarrinho.getProduto());
            itemDoPedido.setQuantidade(itemDoCarrinho.getQuantidade());
            itemDoPedido.setDesconto(itemDoCarrinho.getDesconto());
            itemDoPedido.setPrecoProdutoPedido(itemDoCarrinho.getPrecoComDesconto());
            itemDoPedido.setPedido(pedidoSalvo);
            itensDoPedido.add(itemDoPedido);
        }

        itensDoPedido = itemDoPedidoRepository.saveAll(itensDoPedido);

        carrinho.getItensDoCarrinho().forEach(item -> {
            int quantidade = item.getQuantidade();
            Produto produto = item.getProduto();
            produto.setQuantidade(produto.getQuantidade() - quantidade);
            produtoRepository.save(produto);

            carrinhoService.deletarProdutoDoCarrinho(carrinho.getIdCarrinho(), item.getProduto().getIdProduto());
        });

        PedidoDTO pedidoDTO = modelMapper.map(pedidoSalvo, PedidoDTO.class);

        itensDoPedido.forEach(item ->
                pedidoDTO.getItensDoPedido().add(
                        modelMapper.map(item, ItemDoPedidoDTO.class)
                ));

        pedidoDTO.setEnderecoDeEntrega(modelMapper.map(endereco, EnderecoDTO.class));

        return pedidoDTO;
    }
}
