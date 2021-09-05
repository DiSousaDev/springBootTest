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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static br.com.diego.springboottest.Dados.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SpringBootTestApplicationTests {

    @InjectMocks
    ContaServiceImpl service;

    @Mock
    ContaRepository contaRepository;

    @Mock
    BancoRepository bancoRepository;

    @BeforeEach
    void setUp() {
//        contaRepository = mock(ContaRepository.class);
//        bancoRepository = mock(BancoRepository.class);
//        service = new ContaServiceImpl(contaRepository, bancoRepository);
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
        verify(contaRepository, times(2)).save(any(Conta.class));

        verify(bancoRepository, times(2)).findById(1L);
        verify(bancoRepository).save(any(Banco.class));

        verify(contaRepository, times(6)).findById(anyLong());
        verify(contaRepository, never()).findAll();

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
        verify(contaRepository, never()).save(any(Conta.class));

        verify(bancoRepository, times(1)).findById(1L);
        verify(bancoRepository, never()).save(any(Banco.class));

        verify(contaRepository, times(5)).findById(anyLong());
        verify(contaRepository, never()).findAll();

    }

    @Test
    void contextLoads3() {
        when(contaRepository.findById(1L)).thenReturn(conta001());

        Conta conta1 = service.buscarPorId(1L);
        Conta conta2 = service.buscarPorId(1L);

        assertSame(conta1, conta2);
        assertTrue(conta1 == conta2);
        assertEquals("Carlos Silva", conta1.getCliente());
        assertEquals("Carlos Silva", conta2.getCliente());

        verify(contaRepository, times(2)).findById(1L);
    }

    @Test
    void contextLoads4(){
        // Given
        List<Conta> dados = Arrays.asList(conta001().orElseThrow(), conta002().orElseThrow());
        when(contaRepository.findAll()).thenReturn(dados);

        // When
        List<Conta> contas = service.buscarTodas();

        // Then
        assertFalse(contas.isEmpty());
        assertEquals(2, contas.size());
        assertTrue(contas.contains(conta001().orElseThrow()));

        verify(contaRepository).findAll();

    }
}
