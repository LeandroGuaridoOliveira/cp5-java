package br.com.fiap.mercadomvc.service;

import br.com.fiap.mercadomvc.dto.TransacaoBancariaRequestDto;
import br.com.fiap.mercadomvc.dto.TransacaoBancariaResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BancoTranquiloService {

    private static final Logger log = LoggerFactory.getLogger(BancoTranquiloService.class);

    /**
     * Simula a API de Plataforma de Pagamentos do Banco Tranquilo.
     * Regras de aprovação:
     * - Valor total menor ou igual a 0 -> RECUSADO
     * - Valor total acima de R$ 15.000,00 -> RECUSADO (Limite de segurança excedido)
     * - Cartão com final "0000" -> RECUSADO (Cartão bloqueado / sem saldo para testes de recusa)
     * - Demais casos -> APROVADO com geração de protocolo bancário oficial BT-XXXX
     */
    public TransacaoBancariaResponseDto autorizarTransacao(TransacaoBancariaRequestDto request) {
        log.info("[BANCO TRANQUILO API] Recebendo solicitação de pagamento - Transação: {}, Cliente: {}, Valor: R$ {}",
                request.getCodigoTransacao(), request.getNomeCliente(), request.getValorTotal());

        String protocolo = "BT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Double valor = request.getValorTotal() != null ? request.getValorTotal() : 0.0;
        String cartao = request.getNumeroCartao() != null ? request.getNumeroCartao().trim() : "";

        if (valor <= 0) {
            return new TransacaoBancariaResponseDto(
                    request.getCompraId(),
                    request.getCodigoTransacao(),
                    false,
                    "RECUSADO",
                    protocolo,
                    "Valor da transação inválido.",
                    valor
            );
        }

        if (cartao.endsWith("0000")) {
            log.warn("[BANCO TRANQUILO API] Pagamento RECUSADO (Cartão final 0000 bloqueado) - Transação: {}", request.getCodigoTransacao());
            return new TransacaoBancariaResponseDto(
                    request.getCompraId(),
                    request.getCodigoTransacao(),
                    false,
                    "RECUSADO",
                    protocolo,
                    "Transação não autorizada pelo Banco Tranquilo: Cartão sem saldo ou bloqueado.",
                    valor
            );
        }

        if (valor > 15000.0) {
            log.warn("[BANCO TRANQUILO API] Pagamento RECUSADO (Limite excedido) - Transação: {}", request.getCodigoTransacao());
            return new TransacaoBancariaResponseDto(
                    request.getCompraId(),
                    request.getCodigoTransacao(),
                    false,
                    "RECUSADO",
                    protocolo,
                    "Transação recusada pelo Banco Tranquilo: Valor acima do limite diário de segurança (R$ 15.000,00).",
                    valor
            );
        }

        String mensagem = String.format("Pagamento confirmado com sucesso via %s pelo Banco Tranquilo.",
                request.getMetodoPagamento() != null ? request.getMetodoPagamento() : "PIX");

        log.info("[BANCO TRANQUILO API] Pagamento APROVADO! Protocolo: {} - Transação: {}",
                protocolo, request.getCodigoTransacao());

        return new TransacaoBancariaResponseDto(
                request.getCompraId(),
                request.getCodigoTransacao(),
                true,
                "APROVADO",
                protocolo,
                mensagem,
                valor
        );
    }
}
