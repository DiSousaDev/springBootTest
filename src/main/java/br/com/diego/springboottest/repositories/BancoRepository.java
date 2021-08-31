package br.com.diego.springboottest.repositories;

import br.com.diego.springboottest.models.Banco;

import java.util.List;

public interface BancoRepository {

    List<Banco> findAll();

    Banco findById(Long id);

    void update(Banco banco);

}
