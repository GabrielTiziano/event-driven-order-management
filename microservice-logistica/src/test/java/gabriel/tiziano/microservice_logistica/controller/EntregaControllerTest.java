package gabriel.tiziano.microservice_logistica.controller;

import gabriel.tiziano.microservice_logistica.entity.StatusEntrega;
import gabriel.tiziano.microservice_logistica.exception.EntregaNaoEncontradaException;
import gabriel.tiziano.microservice_logistica.exception.EstadoEntregaInvalidoException;
import gabriel.tiziano.microservice_logistica.service.LogisticaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EntregaController.class)
class EntregaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogisticaService logisticaService;

    @Test
    void despacharDeveRetornar200ComOCodigoRastreio() throws Exception {
        when(logisticaService.despachar(1L)).thenReturn("LG123456789BR");

        mockMvc.perform(post("/entregas/{codigoPedido}/despachar", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoPedido").value(1))
                .andExpect(jsonPath("$.codigoRastreio").value("LG123456789BR"));
    }

    @Test
    void despacharDeveRetornar404QuandoEntregaNaoExiste() throws Exception {
        doThrow(new EntregaNaoEncontradaException(99L)).when(logisticaService).despachar(99L);

        mockMvc.perform(post("/entregas/{codigoPedido}/despachar", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void despacharDeveRetornar409QuandoEstadoInvalido() throws Exception {
        doThrow(new EstadoEntregaInvalidoException(1L, StatusEntrega.DESPACHADO))
                .when(logisticaService).despachar(1L);

        mockMvc.perform(post("/entregas/{codigoPedido}/despachar", 1L))
                .andExpect(status().isConflict());
    }
}