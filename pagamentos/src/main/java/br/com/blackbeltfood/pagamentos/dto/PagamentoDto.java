package br.com.blackbeltfood.pagamentos.dto;


import br.com.blackbeltfood.pagamentos.model.ItensDoPedido;
import br.com.blackbeltfood.pagamentos.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoDto {

        private Long id;
        private BigDecimal valor;
        private String nome;
        private String numero;
        private String expiracao;
        private String codigo;
        private Status status;
        private Long formaDePagamentoId;
        private Long pedidoId;
        private List<ItensDoPedido> itens;

}
