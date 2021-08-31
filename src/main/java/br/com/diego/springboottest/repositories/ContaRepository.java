package br.com.diego.springboottest.repositories;

import br.com.diego.springboottest.models.Conta;

import java.util.List;

public interface ContaRepository {

    List<Conta> findAll();

    Conta findById(Long id);

    void update(Conta conta);

}
