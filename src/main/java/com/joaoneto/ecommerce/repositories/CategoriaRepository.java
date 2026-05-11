package com.joaoneto.ecommerce.repositories;

import com.joaoneto.ecommerce.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Categoria findByNomeCategoria(String nomeCategoria);
}
