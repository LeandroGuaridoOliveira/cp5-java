package br.com.fiap.mercadomvc.service;

import br.com.fiap.mercadomvc.model.Compra;
import br.com.fiap.mercadomvc.model.EmailLog;
import br.com.fiap.mercadomvc.repository.EmailLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    private final EmailLogRepository emailLogRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@mercadoexpress.com.br}")
    private String remetentePadrao;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Autowired
    public EmailNotificationService(EmailLogRepository emailLogRepository,
                                    @Autowired(required = false) JavaMailSender mailSender) {
        this.emailLogRepository = emailLogRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public EmailLog enviarEmailConfirmacaoPagamento(Compra compra) {
        String assunto = "[Mercado Express] Confirmação de Pagamento - Pedido " + compra.getCodigoTransacao();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dataFormatada = compra.getDataConfirmacao() != null
                ? compra.getDataConfirmacao().format(fmt)
                : java.time.LocalDateTime.now().format(fmt);

        String conteudo = String.format(
                "Olá, %s!\n\n" +
                "Sua compra realizada no Mercado Express foi CONFIRMADA pela API do Banco Tranquilo!\n\n" +
                "=== RESUMO DO PAGAMENTO ===\n" +
                "Código da Transação: %s\n" +
                "Protocolo Banco Tranquilo: %s\n" +
                "Método de Pagamento: %s\n" +
                "Itens Comprados: %s\n" +
                "Quantidade Total: %d item(ns)\n" +
                "Valor Total Pago: R$ %.2f\n" +
                "Status: %s (%s)\n" +
                "Data/Hora da Confirmação: %s\n\n" +
                "Obrigado por comprar no Mercado Express!",
                compra.getNomeCliente(),
                compra.getCodigoTransacao(),
                compra.getProtocoloBanco(),
                compra.getMetodoPagamento(),
                compra.getItensResumo(),
                compra.getQuantidadeTotal(),
                compra.getValorTotal(),
                compra.getStatusPagamento(),
                compra.getMensagemBanco(),
                dataFormatada
        );

        String statusEnvio = "REGISTRADO_BD_SIMULADO";
        String detalheTecnico = "E-mail processado e armazenado na tabela TDS_TB_Emails_Enviados (PostgreSQL).";

        if (mailEnabled && mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(remetentePadrao != null && !remetentePadrao.isBlank()
                        ? remetentePadrao : "noreply@mercadoexpress.com.br");
                message.setTo(compra.getEmailCliente());
                message.setSubject(assunto);
                message.setText(conteudo);
                mailSender.send(message);

                statusEnvio = "ENVIADO_SMTP";
                detalheTecnico = "Enviado via servidor SMTP e registrado no PostgreSQL.";
                log.info("[EMAIL-SERVICE] E-mail SMTP enviado com sucesso para {} (Transação: {})",
                        compra.getEmailCliente(), compra.getCodigoTransacao());
            } catch (Exception e) {
                statusEnvio = "ENVIADO_FALLBACK_BD";
                detalheTecnico = "Servidor SMTP indisponível no momento (" + e.getMessage() + "). Registro auditado e salvo no BD PostgreSQL.";
                log.warn("[EMAIL-SERVICE] SMTP offline, salvando confirmação de e-mail no banco de dados para {}: {}",
                        compra.getEmailCliente(), e.getMessage());
            }
        }

        EmailLog emailLog = new EmailLog(
                compra.getId(),
                compra.getCodigoTransacao(),
                compra.getEmailCliente(),
                compra.getNomeCliente(),
                assunto,
                conteudo,
                statusEnvio,
                detalheTecnico
        );

        return emailLogRepository.save(emailLog);
    }

    public List<EmailLog> listarTodosEmails() {
        return emailLogRepository.findAllByOrderByDataEnvioDesc();
    }

    public long totalEmailsEnviados() {
        return emailLogRepository.count();
    }
}
