package br.com.fiap.mercadomvc.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class CompraRequestDto {

    @NotBlank(message = "O nome do cliente é obrigatório.")
    private String nomeCliente;

    @NotBlank(message = "O e-mail do cliente é obrigatório.")
    @Email(message = "Informe um e-mail válido.")
    private String emailCliente;

    private String cpfCliente;

    @NotBlank(message = "O método de pagamento é obrigatório (PIX, CARTAO_CREDITO, CARTAO_DEBITO, BOLETO).")
    private String metodoPagamento;

    private String numeroCartao;

    @NotEmpty(message = "A lista de itens da compra não pode estar vazia.")
    @Valid
    private List<ItemCompraDto> itens = new ArrayList<>();

    public CompraRequestDto() {
    }

    public CompraRequestDto(String nomeCliente, String emailCliente, String cpfCliente,
                            String metodoPagamento, String numeroCartao, List<ItemCompraDto> itens) {
        this.nomeCliente = nomeCliente;
        this.emailCliente = emailCliente;
        this.cpfCliente = cpfCliente;
        this.metodoPagamento = metodoPagamento;
        this.numeroCartao = numeroCartao;
        this.itens = itens;
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

    public List<ItemCompraDto> getItens() {
        return itens;
    }

    public void setItens(List<ItemCompraDto> itens) {
        this.itens = itens;
    }
}
