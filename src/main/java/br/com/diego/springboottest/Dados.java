package br.com.diego.springboottest;

import br.com.diego.springboottest.models.Banco;
import br.com.diego.springboottest.models.Conta;
import br.com.diego.springboottest.models.TransacaoDto;

import java.math.BigDecimal;
import java.util.Optional;

public class Dados {

//    public static final Conta CONTA_001 = new Conta(1L, "Carlos Silva", new BigDecimal("1000"));
//    public static final Conta CONTA_002 = new Conta(2L, "Maria Rita", new BigDecimal("2000"));
//    public static final Banco BANCO = new Banco(1L, "O Banco de Todos", 0);

    public static Optional<Conta> conta001() {
        return Optional.of(new Conta(1L, "Carlos Silva", new BigDecimal("1000")));
    }

    public static  Optional<Conta> conta002() {
        return Optional.of(new Conta(2L, "Maria Rita", new BigDecimal("2000")));
    }

    public static Optional<Banco> banco() {
        return Optional.of(new Banco(1L, "O Banco de Todos", 0));
    }

    public static TransacaoDto getTransacaoDtoMock() {
        TransacaoDto dto = new TransacaoDto();
        dto.setContaOrigemId(1L);
        dto.setContaDestinoId(2L);
        dto.setValor(new BigDecimal("100"));
        dto.setBancoId(1L);
        return dto;
    }

}
