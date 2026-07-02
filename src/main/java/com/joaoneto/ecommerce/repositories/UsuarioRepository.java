package com.joaoneto.ecommerce.repositories;

import com.joaoneto.ecommerce.domain.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNomeUsuario(String nomeUsuario);

    boolean existsByEmail(@NotBlank @Size(max = 50) String email);

    boolean existsByNomeUsuario(@NotBlank @Size(min = 3, max = 20) String nomeUsuario);
}
