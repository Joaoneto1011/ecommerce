package com.joaoneto.ecommerce.controllers;

import com.joaoneto.ecommerce.domain.Carrinho;
import com.joaoneto.ecommerce.dtos.CarrinhoDTO;
import com.joaoneto.ecommerce.repositories.CarrinhoRepository;
import com.joaoneto.ecommerce.services.CarrinhoService;
import com.joaoneto.ecommerce.util.UtilitarioDeAutenticacao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CarrinhoController {

    private final CarrinhoRepository carrinhoRepository;

    private final UtilitarioDeAutenticacao utilitarioDeAutenticacao;

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoRepository carrinhoRepository, UtilitarioDeAutenticacao utilitarioDeAutenticacao, CarrinhoService carrinhoService) {
        this.carrinhoRepository = carrinhoRepository;
        this.utilitarioDeAutenticacao = utilitarioDeAutenticacao;
        this.carrinhoService = carrinhoService;
    }

    @PostMapping("/carrinhos/produtos/{idProduto}/quantidade/{quantidade}")
    public ResponseEntity<CarrinhoDTO> adicionarProdutoAoCarrinho(@PathVariable Long idProduto,
                                                                  @PathVariable Integer quantidade) {
        CarrinhoDTO carrinhoDTO = carrinhoService.adicionarProdutoAoCarrinho(idProduto, quantidade);
        return new ResponseEntity<CarrinhoDTO>(carrinhoDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carrinhos")
    public ResponseEntity<List<CarrinhoDTO>> obterCarrinhos() {

        List<CarrinhoDTO> carrinhoDTOS = carrinhoService.obterTodosCarrinhos();

        return new ResponseEntity<List<CarrinhoDTO>>(carrinhoDTOS, HttpStatus.FOUND);
    }

    @GetMapping("/carrinhos/usuarios/carrinho")
    public ResponseEntity<CarrinhoDTO> obterCarrinhoPorId() {

        String idEmail = utilitarioDeAutenticacao.emailDoUsuarioLogado();

        Optional<Carrinho> carrinho = carrinhoRepository.findByUsuario_Email(idEmail);

        Long idCarrinho = carrinho.get().getIdCarrinho();

        CarrinhoDTO carrinhoDTO = carrinhoService.obterCarrinho(idEmail, idCarrinho);

        return new ResponseEntity<CarrinhoDTO>(carrinhoDTO, HttpStatus.OK);
    }

    @PutMapping("/carrinho/produtos/{idProduto}/quantidade/{operacao}")
    public ResponseEntity<CarrinhoDTO> atualizarProdutoDoCarrinho(@PathVariable Long idProduto,
                                                                  @PathVariable String operacao) {
        CarrinhoDTO carrinhoDTO = carrinhoService.atualizarQuantidadeDoProdutoNoCarrinho(idProduto,
                operacao.equalsIgnoreCase("deletar") ? -1 : 1);

        return new ResponseEntity<CarrinhoDTO>(carrinhoDTO, HttpStatus.OK);
    }

    @DeleteMapping("/carrinhos/{idCarrinho}/produto/{idProduto}")
    public ResponseEntity<String> deletarProdutoDoCarrinho(@PathVariable Long idCarrinho,
                                                           @PathVariable Long idProduto) {

        String status = carrinhoService.deletarProdutoDoCarrinho(idCarrinho, idProduto);

        return new ResponseEntity<String>(status, HttpStatus.OK);

    }
}
