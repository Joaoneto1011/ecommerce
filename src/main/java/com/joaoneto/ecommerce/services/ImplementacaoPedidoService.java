package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.domain.*;
import com.joaoneto.ecommerce.dtos.EnderecoDTO;
import com.joaoneto.ecommerce.dtos.ItemDoPedidoDTO;
import com.joaoneto.ecommerce.dtos.PedidoDTO;
import com.joaoneto.ecommerce.exceptions.APIException;
import com.joaoneto.ecommerce.exceptions.RecursoNaoEncontradoException;
import com.joaoneto.ecommerce.repositories.*;
import com.joaoneto.ecommerce.dtos.ProdutoDTO;
import com.joaoneto.ecommerce.util.UtilitarioDeImagem;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private final UtilitarioDeImagem utilitarioDeImagem;

    public ImplementacaoPedidoService(CarrinhoRepository carrinhoRepository, EnderecoRepository enderecoRepository, PagamentoRepository pagamentoRepository, PedidoRepository pedidoRepository, ItemDoPedidoRepository itemDoPedidoRepository, ProdutoRepository produtoRepository, CarrinhoService carrinhoService, ModelMapper modelMapper, UtilitarioDeImagem utilitarioDeImagem) {
        this.carrinhoRepository = carrinhoRepository;
        this.enderecoRepository = enderecoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
        this.itemDoPedidoRepository = itemDoPedidoRepository;
        this.produtoRepository = produtoRepository;
        this.carrinhoService = carrinhoService;
        this.modelMapper = modelMapper;
        this.utilitarioDeImagem = utilitarioDeImagem;
    }

    private ItemDoPedidoDTO converterParaItemDoPedidoDTO(ItemDoPedido item) {
        ItemDoPedidoDTO itemDTO = modelMapper.map(item, ItemDoPedidoDTO.class);
        ProdutoDTO produtoDTO = itemDTO.getProduto();
        if (produtoDTO != null) {
            produtoDTO.setImagem(utilitarioDeImagem.construirUrl(item.getProduto().getImagem()));
        }
        return itemDTO;
    }

    @Override
    @Transactional
    public PedidoDTO realizarPedido(String email, Long idEndereco, String metodoDePagamento, String nomeGateway, String idPagamentoGateway, String statusGateway, String mensagemRespostaGateway) {

        Carrinho carrinho = carrinhoRepository.findByUsuario_Email(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Carrinho", "email", email));

        Endereco endereco = enderecoRepository.findById(idEndereco)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereco", "idEndereco", idEndereco));

        if (!endereco.getUsuario().getEmail().equalsIgnoreCase(email)) {
            throw new RecursoNaoEncontradoException("Endereco", "idEndereco", idEndereco);
        }

        List<ItemDoCarrinho> itensDoCarrinho = carrinho.getItensDoCarrinho();
        if (itensDoCarrinho.isEmpty()) {
            throw new APIException("O carrinho está vazio");
        }

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

        carrinhoService.limparCarrinhoAposPedido(carrinho.getIdCarrinho());

        PedidoDTO pedidoDTO = modelMapper.map(pedidoSalvo, PedidoDTO.class);

        // Usa setItensDoPedido (substitui) em vez de adicionar aos itens que o ModelMapper
        // já pode ter populado automaticamente ao mapear pedidoSalvo — usar .add() aqui
        // duplicava cada item do pedido (mesmo bug corrigido em converterParaPedidoDTO).
        List<ItemDoPedidoDTO> itensDoPedidoDTO = itensDoPedido.stream()
                .map(this::converterParaItemDoPedidoDTO)
                .toList();
        pedidoDTO.setItensDoPedido(itensDoPedidoDTO);

        pedidoDTO.setEnderecoDeEntrega(modelMapper.map(endereco, EnderecoDTO.class));

        return pedidoDTO;
    }

    private PedidoDTO converterParaPedidoDTO(Pedido pedido) {
        PedidoDTO pedidoDTO = modelMapper.map(pedido, PedidoDTO.class);
        List<ItemDoPedidoDTO> itensDoPedidoDTO = pedido.getItensDoPedido().stream()
                .map(this::converterParaItemDoPedidoDTO)
                .toList();
        pedidoDTO.setItensDoPedido(itensDoPedidoDTO);
        pedidoDTO.setEnderecoDeEntrega(modelMapper.map(pedido.getEndereco(), EnderecoDTO.class));
        return pedidoDTO;
    }

    @Override
    public List<PedidoDTO> buscarPedidosDoUsuarioLogado(String email) {
        return pedidoRepository.findByEmailOrderByDataDoPedidoDesc(email).stream()
                .map(this::converterParaPedidoDTO)
                .toList();
    }

    @Override
    public List<PedidoDTO> listarTodosPedidos() {
        return pedidoRepository.findAll().stream()
                .map(this::converterParaPedidoDTO)
                .toList();
    }

    private static final Map<StatusPedido, Set<StatusPedido>> TRANSICOES_DE_STATUS_PERMITIDAS = Map.of(
            StatusPedido.PENDENTE, Set.of(StatusPedido.PAGO, StatusPedido.CANCELADO),
            StatusPedido.PAGO, Set.of(StatusPedido.ENVIADO, StatusPedido.CANCELADO),
            StatusPedido.ENVIADO, Set.of(StatusPedido.ENTREGUE),
            StatusPedido.ENTREGUE, Set.of(),
            StatusPedido.CANCELADO, Set.of()
    );

    @Override
    @Transactional
    public PedidoDTO atualizarStatusPedido(Long idPedido, StatusPedido novoStatus) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", "idPedido", idPedido));

        StatusPedido statusAtual = pedido.getStatusPedido();

        if (!TRANSICOES_DE_STATUS_PERMITIDAS.getOrDefault(statusAtual, Set.of()).contains(novoStatus)) {
            throw new APIException("Não é possível mudar o status de " + statusAtual + " para " + novoStatus + ".");
        }

        pedido.setStatusPedido(novoStatus);
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaPedidoDTO(pedidoAtualizado);
    }
}
