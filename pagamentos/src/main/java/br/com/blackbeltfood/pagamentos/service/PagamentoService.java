package br.com.blackbeltfood.pagamentos.service;

import br.com.blackbeltfood.pagamentos.http.PedidoClient;
import br.com.blackbeltfood.pagamentos.model.Pagamento;
import br.com.blackbeltfood.pagamentos.model.Status;
import br.com.blackbeltfood.pagamentos.dto.PagamentoDto;
import br.com.blackbeltfood.pagamentos.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PedidoClient pedidoClient;

    public Page<PagamentoDto> obterTodosPagamentos(Pageable paginacao){
        return repository.findAll(paginacao)
                //Repository le apenas Pagamento
                //Mapeia o Pagamento para Pagamento DTO
                .map(p-> modelMapper.map(p, PagamentoDto.class));
    }

    public PagamentoDto obterPagamentoPorId(Long id){
        Pagamento pagamentoEncontrado = repository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        PagamentoDto dto = modelMapper.map(pagamentoEncontrado, PagamentoDto.class);
        dto.setItens(pedidoClient.obterItensDoPedido(pagamentoEncontrado.getPedidoId()).getItens());
        return dto;
    }

    public PagamentoDto criarPagamento(PagamentoDto dto){
        Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
        pagamento.setStatus(Status.CRIADO);
        repository.save(pagamento);

        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    /**Neste caso, para atualizar é preciso enviar o id do pagamento a ser atualizado
    e os dados a serem atualizados**/
    public PagamentoDto atualizarPagamento(Long id, PagamentoDto dto){
        Pagamento pagamento = repository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        pagamento.atualizar(dto);
        pagamento = repository.save(pagamento);

        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    public void excluirPagamentoPorId(Long id){
        repository.deleteById(id);
    }

    /**Este método atua nos dois microsserviçoes
     * Primeiro seta confirmado no MS pagamento, e depois
     * inicia o atualizar pagamento, que define pedido pago no MS pedido*/
    public PagamentoDto confirmarPagamento(Long id){
        Optional<Pagamento> pagamento = repository.findById(id);
        if(!pagamento.isPresent()){
            throw new EntityNotFoundException();
        }
        pagamento.get().setStatus(Status.CONFIRMADO);
        repository.save(pagamento.get());
        pedidoClient.atualizarPagamento(pagamento.get().getPedidoId());

        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    //Utilizado caso confirmarPagamento() entre em CB
    public PagamentoDto alterarStatusParaPendente(Long id) {
        Optional<Pagamento> pagamento = repository.findById(id);
        if(!pagamento.isPresent()){
            throw new EntityNotFoundException();
        }
        pagamento.get().setStatus(Status.CONFIRMADO_SEM_INTEGRACAO);
        repository.save(pagamento.get());

        return modelMapper.map(pagamento, PagamentoDto.class);
    }
}
