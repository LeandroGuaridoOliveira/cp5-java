package br.com.fiap.mercadomvc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "TDS_TB_Compras_Mercado")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODIGO_TRANSACAO", nullable = false, unique = true, length = 60)
    private String codigoTransacao;

    @Column(name = "NOME_CLIENTE", nullable = false, length = 100)
    private String nomeCliente;

    @Column(name = "EMAIL_CLIENTE", nullable = false, length = 120)
    private String emailCliente;

    @Column(name = "CPF_CLIENTE", length = 20)
    private String cpfCliente;

    @Column(name = "METODO_PAGAMENTO", nullable = false, length = 30)
    private String metodoPagamento; // PIX, CARTAO_CREDITO, CARTAO_DEBITO, BOLETO

    @Column(name = "NUMERO_CARTAO_FINAL", length = 20)
    private String numeroCartaoFinal;

    @Column(name = "ITENS_RESUMO", nullable = false, length = 1000)
    private String itensResumo;

    @Column(name = "QUANTIDADE_TOTAL", nullable = false)
    private Integer quantidadeTotal;

    @Column(name = "VALOR_TOTAL", nullable = false)
    private Double valorTotal;

    @Column(name = "LUCRO_ESTIMADO", nullable = false)
    private Double lucroEstimado;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_PAGAMENTO", nullable = false, length = 30)
    private StatusPagamento statusPagamento;

    @Column(name = "PROTOCOLO_BANCO", length = 80)
    private String protocoloBanco;

    @Column(name = "MENSAGEM_BANCO", length = 300)
    private String mensagemBanco;

    @Column(name = "EMAIL_ENVIADO", nullable = false)
    private boolean emailEnviado = false;

    @Column(name = "DATA_COMPRA", nullable = false)
    private LocalDateTime dataCompra;

    @Column(name = "DATA_CONFIRMACAO")
    private LocalDateTime dataConfirmacao;

    public Compra() {
        this.dataCompra = LocalDateTime.now();
        this.statusPagamento = StatusPagamento.PENDENTE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getNumeroCartaoFinal() {
        return numeroCartaoFinal;
    }

    public void setNumeroCartaoFinal(String numeroCartaoFinal) {
        this.numeroCartaoFinal = numeroCartaoFinal;
    }

    public String getItensResumo() {
        return itensResumo;
    }

    public void setItensResumo(String itensResumo) {
        this.itensResumo = itensResumo;
    }

    public Integer getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(Integer quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Double getLucroEstimado() {
        return lucroEstimado;
    }

    public void setLucroEstimado(Double lucroEstimado) {
        this.lucroEstimado = lucroEstimado;
    }

    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamento statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public String getProtocoloBanco() {
        return protocoloBanco;
    }

    public void setProtocoloBanco(String protocoloBanco) {
        this.protocoloBanco = protocoloBanco;
    }

    public String getMensagemBanco() {
        return mensagemBanco;
    }

    public void setMensagemBanco(String mensagemBanco) {
        this.mensagemBanco = mensagemBanco;
    }

    public boolean isEmailEnviado() {
        return emailEnviado;
    }

    public void setEmailEnviado(boolean emailEnviado) {
        this.emailEnviado = emailEnviado;
    }

    public LocalDateTime getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDateTime dataCompra) {
        this.dataCompra = dataCompra;
    }

    public LocalDateTime getDataConfirmacao() {
        return dataConfirmacao;
    }

    public void setDataConfirmacao(LocalDateTime dataConfirmacao) {
        this.dataConfirmacao = dataConfirmacao;
    }
}
