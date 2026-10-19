package br.com.fiap.mercadomvc.controller;

import br.com.fiap.mercadomvc.config.RabbitMQConfig;
import br.com.fiap.mercadomvc.dto.CompraRequestDto;
import br.com.fiap.mercadomvc.dto.TransacaoBancariaRequestDto;
import br.com.fiap.mercadomvc.dto.TransacaoBancariaResponseDto;
import br.com.fiap.mercadomvc.model.Compra;
import br.com.fiap.mercadomvc.model.EmailLog;
import br.com.fiap.mercadomvc.service.BancoTranquiloService;
import br.com.fiap.mercadomvc.service.EmailNotificationService;
import br.com.fiap.mercadomvc.service.PagamentoMensageriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BancoTranquiloApiController {

    private final BancoTranquiloService bancoTranquiloService;
    private final PagamentoMensageriaService pagamentoMensageriaService;
    private final EmailNotificationService emailNotificationService;

    @Autowired
    public BancoTranquiloApiController(BancoTranquiloService bancoTranquiloService,
                                       PagamentoMensageriaService pagamentoMensageriaService,
                                       EmailNotificationService emailNotificationService) {
        this.bancoTranquiloService = bancoTranquiloService;
        this.pagamentoMensageriaService = pagamentoMensageriaService;
        this.emailNotificationService = emailNotificationService;
    }

    /**
     * Endpoint de Status da API do Banco Tranquilo e configuração RabbitMQ.
     */
    @GetMapping("/banco-tranquilo/status")
    public ResponseEntity<Map<String, Object>> statusBancoTranquilo() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("instituicao", "Banco Tranquilo S.A. - API Plataforma de Pagamentos");
        body.put("status", "ONLINE");
        body.put("broker", "RabbitMQ");
        body.put("exchange", RabbitMQConfig.EXCHANGE_BANCO_TRANQUILO);
        body.put("queueRequisicao", RabbitMQConfig.QUEUE_PAGAMENTO_REQUISICAO);
        body.put("routingKeyRequisicao", RabbitMQConfig.ROUTING_KEY_PAGAMENTO_REQUISICAO);
        body.put("queueResposta", RabbitMQConfig.QUEUE_PAGAMENTO_RESPOSTA);
        body.put("routingKeyResposta", RabbitMQConfig.ROUTING_KEY_PAGAMENTO_RESPOSTA);
        body.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(body);
    }

    /**
     * Endpoint direto da API do Banco Tranquilo para autorização de pagamento.
     */
    @PostMapping("/banco-tranquilo/pagamentos")
    public ResponseEntity<TransacaoBancariaResponseDto> autorizarPagamentoDireto(
            @RequestBody TransacaoBancariaRequestDto requestDto) {
        TransacaoBancariaResponseDto response = bancoTranquiloService.autorizarTransacao(requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint principal de Compra do Mercado Express (para Postman / Insomnia / Scripts de Teste).
     * Recebe um JSON de compra, grava no PostgreSQL, publica na Exchange/Queue do RabbitMQ,
     * integra com a API do Banco Tranquilo e dispara o e-mail de confirmação.
     */
    @PostMapping("/compras")
    public ResponseEntity<Map<String, Object>> realizarCompraApi(@Valid @RequestBody CompraRequestDto compraRequest) {
        Compra compra = pagamentoMensageriaService.realizarCompra(compraRequest);

        // Aguarda brevemente caso o processamento assíncrono via RabbitMQ esteja em andamento
        for (int i = 0; i < 10; i++) {
            Optional<Compra> atualizada = pagamentoMensageriaService.buscarPorId(compra.getId());
            if (atualizada.isPresent() && atualizada.get().getProtocoloBanco() != null) {
                compra = atualizada.get();
                break;
            }
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("mensagem", "Compra processada pelo Mercado Express integrada ao Banco Tranquilo via RabbitMQ!");
        resposta.put("exchangeUtilizada", RabbitMQConfig.EXCHANGE_BANCO_TRANQUILO);
        resposta.put("queueRequisicao", RabbitMQConfig.QUEUE_PAGAMENTO_REQUISICAO);
        resposta.put("queueResposta", RabbitMQConfig.QUEUE_PAGAMENTO_RESPOSTA);
        resposta.put("processadoViaRabbitMQ", pagamentoMensageriaService.isUltimoEnvioViaRabbit());
        resposta.put("compra", compra);

        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    /**
     * Lista todas as compras registradas no banco de dados PostgreSQL.
     */
    @GetMapping("/compras")
    public ResponseEntity<List<Compra>> listarCompras() {
        return ResponseEntity.ok(pagamentoMensageriaService.listarTodasCompras());
    }

    /**
     * Consulta uma compra específica pelo código de transação (ex: TRX-A1B2C3D4).
     */
    @GetMapping("/compras/{codigoTransacao}")
    public ResponseEntity<Compra> buscarPorCodigo(@PathVariable String codigoTransacao) {
        return pagamentoMensageriaService.buscarPorCodigoTransacao(codigoTransacao)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lista todos os e-mails de confirmação armazenados no PostgreSQL (TDS_TB_Emails_Enviados).
     */
    @GetMapping("/emails")
    public ResponseEntity<List<EmailLog>> listarEmailsEnviados() {
        return ResponseEntity.ok(emailNotificationService.listarTodosEmails());
    }
}
