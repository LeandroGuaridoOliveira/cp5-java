package br.com.fiap.mercadomvc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "TDS_TB_Emails_Enviados")
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "COMPRA_ID")
    private Long compraId;

    @Column(name = "CODIGO_TRANSACAO", length = 60)
    private String codigoTransacao;

    @Column(name = "DESTINATARIO", nullable = false, length = 120)
    private String destinatario;

    @Column(name = "NOME_CLIENTE", length = 100)
    private String nomeCliente;

    @Column(name = "ASSUNTO", nullable = false, length = 200)
    private String assunto;

    @Column(name = "CONTEUDO", nullable = false, length = 2500)
    private String conteudo;

    @Column(name = "STATUS_ENVIO", nullable = false, length = 40)
    private String statusEnvio; // ENVIADO_SMTP, REGISTRADO_BD_SIMULADO, ERRO_ENVIO

    @Column(name = "DETALHE_TECNICO", length = 400)
    private String detalheTecnico;

    @Column(name = "DATA_ENVIO", nullable = false)
    private LocalDateTime dataEnvio;

    public EmailLog() {
        this.dataEnvio = LocalDateTime.now();
    }

    public EmailLog(Long compraId, String codigoTransacao, String destinatario, String nomeCliente,
                    String assunto, String conteudo, String statusEnvio, String detalheTecnico) {
        this.compraId = compraId;
        this.codigoTransacao = codigoTransacao;
        this.destinatario = destinatario;
        this.nomeCliente = nomeCliente;
        this.assunto = assunto;
        this.conteudo = conteudo;
        this.statusEnvio = statusEnvio;
        this.detalheTecnico = detalheTecnico;
        this.dataEnvio = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getStatusEnvio() {
        return statusEnvio;
    }

    public void setStatusEnvio(String statusEnvio) {
        this.statusEnvio = statusEnvio;
    }

    public String getDetalheTecnico() {
        return detalheTecnico;
    }

    public void setDetalheTecnico(String detalheTecnico) {
        this.detalheTecnico = detalheTecnico;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }
}
