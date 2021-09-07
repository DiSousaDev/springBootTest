package br.com.diego.springboottest.controllers;

import br.com.diego.springboottest.models.TransacaoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

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
}