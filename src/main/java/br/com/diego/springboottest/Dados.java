package br.com.diego.springboottest;

import br.com.diego.springboottest.models.Banco;
import br.com.diego.springboottest.models.Conta;

import java.math.BigDecimal;

public class Dados {

//    public static final Conta CONTA_001 = new Conta(1L, "Carlos Silva", new BigDecimal("1000"));
//    public static final Conta CONTA_002 = new Conta(2L, "Maria Rita", new BigDecimal("2000"));
//    public static final Banco BANCO = new Banco(1L, "O Banco de Todos", 0);

    public static  Conta conta001() {
        return new Conta(1L, "Carlos Silva", new BigDecimal("1000"));
    }

    public static  Conta conta002() {
        return new Conta(2L, "Maria Rita", new BigDecimal("2000"));
    }

    public static Banco banco() {
        return new Banco(1L, "O Banco de Todos", 0);
    }

}