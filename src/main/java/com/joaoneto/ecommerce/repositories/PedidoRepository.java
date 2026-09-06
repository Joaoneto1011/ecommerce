package com.joaoneto.ecommerce.repositories;

import com.joaoneto.ecommerce.domain.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByEmailOrderByDataDoPedidoDesc(String email);
}
