package com.joaoneto.ecommerce.repositories;

import com.joaoneto.ecommerce.domain.Categoria;
import com.joaoneto.ecommerce.domain.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Page<Produto> findByCategoriaOrderByPrecoAsc(Categoria categoria, Pageable pageDetails);

    Page<Produto> findByNomeProdutoLikeIgnoreCase(String keyword, Pageable pageDetails);
}
