package reporta.via.api.historico_ocorrencia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reporta.via.api.historico_ocorrencia.service.HistoricoService;

import java.util.List;

@RestController
@RequestMapping("/api/historico-ocorrencia")
public class HistoricoOcorrenciaController {

    @Autowired
    private HistoricoService service;


 }
