package br.com.blackbeltfood.pagamentos.model;

import br.com.blackbeltfood.pagamentos.dto.PagamentoDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "pagamentos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Positive
    private BigDecimal valor;

    @NotBlank
    @Size(max = 100)
    private String nome;

    @NotBlank
    @Size(max = 19)
    private String numero;

    @NotBlank
    @Size(max = 7)
    private String expiracao;

    @NotBlank
    @Size(min =3, max = 3)
    private String codigo;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    private Long pedidoId;

    @NotNull
    private Long formaDePagamentoId;

    public void excluir() {
        setStatus(Status.CANCELADO);
    }

    public void atualizar(PagamentoDto dto) {
        this.valor = dto.getValor();
        this.nome = dto.getNome();
        this.numero = dto.getNumero();
        this.expiracao = dto.getExpiracao();
        this.codigo = dto.getCodigo();
        this.formaDePagamentoId = dto.getFormaDePagamentoId();
        this.pedidoId = dto.getPedidoId();

    }
}
