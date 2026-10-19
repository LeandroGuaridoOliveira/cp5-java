package br.com.fiap.mercadomvc.service;

import br.com.fiap.mercadomvc.config.RabbitMQConfig;
import br.com.fiap.mercadomvc.dto.CompraRequestDto;
import br.com.fiap.mercadomvc.dto.ItemCompraDto;
import br.com.fiap.mercadomvc.dto.TransacaoBancariaRequestDto;
import br.com.fiap.mercadomvc.dto.TransacaoBancariaResponseDto;
import br.com.fiap.mercadomvc.model.Compra;
import br.com.fiap.mercadomvc.model.Produto;
import br.com.fiap.mercadomvc.model.StatusPagamento;
import br.com.fiap.mercadomvc.repository.CompraRepository;
import br.com.fiap.mercadomvc.repository.ProdutoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PagamentoMensageriaService {

    private static final Logger log = LoggerFactory.getLogger(PagamentoMensageriaService.class);

    private final CompraRepository compraRepository;
    private final ProdutoRepository produtoRepository;
    private final BancoTranquiloService bancoTranquiloService;
    private final EmailNotificationService emailNotificationService;
    private final RabbitTemplate rabbitTemplate;

    private volatile boolean ultimoEnvioViaRabbit = false;

    @Autowired
    public PagamentoMensageriaService(CompraRepository compraRepository,
                                      ProdutoRepository produtoRepository,
                                      BancoTranquiloService bancoTranquiloService,
                                      EmailNotificationService emailNotificationService,
                                      @Autowired(required = false) RabbitTemplate rabbitTemplate) {
        this.compraRepository = compraRepository;
        this.produtoRepository = produtoRepository;
        this.bancoTranquiloService = bancoTranquiloService;
        this.emailNotificationService = emailNotificationService;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Registra uma nova compra no Mercado Express e publica a requisição de pagamento
     * na Exchange/Queue do RabbitMQ para processamento pela Integração de Pagamentos com o Banco Tranquilo.
     */
    @Transactional
    public Compra realizarCompra(CompraRequestDto requestDto) {
        String codigoTransacao = "TRX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        double valorTotal = 0.0;
        int quantidadeTotal = 0;
        List<String> descricoes = new ArrayList<>();

        for (ItemCompraDto item : requestDto.getItens()) {
            Optional<Produto> prodOpt = produtoRepository.findById(item.getProdutoId());
            if (prodOpt.isPresent()) {
                Produto prod = prodOpt.get();
                int qtd = item.getQuantidade() != null && item.getQuantidade() > 0 ? item.getQuantidade() : 1;
                double subtotal = prod.getPreco() * qtd;
                valorTotal += subtotal;
                quantidadeTotal += qtd;
                descricoes.add(qtd + "x " + prod.getNome() + " (R$ " + String.format("%.2f", prod.getPreco()) + ")");

                // Atualiza estoque do produto
                int novoEstoque = Math.max(0, prod.getEstoque() - qtd);
                prod.setEstoque(novoEstoque);
                produtoRepository.save(prod);
            } else if (item.getPrecoUnitario() != null && item.getPrecoUnitario() > 0) {
                int qtd = item.getQuantidade() != null && item.getQuantidade() > 0 ? item.getQuantidade() : 1;
                double subtotal = item.getPrecoUnitario() * qtd;
                valorTotal += subtotal;
                quantidadeTotal += qtd;
                String nome = item.getNomeProduto() != null ? item.getNomeProduto() : "Produto #" + item.getProdutoId();
                descricoes.add(qtd + "x " + nome + " (R$ " + String.format("%.2f", item.getPrecoUnitario()) + ")");
            }
        }

        if (descricoes.isEmpty()) {
            throw new IllegalArgumentException("Nenhum produto válido encontrado para a compra.");
        }

        String resumoItens = String.join(", ", descricoes);
        // Margem de lucro estimada de 35% sobre o valor de venda para o Dashboard Gerencial
        double lucroEstimado = Math.round((valorTotal * 0.35) * 100.0) / 100.0;

        String finalCartao = null;
        if (requestDto.getNumeroCartao() != null && !requestDto.getNumeroCartao().isBlank()) {
            String limpo = requestDto.getNumeroCartao().replaceAll("\\s+", "");
            finalCartao = limpo.length() >= 4 ? limpo.substring(limpo.length() - 4) : limpo;
        }

        Compra compra = new Compra();
        compra.setCodigoTransacao(codigoTransacao);
        compra.setNomeCliente(requestDto.getNomeCliente());
        compra.setEmailCliente(requestDto.getEmailCliente());
        compra.setCpfCliente(requestDto.getCpfCliente());
        compra.setMetodoPagamento(requestDto.getMetodoPagamento().toUpperCase());
        compra.setNumeroCartaoFinal(finalCartao);
        compra.setItensResumo(resumoItens);
        compra.setQuantidadeTotal(quantidadeTotal);
        compra.setValorTotal(Math.round(valorTotal * 100.0) / 100.0);
        compra.setLucroEstimado(lucroEstimado);
        compra.setStatusPagamento(StatusPagamento.PROCESSANDO_FILA);
        compra.setDataCompra(LocalDateTime.now());

        compra = compraRepository.save(compra);

        TransacaoBancariaRequestDto transacaoRequest = new TransacaoBancariaRequestDto(
                compra.getId(),
                compra.getCodigoTransacao(),
                compra.getNomeCliente(),
                compra.getEmailCliente(),
                compra.getCpfCliente(),
                compra.getMetodoPagamento(),
                requestDto.getNumeroCartao(),
                compra.getValorTotal(),
                compra.getItensResumo()
        );

        enviarParaFilaRabbitMQ(transacaoRequest);

        // Recarrega do banco caso o processamento síncrono/assíncrono já tenha atualizado
        return compraRepository.findById(compra.getId()).orElse(compra);
    }

    /**
     * Publica a mensagem na Exchange do RabbitMQ (Queue de Requisição).
     * Caso o broker RabbitMQ não esteja acessível no momento, executa o fluxo diretamente
     * para garantir alta disponibilidade em testes e demonstrações.
     */
    public void enviarParaFilaRabbitMQ(TransacaoBancariaRequestDto requestDto) {
        if (rabbitTemplate != null) {
            try {
                log.info("[RABBITMQ PRODUCER] Publicando requisição de pagamento na Exchange '{}' com RoutingKey '{}' -> Transação: {}",
                        RabbitMQConfig.EXCHANGE_BANCO_TRANQUILO,
                        RabbitMQConfig.ROUTING_KEY_PAGAMENTO_REQUISICAO,
                        requestDto.getCodigoTransacao());

                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.EXCHANGE_BANCO_TRANQUILO,
                        RabbitMQConfig.ROUTING_KEY_PAGAMENTO_REQUISICAO,
                        requestDto
                );
                this.ultimoEnvioViaRabbit = true;
                return;
            } catch (Exception e) {
                log.warn("[RABBITMQ FALLBACK] Broker RabbitMQ indisponível ({}). Executando integração direta com API Banco Tranquilo.",
                        e.getMessage());
                this.ultimoEnvioViaRabbit = false;
            }
        }

        // Fallback direto caso RabbitMQ esteja offline
        TransacaoBancariaResponseDto response = bancoTranquiloService.autorizarTransacao(requestDto);
        processarRespostaPagamento(response);
    }

    /**
     * Consumer 1 (Integração de Pagamentos):
     * Consome a fila 'banco.tranquilo.pagamento.requisicao.queue', chama a API do Banco Tranquilo
     * e publica a resposta na fila 'banco.tranquilo.pagamento.resposta.queue'.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_PAGAMENTO_REQUISICAO)
    public void consumirRequisicaoPagamento(TransacaoBancariaRequestDto requestDto) {
        log.info("[RABBITMQ CONSUMER - INTEGRAÇÃO PAGAMENTOS] Mensagem recebida da fila '{}': Transação {}",
                RabbitMQConfig.QUEUE_PAGAMENTO_REQUISICAO, requestDto.getCodigoTransacao());

        TransacaoBancariaResponseDto responseDto = bancoTranquiloService.autorizarTransacao(requestDto);

        try {
            log.info("[RABBITMQ PRODUCER - INTEGRAÇÃO PAGAMENTOS] Enviando resposta do Banco Tranquilo para fila '{}' -> Status: {}",
                    RabbitMQConfig.QUEUE_PAGAMENTO_RESPOSTA, responseDto.getStatus());

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_BANCO_TRANQUILO,
                    RabbitMQConfig.ROUTING_KEY_PAGAMENTO_RESPOSTA,
                    responseDto
            );
        } catch (Exception e) {
            log.warn("[RABBITMQ FALLBACK RESPOSTA] Erro ao publicar na fila de resposta: {}. Processando diretamente.", e.getMessage());
            processarRespostaPagamento(responseDto);
        }
    }

    /**
     * Consumer 2 (Back End API - Mercado Express):
     * Consome a fila 'banco.tranquilo.pagamento.resposta.queue', atualiza o status da compra no PostgreSQL
     * e, caso confirmada pela API do Banco Tranquilo, dispara e registra o e-mail de confirmação para o cliente.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_PAGAMENTO_RESPOSTA)
    @Transactional
    public void processarRespostaPagamento(TransacaoBancariaResponseDto responseDto) {
        log.info("[RABBITMQ CONSUMER - MERCADO EXPRESS] Resposta recebida da fila '{}': Transação {}, Status: {}, Protocolo: {}",
                RabbitMQConfig.QUEUE_PAGAMENTO_RESPOSTA,
                responseDto.getCodigoTransacao(),
                responseDto.getStatus(),
                responseDto.getProtocoloBanco());

        Optional<Compra> compraOpt = Optional.empty();
        if (responseDto.getCompraId() != null) {
            compraOpt = compraRepository.findById(responseDto.getCompraId());
        }
        if (compraOpt.isEmpty() && responseDto.getCodigoTransacao() != null) {
            compraOpt = compraRepository.findByCodigoTransacao(responseDto.getCodigoTransacao());
        }

        if (compraOpt.isEmpty()) {
            log.warn("[MERCADO EXPRESS] Compra não encontrada para atualização de pagamento: {}", responseDto.getCodigoTransacao());
            return;
        }

        Compra compra = compraOpt.get();
        compra.setProtocoloBanco(responseDto.getProtocoloBanco());
        compra.setMensagemBanco(responseDto.getMensagem());
        compra.setDataConfirmacao(LocalDateTime.now());

        if (responseDto.isAprovado()) {
            compra.setStatusPagamento(StatusPagamento.APROVADO);
            compra = compraRepository.save(compra);

            // Caso a compra realizada no Mercado Express seja confirmada pela API do Banco Tranquilo,
            // envia um e-mail para a pessoa que efetuou a compra e armazena no BD PostgreSQL
            if (!compra.isEmailEnviado()) {
                emailNotificationService.enviarEmailConfirmacaoPagamento(compra);
                compra.setEmailEnviado(true);
                compraRepository.save(compra);
            }
        } else {
            compra.setStatusPagamento(StatusPagamento.RECUSADO);
            compraRepository.save(compra);
        }
    }

    public boolean isUltimoEnvioViaRabbit() {
        return ultimoEnvioViaRabbit;
    }

    public List<Compra> listarTodasCompras() {
        return compraRepository.findAllByOrderByDataCompraDesc();
    }

    public Optional<Compra> buscarPorCodigoTransacao(String codigo) {
        return compraRepository.findByCodigoTransacao(codigo);
    }

    public Optional<Compra> buscarPorId(Long id) {
        return compraRepository.findById(id);
    }
}
