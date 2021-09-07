package br.com.diego.springboottest.controllers;

import br.com.diego.springboottest.models.TransacaoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

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
        webTestClient.post().uri("http://localhost:8080/api/contas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.mensagem").isNotEmpty()
                .jsonPath("$.mensagem").value(is("Transferência realizada com sucesso!"))
                .jsonPath("$.mensagem").value( valor -> assertEquals("Transferência realizada com sucesso!", valor))
                .jsonPath("$.mensagem").isEqualTo("Transferência realizada com sucesso!")
                .jsonPath("$.transacao.contaOrigemId").isEqualTo(dto.getContaOrigemId())
                .jsonPath("$.data").isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(responseBody));
    }
}