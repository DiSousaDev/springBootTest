package br.com.diego.springboottest.models;

import br.com.diego.springboottest.exceptions.SaldoInsuficienteException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "contas")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cliente;
    private BigDecimal saldo;

    public Conta() {
    }

    public Conta(Long id, String cliente, BigDecimal saldo) {
        this.id = id;
        this.cliente = cliente;
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public void debito(BigDecimal valor) {
        BigDecimal novoSaldo = saldo.subtract(valor);
        if(novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente.");
        }
        this.saldo = novoSaldo;
    }

    public void credito(BigDecimal valor) {
        this.saldo = saldo.add(valor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conta conta = (Conta) o;
        return Objects.equals(id, conta.id) && Objects.equals(cliente, conta.cliente) && Objects.equals(saldo, conta.saldo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cliente, saldo);
    }

}
