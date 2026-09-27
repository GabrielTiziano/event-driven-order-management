package gabriel.tiziano.microservice_pedidos.controller;

import gabriel.tiziano.microservice_pedidos.dto.PedidoItemResponse;
import gabriel.tiziano.microservice_pedidos.dto.PedidoResponse;
import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;
import gabriel.tiziano.microservice_pedidos.exception.PedidoNotFoundException;
import gabriel.tiziano.microservice_pedidos.exception.TransicaoStatusInvalidaException;
import gabriel.tiziano.microservice_pedidos.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    private PedidoResponse response(Long codigo, StatusPedido status) {
        return new PedidoResponse(
                codigo, 1L, LocalDateTime.now(), status, new BigDecimal("35.00"),
                null, "obs", null, null,
                List.of(new PedidoItemResponse(500L, 10L, 2, new BigDecimal("10.00"))));
    }

    @Test
    void findAll_deveRetornar200ComLista() throws Exception {
        when(pedidoService.findAllPedidos()).thenReturn(List.of(response(1L, StatusPedido.REALIZADO)));

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andExpect(jsonPath("$[0].itens.length()").value(1));
    }

    @Test
    void findById_quandoExiste_deveRetornar200() throws Exception {
        when(pedidoService.findPedidoById(1L)).thenReturn(response(1L, StatusPedido.REALIZADO));

        mockMvc.perform(get("/pedidos/{codigo}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(1))
                .andExpect(jsonPath("$.status").value("REALIZADO"));
    }

    @Test
    void findById_quandoNaoExiste_deveRetornar404() throws Exception {
        when(pedidoService.findPedidoById(99L)).thenThrow(new PedidoNotFoundException(99L));

        mockMvc.perform(get("/pedidos/{codigo}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void create_comDadosValidos_deveRetornar201() throws Exception {
        when(pedidoService.createPedido(any())).thenReturn(response(10L, StatusPedido.REALIZADO));

        String json = """
                {
                  "codigoCliente": 1,
                  "observacoes": "obs",
                  "itens": [
                    { "codigoProduto": 10, "quantidade": 2, "valorUnitario": 10.00 }
                  ]
                }
                """;

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(10));
    }

    @Test
    void create_semItens_deveRetornar400() throws Exception {
        String json = """
                {
                  "codigoCliente": 1,
                  "observacoes": "obs",
                  "itens": []
                }
                """;

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updateStatus_comTransicaoValida_deveRetornar200() throws Exception {
        when(pedidoService.updateStatus(eq(1L), any())).thenReturn(response(1L, StatusPedido.PAGO));

        String json = """
                { "status": "PAGO" }
                """;

        mockMvc.perform(patch("/pedidos/{codigo}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAGO"));
    }

    @Test
    void updateStatus_comTransicaoInvalida_deveRetornar409() throws Exception {
        when(pedidoService.updateStatus(eq(1L), any()))
                .thenThrow(new TransicaoStatusInvalidaException(StatusPedido.REALIZADO, StatusPedido.ENVIADO));

        String json = """
                { "status": "ENVIADO" }
                """;

        mockMvc.perform(patch("/pedidos/{codigo}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void updateStatus_comStatusInexistente_deveRetornar400() throws Exception {
        String json = """
                { "status": "ABACAXI" }
                """;

        mockMvc.perform(patch("/pedidos/{codigo}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void delete_quandoExiste_deveRetornar204() throws Exception {
        doNothing().when(pedidoService).deletePedido(1L);

        mockMvc.perform(delete("/pedidos/{codigo}", 1L))
                .andExpect(status().isNoContent());

        verify(pedidoService).deletePedido(1L);
    }

    @Test
    void delete_quandoNaoExiste_deveRetornar404() throws Exception {
        doThrow(new PedidoNotFoundException(99L)).when(pedidoService).deletePedido(99L);

        mockMvc.perform(delete("/pedidos/{codigo}", 99L))
                .andExpect(status().isNotFound());
    }
}