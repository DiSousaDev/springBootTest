package br.com.diego.springboottest.services;

import br.com.diego.springboottest.models.Conta;

import java.math.BigDecimal;
import java.util.List;

public interface ContaService {

    Conta buscarPorId(Long id);

    int consultarTotalTransferencia(Long bancoId);

    BigDecimal consultarSaldo(Long contaId);

    void efetuarTransferencia(Long contaOrigem, Long contaDestino, BigDecimal valor, Long bancoId);

    List<Conta> buscarTodas();

    Conta salvar(Conta conta);

    void excluir(Long id);

}
