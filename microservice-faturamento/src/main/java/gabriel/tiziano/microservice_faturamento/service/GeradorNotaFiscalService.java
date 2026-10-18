package gabriel.tiziano.microservice_faturamento.service;

import gabriel.tiziano.microservice_faturamento.entity.ItemNotaFiscal;
import gabriel.tiziano.microservice_faturamento.entity.NotaFiscal;
import gabriel.tiziano.microservice_faturamento.exception.GeracaoNotaFiscalException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class GeradorNotaFiscalService {

    private static final String TEMPLATE = "reports/nota-fiscal.jrxml";
    private static final DateTimeFormatter DATA_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private JasperReport relatorioCompilado;

    @PostConstruct
    void compilarTemplate() {
        try (InputStream template = new ClassPathResource(TEMPLATE).getInputStream()) {
            this.relatorioCompilado = JasperCompileManager.compileReport(template);
            log.info("Template da nota fiscal compilado com sucesso");
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao compilar o template da nota fiscal", e);
        }
    }

    public byte[] gerarNotaFiscal(NotaFiscal nota) {
        log.info("Gerando PDF da NF para o pedido: codigoPedido={}", nota.getCodigoPedido());
        try {
            JasperPrint print = JasperFillManager.fillReport(
                    relatorioCompilado,
                    montarParametros(nota),
                    new JRMapCollectionDataSource(montarItens(nota)));
            return JasperExportManager.exportReportToPdf(print);
        } catch (Exception e) {
            throw new GeracaoNotaFiscalException(nota.getCodigoPedido(), e);
        }
    }

    private Map<String, Object> montarParametros(NotaFiscal nota) throws IOException {
        Map<String, Object> p = new HashMap<>();
        p.put("NOME", nota.getDadosCliente().getNome());
        p.put("CPF", nota.getDadosCliente().getCpf());
        p.put("EMAIL", nota.getDadosCliente().getEmail());
        p.put("TELEFONE", nota.getDadosCliente().getTelefone());
        p.put("LOGRADOURO", nota.getEnderecoEntrega().getLogradouro());
        p.put("NUMERO", nota.getEnderecoEntrega().getNumero());
        p.put("BAIRRO", nota.getEnderecoEntrega().getBairro());
        p.put("DATA_PEDIDO", nota.getDataEmissao().format(DATA_FORMATTER));
        p.put("TOTAL_PEDIDO", nota.getValorTotal());
        p.put("LOGO", new ClassPathResource("reports/logo.png").getInputStream());
        return p;
    }

    private Collection<Map<String, ?>> montarItens(NotaFiscal nota) {
        List<Map<String, ?>> linhas = new ArrayList<>();
        for (ItemNotaFiscal item : nota.getItens()) {
            Map<String, Object> linha = new HashMap<>();
            linha.put("codigo", item.getCodigoProduto());
            linha.put("nome", item.getNomeProduto());
            linha.put("quantidade", item.getQuantidade());
            linha.put("valorUnitario", item.getValorUnitario());
            linha.put("total", item.getTotal());
            linhas.add(linha);
        }
        return linhas;
    }
}
