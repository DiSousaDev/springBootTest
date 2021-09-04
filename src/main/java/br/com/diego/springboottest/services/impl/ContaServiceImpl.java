package br.com.diego.springboottest.services.impl;

import br.com.diego.springboottest.models.Banco;
import br.com.diego.springboottest.models.Conta;
import br.com.diego.springboottest.repositories.BancoRepository;
import br.com.diego.springboottest.repositories.ContaRepository;
import br.com.diego.springboottest.services.ContaService;

import java.math.BigDecimal;
import java.util.Optional;

public class ContaServiceImpl implements ContaService {

    private ContaRepository contaRepository;

    private BancoRepository bancoRepository;

    public ContaServiceImpl(ContaRepository contaRepository, BancoRepository bancoRepository) {
        this.contaRepository = contaRepository;
        this.bancoRepository = bancoRepository;
    }

    @Override
    public Conta buscarPorId(Long id) {
        return contaRepository.findById(id).orElseThrow();
    }

    @Override
    public int consultarTotalTransferencia(Long bancoId) {
        Banco banco = bancoRepository.findById(bancoId).orElseThrow();
        return banco.getTotalTransferencias();
    }

    @Override
    public BigDecimal consultarSaldo(Long contaId) {
        Conta conta = contaRepository.findById(contaId).orElseThrow();
        return conta.getSaldo();
    }

    @Override
    public void efetuarTransferencia(Long numContaOrigem, Long numContaDestino, BigDecimal valor, Long bancoId) {

        Conta contaOrigem = contaRepository.findById(numContaOrigem).orElseThrow();
        contaOrigem.debito(valor);
        contaRepository.save(contaOrigem);

        Conta contaDestino = contaRepository.findById(numContaDestino).orElseThrow();
        contaDestino.credito(valor);
        contaRepository.save(contaDestino);

        adicionarTransferencia(bancoId);

    }

    private void adicionarTransferencia(Long bancoId) {
        Banco banco = bancoRepository.findById(bancoId).orElseThrow();
        int totalTransferencias = banco.getTotalTransferencias();
        banco.setTotalTransferencias(++totalTransferencias);
        bancoRepository.save(banco);
    }

}
