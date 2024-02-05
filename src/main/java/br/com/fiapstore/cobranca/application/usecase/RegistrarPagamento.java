package br.com.fiapstore.cobranca.application.usecase;

import br.com.fiapstore.cobranca.application.dto.PagamentoDto;
import br.com.fiapstore.cobranca.domain.entity.Pagamento;
import br.com.fiapstore.cobranca.domain.repository.IPagamentoDatabaseAdapter;
import br.com.fiapstore.cobranca.domain.repository.IPagamentoQueueAdapterOUT;
import br.com.fiapstore.cobranca.domain.usecase.IRegistrarPagamentoUseCase;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class RegistrarPagamento implements IRegistrarPagamentoUseCase {


    private final IPagamentoDatabaseAdapter iPagamentoDatabaseAdapter;
    private final IPagamentoQueueAdapterOUT pagamentoQueueAdapterOUT;
    @Autowired
    public RegistrarPagamento(IPagamentoDatabaseAdapter iPagamentoDatabaseAdapter, IPagamentoQueueAdapterOUT pagamentoQueueAdapterOUT){
        this.iPagamentoDatabaseAdapter = iPagamentoDatabaseAdapter;
        this.pagamentoQueueAdapterOUT = pagamentoQueueAdapterOUT;

    }
    @Transactional
    public PagamentoDto executar(PagamentoDto pagamentoDto) {

        Pagamento pagamento = new Pagamento(pagamentoDto.getCodigoPedido(), pagamentoDto.getValor(), pagamentoDto.getPercentualDesconto(), pagamentoDto.getCpf());
        pagamento = iPagamentoDatabaseAdapter.save(pagamento);
        pagamentoQueueAdapterOUT.publishPagamentoPendente(toMessage(pagamento));
        return PagamentoDto.toPagamentoDto(pagamento);
    }

    private static String toMessage(Pagamento pagamento){
        Map message = new HashMap<String, String>();
        message.put("codigoPagamento", pagamento.getCodigo());
        message.put("codigoPedido", pagamento.getCodigoPedido());
        message.put("cpf", pagamento.getCpf());
        return new Gson().toJson(message);
    }

}
