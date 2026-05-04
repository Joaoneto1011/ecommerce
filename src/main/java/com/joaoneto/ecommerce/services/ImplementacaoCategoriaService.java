package com.joaoneto.ecommerce.services;

import com.joaoneto.ecommerce.exceptions.APIException;
import com.joaoneto.ecommerce.exceptions.RecursoNaoEncontradoException;
import com.joaoneto.ecommerce.model.Categoria;
import com.joaoneto.ecommerce.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImplementacaoCategoriaService implements  CategoriaService{

    private final CategoriaRepository categoriaRepository;

    public ImplementacaoCategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<Categoria> buscarTodasCategorias() {
       return categoriaRepository.findAll();
    }

    @Override
    public Categoria buscarCategoriaPorID(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", "id", id));
    }

    @Override
    public String criarCategoria(Categoria categoria) {
        Categoria existente = categoriaRepository.findByNomeCategoria(categoria.getNomeCategoria());

        if (existente != null) {
            throw new APIException("Categoria com o nome " + categoria.getNomeCategoria() + " ja existe!");
        }
        categoriaRepository.save(categoria);
        return "Categoria criada com sucesso";
    }

    @Override
    public String deletarCategoriaPorID(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", "Id", id));
        categoriaRepository.delete(categoria);

        return "Categoria deletada com sucesso";
    }

    @Override
    public String atualizarCategoriaPorID(Categoria novaCategoria, Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", "Id", id));

        Categoria existente = categoriaRepository.findByNomeCategoria(novaCategoria.getNomeCategoria());

        if (existente != null && !existente.getIdCategoria().equals(id)) {
            throw new APIException("Ja existe uma categoria com esse nome!");
        }

        categoria.setNomeCategoria(novaCategoria.getNomeCategoria());

        categoriaRepository.save(categoria);

        return "Categoria atualizada com sucesso";
    }
}
