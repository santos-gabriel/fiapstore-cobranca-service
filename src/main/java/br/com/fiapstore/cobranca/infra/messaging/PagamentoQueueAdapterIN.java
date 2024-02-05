package br.com.fiapstore.cobranca.infra.messaging;

import br.com.fiapstore.cobranca.application.dto.PagamentoDto;
import br.com.fiapstore.cobranca.domain.repository.IPagamentoQueueAdapterIN;
import br.com.fiapstore.cobranca.domain.usecase.IRegistrarPagamentoUseCase;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PagamentoQueueAdapterIN implements IPagamentoQueueAdapterIN {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private Gson gson;
    private final IRegistrarPagamentoUseCase registrarPagamentoUseCase;

    public PagamentoQueueAdapterIN(IRegistrarPagamentoUseCase registrarPagamentoUseCase) {
        this.registrarPagamentoUseCase = registrarPagamentoUseCase;
    }

    @RabbitListener(queues = {"${queue1.name}"})
    @Override
    public void receive(@Payload String message) {
        HashMap<String, String> menssagem = gson.fromJson(message, HashMap.class);
        PagamentoDto pagamentoDto = fromMessageToDto(menssagem);
        registrarPagamentoUseCase.executar(pagamentoDto);
        logger.debug("Pagamento Registrado", pagamentoDto);
    }

    private static PagamentoDto fromMessageToDto(Map menssagem) {
        return new PagamentoDto(
                null,
                (String)menssagem.get("codigoPedido"),
                (Double)menssagem.get("precoTotal"),
                (Double)menssagem.get("percentualDesconto"),
                (String)menssagem.get("cpf"),
                null,
                null
        );
    }
}
