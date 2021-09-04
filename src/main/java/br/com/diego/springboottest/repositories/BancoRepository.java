package br.com.diego.springboottest.repositories;

import br.com.diego.springboottest.models.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BancoRepository extends JpaRepository<Banco, Long> {

}
