package br.com.diego.springboottest.services;

import br.com.diego.springboottest.models.Conta;

import java.math.BigDecimal;

public interface ContaService {

    Conta buscarPorId(Long id);

    int consultarTotalTransferencia(Long bancoId);

    BigDecimal consultarSaldo(Long contaId);

    void efetuarTransferencia(Long contaOrigem, Long contaDestino, BigDecimal valor, Long bancoId);

}
