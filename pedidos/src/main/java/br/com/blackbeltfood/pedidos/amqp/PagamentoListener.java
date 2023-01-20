package br.com.blackbeltfood.pedidos.amqp;

import br.com.blackbeltfood.pedidos.dto.PagamentoDto;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PagamentoListener {

    //Escolhendo a fila a ser lida
    @RabbitListener(queues = "pagamentos.detalhes-pedido")
    public void recebeMessagem(PagamentoDto pagamento){
        String mensagem = """
                Dados do pagamento: %s
                Número do pedido: %s
                Valor R$: %s
                Status: %s
                """.formatted(pagamento.getId(), pagamento.getPedidoId(),
                        pagamento.getValor(), pagamento.getStatusPagamento());

        System.out.println("Recebi a mensagem " + mensagem);
    }


}
