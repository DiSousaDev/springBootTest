package br.com.diego.springboottest.controllers;

import br.com.diego.springboottest.models.Conta;
import br.com.diego.springboottest.models.TransacaoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.diego.springboottest.Dados.conta001;
import static br.com.diego.springboottest.Dados.conta002;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class ContaControllerWebTestClientTest {

    private ObjectMapper objectMapper;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(2)
    void testTransferir() throws JsonProcessingException {
        // Given
        TransacaoDto dto = new TransacaoDto();
        dto.setContaOrigemId(1L);
        dto.setContaDestinoId(2L);
        dto.setBancoId(1L);
        dto.setValor(new BigDecimal("100"));

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", LocalDate.now().toString());
        responseBody.put("status", "OK");
        responseBody.put("mensagem", "Transferência realizada com sucesso!");
        responseBody.put("transacao", dto);

        // When
        webTestClient.post().uri("/api/contas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
        // Then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(resposta -> {
                    try {
                        JsonNode json = objectMapper.readTree(resposta.getResponseBody());
                        assertEquals("Transferência realizada com sucesso!", json.path("mensagem").asText());
                        assertEquals(1, json.path("transacao").path("contaOrigemId").asLong());
                        assertEquals(LocalDate.now().toString(), json.path("data").asText());
                        assertEquals("100", json.path("transacao").path("valor").asText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .jsonPath("$.mensagem").isNotEmpty()
                .jsonPath("$.mensagem").value(is("Transferência realizada com sucesso!"))
                .jsonPath("$.mensagem").value( valor -> assertEquals("Transferência realizada com sucesso!", valor))
                .jsonPath("$.mensagem").isEqualTo("Transferência realizada com sucesso!")
                .jsonPath("$.transacao.contaOrigemId").isEqualTo(dto.getContaOrigemId())
                .jsonPath("$.data").isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(responseBody));
    }

    @Test
    @Order(3)
    void testDetalhe() throws JsonProcessingException{

        // Given
        Conta conta = new Conta(1L, "Carlos Silva", new BigDecimal("900"));

        // When
        webTestClient.get().uri("/api/contas/1").exchange()
        // Then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.cliente").isEqualTo("Carlos Silva")
                .jsonPath("$.saldo").isEqualTo(900)
                .json(objectMapper.writeValueAsString(conta));
    }

    @Test
    @Order(4)
    void testDetalhe2(){
        // When
        webTestClient.get().uri("/api/contas/2").exchange()
                // Then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Conta.class)
                .consumeWith(response -> {
                    Conta conta = response.getResponseBody();
                    assertEquals("Maria Rita", conta.getCliente());
                    assertEquals("2100.00", conta.getSaldo().toPlainString());
                });
    }

    @Test
    @Order(1)
    void testListarTodas() throws JsonProcessingException{
        // Given
        List<Conta> contas = Arrays.asList(conta001().orElseThrow(), conta002().orElseThrow());

        // When
        webTestClient.get().uri("/api/contas").exchange()
        // Then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].cliente").isEqualTo("Carlos Silva")
                .jsonPath("$[0].saldo").isEqualTo(1000)
                .jsonPath("$[1].cliente").isEqualTo("Maria Rita")
                .jsonPath("$[1].saldo").isEqualTo(2000)
                .json(objectMapper.writeValueAsString(contas))
                .jsonPath("$", hasSize(2));

    }


}