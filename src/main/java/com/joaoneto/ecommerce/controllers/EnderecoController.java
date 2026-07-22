package com.joaoneto.ecommerce.controllers;

import com.joaoneto.ecommerce.domain.Usuario;
import com.joaoneto.ecommerce.dtos.EnderecoDTO;
import com.joaoneto.ecommerce.dtos.RespostaDaAPI;
import com.joaoneto.ecommerce.services.EnderecoService;
import com.joaoneto.ecommerce.util.UtilitarioDeAutenticacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "APIs de Endereço", description = "APIs para gerenciamento dos endereços do usuário")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api")
public class EnderecoController {

    private final EnderecoService enderecoService;

    private final UtilitarioDeAutenticacao utilitarioDeAutenticacao;

    public EnderecoController(EnderecoService enderecoService, UtilitarioDeAutenticacao utilitarioDeAutenticacao) {
        this.enderecoService = enderecoService;
        this.utilitarioDeAutenticacao = utilitarioDeAutenticacao;
    }

    @Operation(summary = "Criar endereço", description = "API para cadastrar um novo endereço vinculado ao usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso",
                    content = @Content(schema = @Schema(implementation = EnderecoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Corpo da requisição inválido: um ou mais campos não passaram na validação",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Erro de validação\", \"status\": false, \"erros\": {\"rua\": \"não deve estar em branco\", \"cep\": \"não deve estar em branco\"}}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @PostMapping("/enderecos")
    public ResponseEntity<EnderecoDTO> criarEndereco(@Valid @RequestBody EnderecoDTO enderecoDTO) {

        Usuario usuario = utilitarioDeAutenticacao.usuarioLogado();

        EnderecoDTO salvarEnderecoDTO = enderecoService.criarEndereco(enderecoDTO, usuario);

        return new ResponseEntity<>(salvarEnderecoDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Buscar todos os endereços", description = "API para buscar todos os endereços cadastrados no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Endereços encontrados com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EnderecoDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @GetMapping("/enderecos")
    public ResponseEntity<List<EnderecoDTO>> buscarEnderecos() {

        List<EnderecoDTO> listaEnderecos = enderecoService.buscarEndereco();

        return new ResponseEntity<>(listaEnderecos, HttpStatus.OK);
    }

    @Operation(summary = "Buscar endereço por ID", description = "API para buscar um endereço específico pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Endereço encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = EnderecoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Endereco nao encontrado com idEndereco: 1\", \"status\": false}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @GetMapping("/enderecos/{idEndereco}")
    public ResponseEntity<EnderecoDTO> buscarEnderecoPorId(
            @Parameter(description = "ID do endereço que você deseja buscar")
            @PathVariable Long idEndereco) {

        EnderecoDTO enderecoDTOS = enderecoService.buscarEnderecoPorId(idEndereco);

        return new ResponseEntity<>(enderecoDTOS, HttpStatus.OK);
    }

    @Operation(summary = "Buscar endereços do usuário logado", description = "API para buscar todos os endereços vinculados ao usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Endereços encontrados com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EnderecoDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @GetMapping("/enderecos/usuarios")
    public ResponseEntity<List<EnderecoDTO>> buscarEnderecoPorUsuario() {

        Usuario usuario = utilitarioDeAutenticacao.usuarioLogado();

        List<EnderecoDTO> listaEnderecos = enderecoService.buscarEnderecoPorUsuario(usuario);

        return new ResponseEntity<>(listaEnderecos, HttpStatus.OK);
    }

    @Operation(summary = "Atualizar endereço", description = "API para atualizar os dados de um endereço existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = EnderecoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Corpo da requisição inválido: um ou mais campos não passaram na validação",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Erro de validação\", \"status\": false, \"erros\": {\"rua\": \"A rua não deve estar em branco\", \"cep\": \"O CEP não deve estar em branco\"}}"))),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Endereco nao encontrado com idEndereco: 1\", \"status\": false}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @PutMapping("/enderecos/{idEndereco}")
    public ResponseEntity<EnderecoDTO> atualizarEndereco(
            @Parameter(description = "ID do endereço que você deseja atualizar")
            @PathVariable Long idEndereco,
            @Valid @RequestBody EnderecoDTO enderecoDTO) {

        EnderecoDTO atualizarEndereco = enderecoService.atualizarEndereco(idEndereco, enderecoDTO);

        return new ResponseEntity<>(atualizarEndereco, HttpStatus.OK);
    }

    @Operation(summary = "Deletar endereço", description = "API para excluir um endereço existente pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Endereço deletado com sucesso",
                    content = @Content(examples = @ExampleObject(value = "Endereço deletado com sucesso com o idEndereço: 1"))),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado",
                    content = @Content(schema = @Schema(implementation = RespostaDaAPI.class),
                            examples = @ExampleObject(value = "{\"mensagem\": \"Endereco nao encontrado com idEndereco: 1\", \"status\": false}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content),
    })
    @DeleteMapping("/enderecos/{idEndereco}")
    public ResponseEntity<String> deletarEndereco(
            @Parameter(description = "ID do endereço que você deseja excluir")
            @PathVariable Long idEndereco) {

        String status = enderecoService.deletarEndereco(idEndereco);

        return new ResponseEntity<>(status, HttpStatus.OK);
    }

}