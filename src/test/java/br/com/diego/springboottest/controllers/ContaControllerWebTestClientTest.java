package br.com.diego.springboottest.controllers;

import br.com.diego.springboottest.models.Conta;
import br.com.diego.springboottest.models.TransacaoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Tag("integracao_wc")
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
    @Order(1)
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
    @Order(2)
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
    @Order(3)
    void testDetalhe2(){
        // When
        webTestClient.get().uri("/api/contas/2").exchange()
                // Then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Conta.class)
                .consumeWith(response -> {
                    Conta conta = response.getResponseBody();
                    assertNotNull(conta);
                    assertEquals("Maria Rita", conta.getCliente());
                    assertEquals("2100.00", conta.getSaldo().toPlainString());
                });
    }

    @Test
    @Order(4)
    void testListarTodas() {

        // When
        webTestClient.get().uri("/api/contas").exchange()
        // Then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].cliente").isEqualTo("Carlos Silva")
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].saldo").isEqualTo(900)
                .jsonPath("$[1].cliente").isEqualTo("Maria Rita")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].saldo").isEqualTo(2100)
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(2));

    }

    @Test
    @Order(5)
    void testListarTodas2() {
        // Given
        List<Conta> contasMock = Arrays.asList(conta001().orElseThrow(), conta002().orElseThrow());

        // When
        webTestClient.get().uri("/api/contas").exchange()
                // Then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Conta.class)
                .consumeWith(response -> {
                    List<Conta> contas = response.getResponseBody();
                    assertNotNull(contas);
                    assertEquals(2, contas.size());
                    assertEquals(1L, contas.get(0).getId());
                    assertEquals("Carlos Silva", contas.get(0).getCliente());
                    assertEquals("900.0", contas.get(0).getSaldo().toPlainString());
                    assertEquals(2L, contas.get(1).getId());
                    assertEquals("Maria Rita", contas.get(1).getCliente());
                    assertEquals("2100.0", contas.get(1).getSaldo().toPlainString());
                })
                .hasSize(2)// BodyList
                .value(hasSize(2)); // hamcrest

    }

    @Test
    @Order(6)
    void testSalvar() {
        // Given
        Conta conta = new Conta(null, "João", new BigDecimal("3000"));

        // When
        webTestClient.post().uri("/api/contas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(conta)
                .exchange()
        // Then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.cliente").isEqualTo("João")
                .jsonPath("$.cliente").value(is("João"))
                .jsonPath("$.saldo").isEqualTo(3000);

    }

    @Test
    @Order(7)
    void testSalvar2() {
        // Given
        Conta conta = new Conta(null, "Maria", new BigDecimal("3123"));

        // When
        webTestClient.post().uri("/api/contas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(conta)
                .exchange()
                // Then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Conta.class)
                .consumeWith(response -> {
                    Conta c = response.getResponseBody();
                    assertNotNull(c);
                    assertEquals(4L, c.getId());
                    assertEquals("Maria", c.getCliente());
                    assertEquals("3123", c.getSaldo().toPlainString());
                });

    }

    @Test
    @Order(8)
    void testExcluir(){

        webTestClient.get().uri("/api/contas").exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Conta.class)
                .hasSize(4);

        webTestClient.delete().uri("/api/contas/3")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        webTestClient.get().uri("/api/contas").exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Conta.class)
                .hasSize(3);

        webTestClient.get().uri("/api/contas/3").exchange()
//                .expectStatus().is5xxServerError();
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

}