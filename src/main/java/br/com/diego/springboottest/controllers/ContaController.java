package br.com.diego.springboottest.controllers;

import br.com.diego.springboottest.models.Conta;
import br.com.diego.springboottest.models.TransacaoDto;
import br.com.diego.springboottest.services.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("/api/contas")
public class ContaController {

    @Autowired
    ContaService contaService;

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public Conta detalhe(@PathVariable Long id){
        return contaService.buscarPorId(id);
    }

    @PostMapping("/transferir")
    public ResponseEntity<?> transferir(@RequestBody TransacaoDto dto) {
        contaService.efetuarTransferencia(dto.getContaOrigemId(), dto.getContaDestinoId(), dto.getValor(), dto.getBancoId());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", LocalDate.now().toString());
        responseBody.put("status", "OK");
        responseBody.put("mensagem", "Transferência realizada com sucesso!");
        responseBody.put("transacao", dto);

        return ResponseEntity.ok(responseBody);
    }

}
