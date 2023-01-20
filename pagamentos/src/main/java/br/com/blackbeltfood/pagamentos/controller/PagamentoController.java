package br.com.blackbeltfood.pagamentos.controller;

import br.com.blackbeltfood.pagamentos.model.Pagamento;
import br.com.blackbeltfood.pagamentos.service.PagamentoService;
import br.com.blackbeltfood.pagamentos.dto.PagamentoDto;
import com.netflix.discovery.converters.Auto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService service;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("listar")
    public Page<PagamentoDto> listar(@PageableDefault(size = 30) Pageable paginacao){
        return service.obterTodosPagamentos(paginacao);
    }

    //O PathVariable liga o id no {id} que é a variável digitada no navegador
    @GetMapping("procurar/{id}")
    public ResponseEntity<PagamentoDto> procurarPagamentoPorId(@PathVariable("id") @NotNull Long id){
        PagamentoDto dto = service.obterPagamentoPorId(id);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<PagamentoDto> cadastrar(@RequestBody @Valid PagamentoDto dto, UriComponentsBuilder uriBuilder){
        PagamentoDto pagamento = service.criarPagamento(dto);
        URI endereco = uriBuilder.path("/pagamento/{id}").buildAndExpand(pagamento.getId()).toUri();
        // Produção de mensagens
        rabbitTemplate.convertAndSend("pagamentos.ex","",pagamento);

        return ResponseEntity.created(endereco).body(pagamento);
    }

    @PutMapping("atualizar/{id}")
    public ResponseEntity<PagamentoDto> atualizarPagamento(@PathVariable("id") @NotNull Long id, @RequestBody PagamentoDto dto){
        PagamentoDto pagamentoAtualizado = service.atualizarPagamento(id,dto);
        return ResponseEntity.ok(pagamentoAtualizado);
    }

    @DeleteMapping("deletar/{id}")
    public ResponseEntity<PagamentoDto> deletar(@PathVariable("id") @NotNull Long id){
        service.excluirPagamentoPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/confirmar/{id}")
    @CircuitBreaker(name = "atualizaPedido", fallbackMethod = "pagamentoAutorizadoComIntegracaoPendente")//Lida com o erro
    public ResponseEntity<PagamentoDto> confirmarPagamento(@PathVariable @NotNull Long id){
        PagamentoDto pagamentoConfirmado = service.confirmarPagamento(id);
        return ResponseEntity.ok(pagamentoConfirmado);
    }

    public ResponseEntity<PagamentoDto> pagamentoAutorizadoComIntegracaoPendente(Long id, Exception e){
        PagamentoDto pagamentoAlterado = service.alterarStatusParaPendente(id);
        return ResponseEntity.ok(pagamentoAlterado);
    }

}

