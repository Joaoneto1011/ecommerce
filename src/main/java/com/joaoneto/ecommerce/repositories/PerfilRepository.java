package com.joaoneto.ecommerce.repositories;

import com.joaoneto.ecommerce.domain.TipoPerfil;
import com.joaoneto.ecommerce.domain.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Integer> {

    Optional<Perfil> findByTipoPerfil(TipoPerfil tipoPerfil);
}
