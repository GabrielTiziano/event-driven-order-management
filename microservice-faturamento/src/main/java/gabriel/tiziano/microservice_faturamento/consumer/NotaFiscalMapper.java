package gabriel.tiziano.microservice_faturamento.consumer;

import gabriel.tiziano.microservice_faturamento.consumer.representation.ItemRepresentation;
import gabriel.tiziano.microservice_faturamento.consumer.representation.PedidoPagoRepresentation;
import gabriel.tiziano.microservice_faturamento.entity.*;

import java.time.LocalDateTime;

public class NotaFiscalMapper {

    private NotaFiscalMapper() {
    }

    public static NotaFiscal toNotaFiscal(PedidoPagoRepresentation evento) {
        NotaFiscal nota = new NotaFiscal();
        nota.setCodigoPedido(evento.codigoPedido());
        nota.setCodigoCliente(evento.codigoCliente());
        nota.setDadosCliente(new DadosCliente(
                evento.nome(),
                evento.cpf(),
                evento.email(),
                evento.telefone()
        ));
        nota.setEnderecoEntrega(new EnderecoEntrega(
                evento.logradouro(),
                evento.numero(),
                evento.bairro()
        ));
        nota.setValorTotal(evento.valorTotal());
        nota.setStatus(StatusNotaFiscal.PROCESSANDO);
        nota.setDataEmissao(LocalDateTime.now());

        evento.itens().stream()
                .map(NotaFiscalMapper::toItemNotaFiscal)
                .forEach(nota::addItem);

        return nota;
    }

    private static ItemNotaFiscal toItemNotaFiscal(ItemRepresentation item) {
        ItemNotaFiscal itemNota = new ItemNotaFiscal();
        itemNota.setCodigoProduto(item.codigoProduto());
        itemNota.setNomeProduto(item.nomeProduto());
        itemNota.setQuantidade(item.quantidade());
        itemNota.setValorUnitario(item.valorUnitario());
        return itemNota;
    }
}