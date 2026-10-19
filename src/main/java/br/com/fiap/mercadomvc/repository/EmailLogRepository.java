package br.com.fiap.mercadomvc.repository;

import br.com.fiap.mercadomvc.model.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {

    List<EmailLog> findAllByOrderByDataEnvioDesc();

    List<EmailLog> findByCodigoTransacaoOrderByDataEnvioDesc(String codigoTransacao);

    List<EmailLog> findByDestinatarioContainingIgnoreCaseOrderByDataEnvioDesc(String destinatario);
}
