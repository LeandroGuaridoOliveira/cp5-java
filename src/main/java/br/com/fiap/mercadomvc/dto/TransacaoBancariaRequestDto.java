package br.com.fiap.mercadomvc.dto;

import java.io.Serializable;

public class TransacaoBancariaRequestDto implements Serializable {

    private Long compraId;
    private String codigoTransacao;
    private String nomeCliente;
    private String emailCliente;
    private String cpfCliente;
    private String metodoPagamento;
    private String numeroCartao;
    private Double valorTotal;
    private String descricaoItens;

    public TransacaoBancariaRequestDto() {
    }

    public TransacaoBancariaRequestDto(Long compraId, String codigoTransacao, String nomeCliente,
                                       String emailCliente, String cpfCliente, String metodoPagamento,
                                       String numeroCartao, Double valorTotal, String descricaoItens) {
        this.compraId = compraId;
        this.codigoTransacao = codigoTransacao;
        this.nomeCliente = nomeCliente;
        this.emailCliente = emailCliente;
        this.cpfCliente = cpfCliente;
        this.metodoPagamento = metodoPagamento;
        this.numeroCartao = numeroCartao;
        this.valorTotal = valorTotal;
        this.descricaoItens = descricaoItens;
    }

    public Long getCompraId() {
        return compraId;
    }

    public void setCompraId(Long compraId) {
        this.compraId = compraId;
    }

    public String getCodigoTransacao() {
        return codigoTransacao;
    }

    public void setCodigoTransacao(String codigoTransacao) {
        this.codigoTransacao = codigoTransacao;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    public String getCpfCliente() {
        return cpfCliente;
    }

    public void setCpfCliente(String cpfCliente) {
        this.cpfCliente = cpfCliente;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getDescricaoItens() {
        return descricaoItens;
    }

    public void setDescricaoItens(String descricaoItens) {
        this.descricaoItens = descricaoItens;
    }
}
