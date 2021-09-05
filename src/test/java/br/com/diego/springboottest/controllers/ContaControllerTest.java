package br.com.diego.springboottest.controllers;

import br.com.diego.springboottest.models.TransacaoDto;
import br.com.diego.springboottest.services.ContaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

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

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
    }

    @Test
    void testDetalhe() throws Exception{
        // Given
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

    @Test
    void testTransferir() throws Exception{
        // Given
        TransacaoDto dto = getTransacaoDtoMock();
        // When
        mockMvc.perform(post("/api/contas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.mensagem").value("Transferência realizada com sucesso!"))
                .andExpect(jsonPath("$.transacao.contaOrigemId").value(dto.getContaOrigemId()));

    }

}