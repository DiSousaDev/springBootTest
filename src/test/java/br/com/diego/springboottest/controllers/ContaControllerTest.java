package br.com.diego.springboottest.controllers;

import br.com.diego.springboottest.services.ContaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.*;
import static br.com.diego.springboottest.Dados.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContaController.class)
class ContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContaService contaService;

    @Test
    void testDetalhe() throws Exception{
        //Given
        when(contaService.buscarPorId(1L)).thenReturn(conta001().orElseThrow());

        // When
        mockMvc.perform(get("/api/contas/1").contentType(MediaType.APPLICATION_JSON))
        // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cliente").value("Carlos Silva"))
                .andExpect(jsonPath("$.saldo").value("1000"));

        verify(contaService).buscarPorId(1L);
    }
}