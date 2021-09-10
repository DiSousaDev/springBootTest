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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContaControllerTestRestTemplateTest {

    @Autowired
    private TestRestTemplate testRestTemplateclient;

    private ObjectMapper objectMapper;

    @LocalServerPort
    private Integer porta;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void transferir() throws JsonProcessingException{
        TransacaoDto dto = new TransacaoDto();
        dto.setValor(new BigDecimal("100"));
        dto.setContaOrigemId(1L);
        dto.setContaDestinoId(2L);
        dto.setBancoId(1L);

        ResponseEntity<String> response = testRestTemplateclient
                .postForEntity(getUri("/api/contas/transferir"), dto, String.class);
        String json = response.getBody();

        System.out.println(">>>Testando na porta: " + porta);
        System.out.println(">>>Exibindo Json\n" + response);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(json);
        assertTrue(json.contains("Transferência realizada com sucesso!"));
        // Testando o objeto completo
        assertTrue(json.contains("{\"contaOrigemId\":1,\"contaDestinoId\":2,\"valor\":100,\"bancoId\":1}"));

        // Testando partes do objeto Json
        JsonNode jsonNode = objectMapper.readTree(json);
        assertEquals("Transferência realizada com sucesso!", jsonNode.path("mensagem").asText());
        assertEquals(LocalDate.now().toString(), jsonNode.path("data").asText());
        assertEquals("100", jsonNode.path("transacao").path("valor").asText());
        assertEquals(1, jsonNode.path("transacao").path("contaOrigemId").asLong());
        assertEquals(2, jsonNode.path("transacao").path("contaDestinoId").asLong());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", LocalDate.now().toString());
        responseBody.put("status", "OK");
        responseBody.put("mensagem", "Transferência realizada com sucesso!");
        responseBody.put("transacao", dto);

        // Testando objeto retorno
        assertEquals(objectMapper.writeValueAsString(responseBody), json);

    }

    @Test
    @Order(2)
    void testDetalhe() throws JsonProcessingException{

        ResponseEntity<Conta> response = testRestTemplateclient.getForEntity(getUri("/api/contas/1"), Conta.class);
        Conta conta = response.getBody();

        // Given
        Conta contaTeste = new Conta(1L, "Carlos Silva", new BigDecimal("900.00"));

        // When
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(conta);
        assertEquals(1L, conta.getId());
        assertEquals("Carlos Silva", conta.getCliente());
        assertEquals("900.00", conta.getSaldo().toPlainString());

        assertEquals(conta, contaTeste);

    }


    @Test
    @Order(3)
    void testListarTodas() throws JsonProcessingException{

        ResponseEntity<Conta[]> response = testRestTemplateclient.getForEntity(getUri("/api/contas"), Conta[].class);
        List<Conta> contas = Arrays.asList(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(contas);
        assertEquals(2, contas.size());
        assertEquals(1L, contas.get(0).getId());
        assertEquals("Carlos Silva", contas.get(0).getCliente());
        assertEquals("900.00", contas.get(0).getSaldo().toPlainString());
        assertEquals(2L, contas.get(1).getId());
        assertEquals("Maria Rita", contas.get(1).getCliente());
        assertEquals("2100.00", contas.get(1).getSaldo().toPlainString());

        JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(contas));
        assertEquals(1, jsonNode.get(0).path("id").asLong());
        assertEquals("Carlos Silva", jsonNode.get(0).path("cliente").asText());
        assertEquals("900.0", jsonNode.get(0).path("saldo").asText());
        assertEquals(2, jsonNode.get(1).path("id").asLong());
        assertEquals("Maria Rita", jsonNode.get(1).path("cliente").asText());
        assertEquals("2100.0", jsonNode.get(1).path("saldo").asText());
    }

    private String getUri(String uri) {
        return "http://localhost:" + porta + uri;
    }
}