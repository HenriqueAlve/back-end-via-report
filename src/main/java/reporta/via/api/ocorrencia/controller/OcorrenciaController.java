package reporta.via.api.ocorrencia.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reporta.via.api.ocorrencia.dto.request.OcorrenciaRequestDTO;
import reporta.via.api.ocorrencia.dto.request.OcorrenciaStatusUpdateDTO;
import reporta.via.api.ocorrencia.dto.response.*;
import reporta.via.api.ocorrencia.service.OcorrenciaService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaService service;

    public OcorrenciaController(OcorrenciaService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OcorrenciaResponseDTO> criar(
            @Valid @RequestPart("dados") OcorrenciaRequestDTO dto,
            @RequestPart(value = "foto", required = false) MultipartFile foto
    ) {
        OcorrenciaResponseDTO ocorrenciaCriada = service.criar(dto, foto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ocorrenciaCriada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OcorrenciaResponseDTO> buscarPorId(@PathVariable UUID id) {
        OcorrenciaResponseDTO ocorrencia = service.listarOcorrenciaPorId(id);
        return ResponseEntity.ok(ocorrencia);
    }

    @GetMapping
    public ResponseEntity<List<OcorrenciaMapaDTO>> listarParaMapa(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String categoria
    ) {
        List<OcorrenciaMapaDTO> ocorrencias = service.listarComFiltros(status, categoria);
        return ResponseEntity.ok(ocorrencias);
    }

    @GetMapping("/dashboard/resumo")
    public ResponseEntity<DashboardResumoDTO> buscarResumoDashboard() {
        DashboardResumoDTO resumo = service.dashboardResumoCards();
        return ResponseEntity.ok(resumo);
    }

    @GetMapping("/mapa/resumo")
    public ResponseEntity<MapaResumoCardDTO> buscarResumoMapa() {
        MapaResumoCardDTO resumo = service.mapaResumoCard();
        return ResponseEntity.ok(resumo);
    }

    @GetMapping("/{id}/detalhes")
    public ResponseEntity<OcorrenciaDetalhesDTO> buscarDetalhes(@PathVariable UUID id) {
        OcorrenciaDetalhesDTO detalhes = service.ocorrenciaDetalhes(id);
        return ResponseEntity.ok(detalhes);
    }

    @GetMapping("/minhas/metricas")
    public ResponseEntity<MetricasUsuarioDTO> buscarMinhasMetricas(Authentication authentication) {
        String email = authentication.getName();
        MetricasUsuarioDTO metricas = service.metricasDeCadaUsuario(email);
        return ResponseEntity.ok(metricas);
    }

    @GetMapping("/minhas")
    public ResponseEntity<OcorrenciaAplicativoDTO> listarMinhasOcorrencias(Authentication authentication) {
        String email = authentication.getName();
        OcorrenciaAplicativoDTO ocorrencias = service.listaDeOcorrenciaPorUsuario(email);
        return ResponseEntity.ok(ocorrencias);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OcorrenciaResponseDTO> atualizarStatus(
            @PathVariable UUID id,
            @Valid @RequestBody OcorrenciaStatusUpdateDTO dto
    ) {
        OcorrenciaResponseDTO ocorrenciaAtualizada = service.atualizarStatusOcorrencia(dto, id);
        return ResponseEntity.ok(ocorrenciaAtualizada);
    }
}
