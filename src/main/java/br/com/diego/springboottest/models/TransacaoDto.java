package br.com.diego.springboottest.models;

import java.math.BigDecimal;

public class TransacaoDto {

    private Long contaOrigemId;
    private Long contaDestinoId;
    private BigDecimal valor;
    private Long bancoId;

    public Long getContaOrigemId(){
        return contaOrigemId;
    }

    public void setContaOrigemId(Long contaOrigemId){
        this.contaOrigemId = contaOrigemId;
    }

    public Long getContaDestinoId(){
        return contaDestinoId;
    }

    public void setContaDestinoId(Long contaDestinoId){
        this.contaDestinoId = contaDestinoId;
    }

    public BigDecimal getValor(){
        return valor;
    }

    public void setValor(BigDecimal valor){
        this.valor = valor;
    }

    public Long getBancoId(){
        return bancoId;
    }

    public void setBancoId(Long bancoId){
        this.bancoId = bancoId;
    }
}
