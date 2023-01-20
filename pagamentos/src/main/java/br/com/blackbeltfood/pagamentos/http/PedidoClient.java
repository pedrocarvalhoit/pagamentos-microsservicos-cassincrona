package br.com.blackbeltfood.pagamentos.http;

import br.com.blackbeltfood.pagamentos.model.Pedido;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//Mapeando para o MS pedidos
@FeignClient("pedidos-ms")
public interface PedidoClient {

    //Envia uma PUT para passar o pedido como PAGO no MS pedidos
    @RequestMapping(method = RequestMethod.PUT, value = "/pedidos/aprovarPagamento/{id}")
    void atualizarPagamento(@PathVariable Long id);

    @RequestMapping(method = RequestMethod.GET, value = "/pedidos/procurar/{id}")
    Pedido obterItensDoPedido(@PathVariable Long id);

}
