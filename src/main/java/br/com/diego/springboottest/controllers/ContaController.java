package br.com.diego.springboottest.controllers;

import br.com.diego.springboottest.models.Conta;
import br.com.diego.springboottest.services.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.*;

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

}
