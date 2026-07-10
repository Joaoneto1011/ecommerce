package com.joaoneto.ecommerce.controllers;

import com.joaoneto.ecommerce.domain.Usuario;
import com.joaoneto.ecommerce.dtos.EnderecoDTO;
import com.joaoneto.ecommerce.services.EnderecoService;
import com.joaoneto.ecommerce.util.UtilitarioDeAutenticacao;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EnderecoController {

    private final EnderecoService enderecoService;

    private final UtilitarioDeAutenticacao utilitarioDeAutenticacao;

    public EnderecoController(EnderecoService enderecoService, UtilitarioDeAutenticacao utilitarioDeAutenticacao) {
        this.enderecoService = enderecoService;
        this.utilitarioDeAutenticacao = utilitarioDeAutenticacao;
    }

    @PostMapping("/enderecos")
    public ResponseEntity<EnderecoDTO> criarEndereco(@Valid @RequestBody EnderecoDTO enderecoDTO) {

        Usuario usuario = utilitarioDeAutenticacao.usuarioLogado();

        EnderecoDTO salvarEnderecoDTO = enderecoService.criarEndereco(enderecoDTO, usuario);

        return new ResponseEntity<>(salvarEnderecoDTO, HttpStatus.CREATED);
    }

    @GetMapping("/enderecos")
    public ResponseEntity<List<EnderecoDTO>> buscarEnderecos() {

        List<EnderecoDTO> listaEnderecos = enderecoService.buscarEndereco();

        return new ResponseEntity<>(listaEnderecos, HttpStatus.OK);
    }

    @GetMapping("/enderecos/{idEndereco}")
    public ResponseEntity<EnderecoDTO> buscarEnderecoPorId(@PathVariable Long idEndereco) {

        EnderecoDTO enderecoDTOS = enderecoService.buscarEnderecoPorId(idEndereco);

        return new ResponseEntity<>(enderecoDTOS, HttpStatus.OK);
    }

    @GetMapping("/enderecos/usuarios")
    public ResponseEntity<List<EnderecoDTO>> buscarEnderecoPorUsuario() {

        Usuario usuario = utilitarioDeAutenticacao.usuarioLogado();

        List<EnderecoDTO> listaEnderecos = enderecoService.buscarEnderecoPorUsuario(usuario);

        return new ResponseEntity<>(listaEnderecos, HttpStatus.OK);
    }

    @PutMapping("/enderecos/{idEndereco}")
    public ResponseEntity<EnderecoDTO> atualizarEndereco(@PathVariable Long idEndereco,
                                                         @RequestBody EnderecoDTO enderecoDTO) {

        EnderecoDTO atualizarEndereco = enderecoService.atualizarEndereco(idEndereco, enderecoDTO);

        return new ResponseEntity<>(atualizarEndereco, HttpStatus.OK);
    }

    @DeleteMapping("/enderecos/{idEndereco}")
    public ResponseEntity<String> deletarEndereco(@PathVariable Long idEndereco) {

        String status = enderecoService.deletarEndereco(idEndereco);

        return new ResponseEntity<>(status, HttpStatus.OK);
    }

}
