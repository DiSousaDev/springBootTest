package br.com.diego.springboottest.controllers;

import br.com.diego.springboottest.models.TransacaoDto;
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

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContaControllerTestRestTemplateTest {

    @Autowired
    private TestRestTemplate testRestTemplateclient;

    @LocalServerPort
    private Integer porta;

    @BeforeEach
    void setUp(){
    }

    @Test
    @Order(1)
    void listarTodas(){
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
        assertTrue(json.contains("{\"contaOrigemId\":1,\"contaDestinoId\":2,\"valor\":100,\"bancoId\":1}"));

    }

    private String getUri(String uri) {
        return "http://localhost:" + porta + uri;
    }
}