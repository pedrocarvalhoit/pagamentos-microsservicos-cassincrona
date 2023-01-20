package br.com.blackbeltfood.pagamentos.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItensDoPedido {

    private Long id;
    private Integer quantidade;
    private String descricao;

}
