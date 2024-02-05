package br.com.fiapstore.cobranca.infra.messaging;

import br.com.fiapstore.cobranca.domain.repository.IPagamentoQueueAdapterOUT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PagamentoQueueAdapterOUT implements IPagamentoQueueAdapterOUT {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${queue2.name}")
    private String filaPagamentosPendentes;
    @Value("${queue3.name}")
    private String filaPagamentosConfirmados;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void publishPagamentoPendente(String pagamentoJson) {
        rabbitTemplate.convertAndSend(filaPagamentosPendentes, pagamentoJson);
        logger.debug("Publicação na fila filaPagamentosPendentes executada");
    }

    @Override
    public void publishPagamentoConfirmado(String pagamentoJson) {
        rabbitTemplate.convertAndSend(filaPagamentosConfirmados, pagamentoJson);
        logger.debug("Publicação na fila filaPagamentosConfirmados executada");
    }
}
