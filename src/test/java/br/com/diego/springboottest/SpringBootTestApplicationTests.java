package br.com.diego.springboottest;

import br.com.diego.springboottest.repositories.BancoRepository;
import br.com.diego.springboottest.repositories.ContaRepository;
import br.com.diego.springboottest.services.ContaService;
import br.com.diego.springboottest.services.impl.ContaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SpringBootTestApplicationTests {

    ContaRepository contaRepository;
    BancoRepository bancoRepository;

    ContaService service;

    @BeforeEach
    void setUp() {
        contaRepository = mock(ContaRepository.class);
        bancoRepository = mock(BancoRepository.class);
        service = new ContaServiceImpl(contaRepository, bancoRepository);
    }

    @Test
    void contextLoads() {
        when(contaRepository.findById(1L)).thenReturn(Dados.CONTA_001);
        when(contaRepository.findById(2L)).thenReturn(Dados.CONTA_002);
        when(bancoRepository.findById(1L)).thenReturn(Dados.BANCO);

        BigDecimal saldoOrigem = service.consultarSaldo(1L);
        BigDecimal saldoDestino = service.consultarSaldo(2L);

        assertEquals("1000", saldoOrigem.toPlainString());
        assertEquals("2000", saldoDestino.toPlainString());

        service.efetuarTransferencia(1L, 2L, new BigDecimal("100"), 1L);

        saldoOrigem = service.consultarSaldo(1L);
        saldoDestino = service.consultarSaldo(2L);

        assertEquals("900", saldoOrigem.toPlainString());
        assertEquals("2100", saldoDestino.toPlainString());

    }

}
