package br.com.diego.springboottest;

import br.com.diego.springboottest.models.Conta;
import br.com.diego.springboottest.repositories.ContaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class IntegracaoJpaTest {

    @Autowired
    ContaRepository contaRepository;

    @Test
    void testFindById(){
        Optional<Conta> contaOptional = contaRepository.findById(1L);
        assertTrue(contaOptional.isPresent());
        assertEquals("Carlos Silva", contaOptional.orElseThrow().getCliente());
    }

    @Test
    void testFindByCliente(){
        Optional<Conta> contaOptional = contaRepository.findByCliente("Carlos Silva");
        assertTrue(contaOptional.isPresent());
        assertEquals("Carlos Silva", contaOptional.orElseThrow().getCliente());
        assertEquals("1000.00", contaOptional.orElseThrow().getSaldo().toPlainString());
    }

    @Test
    void testFindByClienteThrowException(){
        Optional<Conta> contaOptional = contaRepository.findByCliente("Pedro Bento");
        assertThrows(NoSuchElementException.class, contaOptional::orElseThrow);
        assertFalse(contaOptional.isPresent());
    }

    @Test
    void findAll(){
        List<Conta> contas = contaRepository.findAll();
        assertFalse(contas.isEmpty());
        assertEquals(2, contas.size());
    }

    @Test
    void testSave(){
        // Given
        Conta contaJoao = new Conta(null, "João", new BigDecimal("3000"));

        // When
        Conta conta = contaRepository.save(contaJoao);
        // Conta conta = contaRepository.findByCliente("João").orElseThrow();

        // Then
        assertEquals("João", conta.getCliente());
        assertEquals("3000", conta.getSaldo().toPlainString());
        // assertEquals(3, conta.getId());

    }
}

