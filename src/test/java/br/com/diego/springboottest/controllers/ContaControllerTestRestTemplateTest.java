package br.com.diego.springboottest.controllers;

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
import java.util.HashMap;
import java.util.Map;

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
    void listarTodas() throws JsonProcessingException{
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

    private String getUri(String uri) {
        return "http://localhost:" + porta + uri;
    }
}