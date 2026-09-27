package gabriel.tiziano.microservice_pedidos.controller;

import gabriel.tiziano.microservice_pedidos.dto.PedidoResponse;
import gabriel.tiziano.microservice_pedidos.exception.PedidoNotFoundException;
import gabriel.tiziano.microservice_pedidos.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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

    private PedidoResponse response(Long codigo) {
        return new PedidoResponse(
                codigo, 1L, 2L, 3,
                new BigDecimal("10.00"), new BigDecimal("30.00"),
                "REALIZADO");
    }

    @Test
    void findAll_deveRetornar200ComLista() throws Exception {
        when(pedidoService.findAllPedidos()).thenReturn(List.of(response(1L)));

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andExpect(jsonPath("$[0].status").value("REALIZADO"));
    }

    @Test
    void findById_quandoExiste_deveRetornar200() throws Exception {
        when(pedidoService.findPedidoById(1L)).thenReturn(response(1L));

        mockMvc.perform(get("/pedidos/{codigo}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(1))
                .andExpect(jsonPath("$.codigoCliente").value(1));
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
        when(pedidoService.createPedido(any())).thenReturn(response(10L));

        String json = """
                {
                  "codigoCliente": 1,
                  "codigoProduto": 2,
                  "quantidade": 3,
                  "valorUnitario": 10.00,
                  "total": 30.00,
                  "status": "REALIZADO"
                }
                """;

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(10));
    }

    @Test
    void create_comDadosInvalidos_deveRetornar400() throws Exception {
        String json = """
                {
                  "codigoCliente": null,
                  "codigoProduto": 2,
                  "quantidade": 0,
                  "valorUnitario": 10.00,
                  "total": 30.00,
                  "status": ""
                }
                """;

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void update_quandoExiste_deveRetornar200() throws Exception {
        when(pedidoService.updatePedido(eq(1L), any())).thenReturn(response(1L));

        String json = """
                {
                  "codigoCliente": 1,
                  "codigoProduto": 2,
                  "quantidade": 3,
                  "valorUnitario": 10.00,
                  "total": 30.00,
                  "status": "PAGO"
                }
                """;

        mockMvc.perform(put("/pedidos/{codigo}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(1));
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