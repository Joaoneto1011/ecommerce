package com.joaoneto.ecommerce.repositories;

import com.joaoneto.ecommerce.domain.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {

    Optional<Carrinho> findByUsuario_Email(String email);

    Optional<Carrinho> findByUsuario_EmailAndIdCarrinho(String email, Long idCarrinho);
}
