package br.com.blackbeltfood.pedidos.controller;

import br.com.blackbeltfood.pedidos.dto.PedidoDto;
import br.com.blackbeltfood.pedidos.dto.StatusDto;
import br.com.blackbeltfood.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

        @Autowired
        private PedidoService service;

        @GetMapping("/listar")
        public List<PedidoDto> listarTodos() {
            return service.obterTodos();
        }

        @GetMapping("procurar/{id}")
        public ResponseEntity<PedidoDto> listarPorId(@PathVariable @NotNull Long id) {
            PedidoDto dto = service.obterPorId(id);

            return  ResponseEntity.ok(dto);
        }

        @GetMapping("/porta")
        //O Value pega o resultado gerado na variável de hambiente, que foi programada para gerar um número random
        public String retornaPorta(@Value("${local.server.port}") String porta){
            return String.format("Requisição respondida pela instância executando na porta %s", porta);
        }

        @PostMapping("realizarNovo")
        public ResponseEntity<PedidoDto> realizaPedido(@RequestBody @Valid PedidoDto dto, UriComponentsBuilder uriBuilder) {
            PedidoDto pedidoRealizado = service.criarPedido(dto);

            URI endereco = uriBuilder.path("/pedidos/{id}").buildAndExpand(pedidoRealizado.getId()).toUri();

            return ResponseEntity.created(endereco).body(pedidoRealizado);

        }

        @PutMapping("atualizarStatus/{id}")
        public ResponseEntity<PedidoDto> atualizaStatus(@PathVariable Long id, @RequestBody StatusDto status){
           PedidoDto dto = service.atualizaStatus(id, status);

            return ResponseEntity.ok(dto);
        }


        @PutMapping("aprovarPagamento/{id}")
        public ResponseEntity<Void> aprovaPagamento(@PathVariable @NotNull Long id) {
            service.aprovaPagamentoPedido(id);
            return ResponseEntity.ok().build();

        }
}
