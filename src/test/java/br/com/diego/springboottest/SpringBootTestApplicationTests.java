package br.com.diego.springboottest;

import br.com.diego.springboottest.exceptions.SaldoInsuficienteException;
import br.com.diego.springboottest.models.Banco;
import br.com.diego.springboottest.models.Conta;
import br.com.diego.springboottest.repositories.BancoRepository;
import br.com.diego.springboottest.repositories.ContaRepository;
import br.com.diego.springboottest.services.ContaService;
import br.com.diego.springboottest.services.impl.ContaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static br.com.diego.springboottest.Dados.*;
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
        when(contaRepository.findById(1L)).thenReturn(conta001());
        when(contaRepository.findById(2L)).thenReturn(conta002());
        when(bancoRepository.findById(1L)).thenReturn(banco());

        BigDecimal saldoOrigem = service.consultarSaldo(1L);
        BigDecimal saldoDestino = service.consultarSaldo(2L);

        assertEquals("1000", saldoOrigem.toPlainString());
        assertEquals("2000", saldoDestino.toPlainString());

        service.efetuarTransferencia(1L, 2L, new BigDecimal("100"), 1L);

        saldoOrigem = service.consultarSaldo(1L);
        saldoDestino = service.consultarSaldo(2L);

        assertEquals("900", saldoOrigem.toPlainString());
        assertEquals("2100", saldoDestino.toPlainString());

        int total = service.consultarTotalTransferencia(1L);
        assertEquals(1, total);

        verify(contaRepository, times(3)).findById(1L);
        verify(contaRepository, times(3)).findById(2L);
        verify(contaRepository, times(2)).update(any(Conta.class));

        verify(bancoRepository, times(2)).findById(1L);
        verify(bancoRepository).update(any(Banco.class));

    }

    @Test
    void contextLoads2() {
        when(contaRepository.findById(1L)).thenReturn(conta001());
        when(contaRepository.findById(2L)).thenReturn(conta002());
        when(bancoRepository.findById(1L)).thenReturn(banco());

        BigDecimal saldoOrigem = service.consultarSaldo(1L);
        BigDecimal saldoDestino = service.consultarSaldo(2L);

        assertEquals("1000", saldoOrigem.toPlainString());
        assertEquals("2000", saldoDestino.toPlainString());

        assertThrows(SaldoInsuficienteException.class, () -> {
            service.efetuarTransferencia(1L, 2L, new BigDecimal("1200"), 1L);
        });

        saldoOrigem = service.consultarSaldo(1L);
        saldoDestino = service.consultarSaldo(2L);

        assertEquals("1000", saldoOrigem.toPlainString());
        assertEquals("2000", saldoDestino.toPlainString());

        int total = service.consultarTotalTransferencia(1L);
        assertEquals(0, total);

        verify(contaRepository, times(3)).findById(1L);
        verify(contaRepository, times(2)).findById(2L);
        verify(contaRepository, never()).update(any(Conta.class));

        verify(bancoRepository, times(1)).findById(1L);
        verify(bancoRepository, never()).update(any(Banco.class));

    }

}
