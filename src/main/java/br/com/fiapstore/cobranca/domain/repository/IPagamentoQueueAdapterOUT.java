package br.com.fiapstore.cobranca.domain.repository;


public interface IPagamentoQueueAdapterOUT {
    void publishPagamentoPendente(String pagamentoJson);

    void publishPagamentoConfirmado(String pagamentoJson);

}
