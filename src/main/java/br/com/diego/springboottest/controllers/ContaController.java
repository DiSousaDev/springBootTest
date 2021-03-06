package br.com.diego.springboottest.controllers;

import br.com.diego.springboottest.models.Conta;
import br.com.diego.springboottest.models.TransacaoDto;
import br.com.diego.springboottest.services.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

    @Autowired
    ContaService contaService;

    @GetMapping
    @ResponseStatus(OK)
    public List<Conta> listarTodas() {
        return contaService.buscarTodas();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Conta salvar(@RequestBody Conta conta) {
        return contaService.salvar(conta);
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public ResponseEntity<?> detalhe(@PathVariable Long id){
        Conta conta;
        try {
            conta = contaService.buscarPorId(id);
        } catch (NoSuchElementException error) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(conta);
    }

    @PostMapping("/transferir")
    public ResponseEntity<?> transferir(@RequestBody TransacaoDto dto) {
        contaService.efetuarTransferencia(dto.getContaOrigemId(), dto.getContaDestinoId(), dto.getValor(), dto.getBancoId());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", LocalDate.now().toString());
        responseBody.put("status", "OK");
        responseBody.put("mensagem", "Transfer??ncia realizada com sucesso!");
        responseBody.put("transacao", dto);

        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void excluir (@PathVariable Long id) {
        contaService.excluir(id);
    }

}
