package reporta.via.api.mapa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reporta.via.api.mapa.service.MapaService;
import reporta.via.api.ocorrencia.dto.response.OcorrenciaMapaDTO;

import java.util.List;

@RestController
@RequestMapping("/api/mapa")
public class MapaController {

    @Autowired
    private MapaService service;

    @GetMapping("/listar")
    public ResponseEntity<List<OcorrenciaMapaDTO>> listarOcorrenciaNoMapa(){
        List<OcorrenciaMapaDTO> ocorrenciaMapaDTOS = service.listarOcorrenciaNoMapa();
        return ResponseEntity.status(HttpStatus.OK).body(ocorrenciaMapaDTOS);
    }

}
