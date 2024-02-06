package br.com.fiapstore.cobranca.application.usecase;

import br.com.fiapstore.cobranca.application.dto.PagamentoDto;
import br.com.fiapstore.cobranca.domain.entity.Pagamento;
import br.com.fiapstore.cobranca.domain.exception.OperacaoInvalidaException;
import br.com.fiapstore.cobranca.domain.exception.PagamentoNaoEncontradoException;
import br.com.fiapstore.cobranca.domain.repository.IPagamentoDatabaseAdapter;
import br.com.fiapstore.cobranca.domain.repository.IPagamentoQueueAdapterOUT;
import br.com.fiapstore.cobranca.domain.usecase.IConfirmarPagamentoUseCase;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class ConfirmarPagamento implements IConfirmarPagamentoUseCase {

    private final IPagamentoDatabaseAdapter iPagamentoDatabaseAdapter;
    private final IPagamentoQueueAdapterOUT pagamentoQueueAdapterOUT;

    public ConfirmarPagamento(IPagamentoDatabaseAdapter iPagamentoDatabaseAdapter, IPagamentoQueueAdapterOUT pagamentoQueueAdapterOUT) {
        this.iPagamentoDatabaseAdapter = iPagamentoDatabaseAdapter;
        this.pagamentoQueueAdapterOUT = pagamentoQueueAdapterOUT;
    }


    @Transactional
    public PagamentoDto executar(String codigoPagamento) throws PagamentoNaoEncontradoException, OperacaoInvalidaException {
        Pagamento pagamento = null;

        pagamento = this.iPagamentoDatabaseAdapter.findByCodigo(codigoPagamento);

        if(pagamento==null) throw new PagamentoNaoEncontradoException("Pagamento não encontrado");

        pagamento.confirmar();

        pagamento = this.iPagamentoDatabaseAdapter.save(pagamento);
        this.pagamentoQueueAdapterOUT.publishPagamentoConfirmado(toMessage(pagamento));

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
