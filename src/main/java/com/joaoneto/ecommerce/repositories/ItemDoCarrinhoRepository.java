package com.joaoneto.ecommerce.repositories;

import com.joaoneto.ecommerce.domain.ItemDoCarrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemDoCarrinhoRepository extends JpaRepository<ItemDoCarrinho, Long> {

    Optional<ItemDoCarrinho> findByCarrinho_IdCarrinhoAndProduto_IdProduto(Long idCarrinho, Long idProduto);

}
