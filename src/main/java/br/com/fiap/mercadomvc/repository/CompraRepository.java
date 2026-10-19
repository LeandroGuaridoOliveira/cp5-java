package br.com.fiap.mercadomvc.repository;

import br.com.fiap.mercadomvc.model.Compra;
import br.com.fiap.mercadomvc.model.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    Optional<Compra> findByCodigoTransacao(String codigoTransacao);

    List<Compra> findAllByOrderByDataCompraDesc();

    List<Compra> findByStatusPagamentoOrderByDataCompraDesc(StatusPagamento statusPagamento);

    long countByStatusPagamento(StatusPagamento statusPagamento);
}
