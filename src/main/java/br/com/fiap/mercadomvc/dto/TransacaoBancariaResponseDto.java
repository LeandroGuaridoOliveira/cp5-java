package br.com.fiap.mercadomvc.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TransacaoBancariaResponseDto implements Serializable {

    private Long compraId;
    private String codigoTransacao;
    private boolean aprovado;
    private String status; // APROVADO, RECUSADO
    private String protocoloBanco;
    private String mensagem;
    private Double valorProcessado;
    private String instituicao;
    private String dataHoraProcessamento;

    public TransacaoBancariaResponseDto() {
        this.instituicao = "Banco Tranquilo S.A.";
        this.dataHoraProcessamento = LocalDateTime.now().toString();
    }

    public TransacaoBancariaResponseDto(Long compraId, String codigoTransacao, boolean aprovado,
                                        String status, String protocoloBanco, String mensagem,
                                        Double valorProcessado) {
        this.compraId = compraId;
        this.codigoTransacao = codigoTransacao;
        this.aprovado = aprovado;
        this.status = status;
        this.protocoloBanco = protocoloBanco;
        this.mensagem = mensagem;
        this.valorProcessado = valorProcessado;
        this.instituicao = "Banco Tranquilo S.A.";
        this.dataHoraProcessamento = LocalDateTime.now().toString();
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

    public boolean isAprovado() {
        return aprovado;
    }

    public void setAprovado(boolean aprovado) {
        this.aprovado = aprovado;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProtocoloBanco() {
        return protocoloBanco;
    }

    public void setProtocoloBanco(String protocoloBanco) {
        this.protocoloBanco = protocoloBanco;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Double getValorProcessado() {
        return valorProcessado;
    }

    public void setValorProcessado(Double valorProcessado) {
        this.valorProcessado = valorProcessado;
    }

    public String getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    public String getDataHoraProcessamento() {
        return dataHoraProcessamento;
    }

    public void setDataHoraProcessamento(String dataHoraProcessamento) {
        this.dataHoraProcessamento = dataHoraProcessamento;
    }
}
