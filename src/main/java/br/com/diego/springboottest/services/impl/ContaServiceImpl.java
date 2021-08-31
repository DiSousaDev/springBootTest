package br.com.diego.springboottest.services.impl;

import br.com.diego.springboottest.models.Banco;
import br.com.diego.springboottest.models.Conta;
import br.com.diego.springboottest.repositories.BancoRepository;
import br.com.diego.springboottest.repositories.ContaRepository;
import br.com.diego.springboottest.services.ContaService;

import java.math.BigDecimal;

public class ContaServiceImpl implements ContaService {

    private ContaRepository contaRepository;

    private BancoRepository bancoRepository;

    public ContaServiceImpl(ContaRepository contaRepository, BancoRepository bancoRepository) {
        this.contaRepository = contaRepository;
        this.bancoRepository = bancoRepository;
    }

    @Override
    public Conta buscarPorId(Long id) {
        return contaRepository.findById(id);
    }

    @Override
    public int consultarTotalTransferencia(Long bancoId) {
        Banco banco = bancoRepository.findById(bancoId);
        return banco.getTotalTransferencias();
    }

    @Override
    public BigDecimal consultarSaldo(Long contaId) {
        Conta conta = contaRepository.findById(contaId);
        return conta.getSaldo();
    }

    @Override
    public void efetuarTransferencia(Long numContaOrigem, Long numContaDestino, BigDecimal valor, Long bancoId) {
       Banco banco = bancoRepository.findById(bancoId);
       int totalTransferencias = banco.getTotalTransferencias();
       banco.setTotalTransferencias(++totalTransferencias);
       bancoRepository.update(banco);

       Conta contaOrigem = contaRepository.findById(numContaOrigem);
       contaOrigem.debito(valor);
       contaRepository.update(contaOrigem);

       Conta contaDestino = contaRepository.findById(numContaDestino);
       contaDestino.credito(valor);
       contaRepository.update(contaDestino);

    }

}
