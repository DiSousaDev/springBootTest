package br.com.diego.springboottest.controllers;

import br.com.diego.springboottest.models.Conta;
import br.com.diego.springboottest.models.TransacaoDto;
import br.com.diego.springboottest.services.ContaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.diego.springboottest.Dados.conta001;
import static br.com.diego.springboottest.Dados.conta002;
import static br.com.diego.springboottest.Dados.getTransacaoDtoMock;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        System.out.println(">>> Exibindo dto de entrada: " + objectMapper.writeValueAsString(dto));

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", LocalDate.now().toString());
        responseBody.put("status", "OK");
        responseBody.put("mensagem", "Transfer??ncia realizada com sucesso!");
        responseBody.put("transacao", dto);
        System.out.println(">>> Exibindo mapper de retorno" + objectMapper.writeValueAsString(responseBody));

        // When
        mockMvc.perform(post("/api/contas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.mensagem").value("Transfer??ncia realizada com sucesso!"))
                .andExpect(jsonPath("$.transacao.contaOrigemId").value(dto.getContaOrigemId()))
                .andExpect(content().json(objectMapper.writeValueAsString(responseBody)));

    }

    @Test
    void testListarTodas() throws Exception{
        // Given
        List<Conta> contas = Arrays.asList(conta001().orElseThrow(), conta002().orElseThrow());
        when(contaService.buscarTodas()).thenReturn(contas);

        // When
        mockMvc.perform(get("/api/contas")
                .contentType(MediaType.APPLICATION_JSON))
        // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].cliente").value("Carlos Silva"))
                .andExpect(jsonPath("$[1].cliente").value("Maria Rita"))
                .andExpect(jsonPath("$[0].saldo").value("1000"))
                .andExpect(jsonPath("$[1].saldo").value("2000"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(contas)));
        verify(contaService).buscarTodas();

    }

    @Test
    void testSalvar() throws Exception {
        // Given
        Conta conta = new Conta(null, "Jo??o", new BigDecimal("3000"));
        when(contaService.salvar(any())).then(invocation -> {
            Conta c = invocation.getArgument(0);
            c.setId(3L);
            return c;
        });

        // When
        mockMvc.perform(post("/api/contas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(conta)))
        // Then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.cliente", is("Jo??o")))
                .andExpect(jsonPath("$.saldo", is(3000)));
        verify(contaService).salvar(any());
    }
}