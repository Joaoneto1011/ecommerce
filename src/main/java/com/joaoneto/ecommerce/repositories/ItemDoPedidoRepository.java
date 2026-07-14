package com.joaoneto.ecommerce.repositories;

import com.joaoneto.ecommerce.domain.ItemDoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemDoPedidoRepository extends JpaRepository<ItemDoPedido, Long> {
}
