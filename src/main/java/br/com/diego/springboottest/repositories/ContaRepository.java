package br.com.diego.springboottest.repositories;

import br.com.diego.springboottest.models.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByCliente(String cliente);

}
