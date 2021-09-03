package br.com.diego.springboottest.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "bancos")
public class Banco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @Column(name = "total_transferencias")
    private int totalTransferencias;

    public Banco() {

    }

    public Banco(Long id, String nome, int totalTransferencias) {
        this.id = id;
        this.nome = nome;
        this.totalTransferencias = totalTransferencias;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getTotalTransferencias() {
        return totalTransferencias;
    }

    public void setTotalTransferencias(int totalTransferencias) {
        this.totalTransferencias = totalTransferencias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Banco banco = (Banco) o;
        return Objects.equals(id, banco.id) && Objects.equals(nome, banco.nome) && Objects.equals(totalTransferencias, banco.totalTransferencias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, totalTransferencias);
    }
}
