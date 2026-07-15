package reporta.via.api.ocorrencia.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reporta.via.api.ocorrencia.dto.request.OcorrenciaRequestDTO;
import reporta.via.api.ocorrencia.dto.request.OcorrenciaStatusUpdateDTO;
import reporta.via.api.ocorrencia.dto.response.*;
import reporta.via.api.ocorrencia.model.Ocorrencia;
import reporta.via.api.ocorrencia.service.OcorrenciaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/agrupadas/status")
    public ResponseEntity<Map<String, List<OcorrenciaResponseDTO>>> listarOcorrenciasAgrupadasPorStatus(){
        Map<String, List<OcorrenciaResponseDTO>> resultado = service.listarOcorrenciasAgrupadasPorStatus();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/contagem/categoria")
    public ResponseEntity<Map<String,Long>> listarOcorrenciasPorCategoria(){
        Map<String, Long> resultado = service.listarOcorrenciasPorCategoria();
        return ResponseEntity.ok(resultado);
    }
    @GetMapping("/agrupadas/prioridade")
    public ResponseEntity<Map<String,List<OcorrenciaResponseDTO>>> listarOcorrenciasPorPrioridade(){
        Map<String, List<OcorrenciaResponseDTO>> resultado = service.listarOcorrenciasPorPrioridade();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/agrupadas/bairro")
    public ResponseEntity<Map<String,List<OcorrenciaResponseDTO>>> listarOcorrenciasPorBairro(){
        Map<String, List<OcorrenciaResponseDTO>> resultado = service.listarOcorrenciasPorBairro();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/contagem/status")
    public ResponseEntity<Map<String, Long> > listarOcorrenciasExistemPorStatus(){
        Map<String, Long>  resultado = service.listarOcorrenciasExistemPorStatus();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/abertas/agrupadas/prioridade")
    public ResponseEntity<Map<String,List<OcorrenciaResponseDTO>>> listarOcorrenciasPorPrioridadeComStatusAberto(){
        Map<String, List<OcorrenciaResponseDTO>> resultado = service.listarOcorrenciasPorPrioridadeComStatusAberto();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/abertas/contagem/usuario")
    public ResponseEntity<Map<String, Long>> listarOcorrenciasPorUsuarioAberto(){
        Map<String, Long>  resultado = service.listarOcorrenciasPorUsuarioAberto();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/agrupadas/categoria/titulos")
    public ResponseEntity<Map<String, List<String>>> listarOcorrenciasPorCategoriaETitulo(){
        Map<String, List<String>>  resultado = service.listarOcorrenciasPorCategoriaETitulo();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/contagem/mes")
    public ResponseEntity<Map<String, Long>> listarOcorrenciasPorMes(){
        Map<String, Long>  resultado = service.listarOcorrenciasPorMes();
        return ResponseEntity.ok(resultado);
    }


    @GetMapping("/recentes")
    public ResponseEntity<List<OcorrenciaResponseDTO>> ordenarOcorrenciaDoRecenteProMaisAntigo(){
        List<OcorrenciaResponseDTO> resultado = service.ordenarOcorrenciaDoRecenteProMaisAntigo();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/prioridade-alta/contagem/mes")
    public ResponseEntity<Map<String,Long>> contagemPorMesComPrioridadeAlta(){
        Map<String,Long> resultado = service.contagemPorMesComPrioridadeAlta();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/contagem/bairro")
    public ResponseEntity<Map<String,Long>> contagemDeOcorrenciasPorBairroQueNaoEstaoVazios(){
        Map<String,Long> resultado = service.contagemDeOcorrenciasPorBairroQueNaoEstaoVazios();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/titulos")
    public ResponseEntity<List<String>> titulosDeTodasOcorrencias(){
        List<String> resultado = service.titulosDeTodasOcorrencias();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/fotos")
    public ResponseEntity<List<String>> listaDeTodasAsFotos(){
        List<String> resultado = service.listaDeTodasAsFotos();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/observacoes")
    public ResponseEntity<List<String>> listaDeTodasObservacoes(){
        List<String> resultado = service.listaDeTodasObservacoes();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}/resumo")
    public ResponseEntity<OcorrenciaResponseDTO> buscarOcorrenciaPorId(@PathVariable UUID id){
        OcorrenciaResponseDTO resultado = service.buscarOcorrenciaPorId(id);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}/responsavel")
    public ResponseEntity<String> buscarResponsavelDaOcorrencia(@PathVariable UUID id){
        String resultado = service.buscarResponsavelDaOcorrencia(id);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}/usuario/nome")
    public ResponseEntity<String> buscarNomeDoUsuarioDaOcorrencia(@PathVariable UUID id){
        String resultado = service.buscarNomeDoUsuarioDaOcorrencia(id);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}/validar-aberta")
    public ResponseEntity<OcorrenciaResponseDTO> validarSeOcorrenciaEstaAberta(@PathVariable UUID id){
        OcorrenciaResponseDTO resultado = service.validarSeOcorrenciaEstaAberta(id);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<OcorrenciaResponseDTO>> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<OcorrenciaResponseDTO> resultado = service.listarPaginado(pageable);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<Page<OcorrenciaResponseDTO>> filtrar(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String bairro,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.filtrar(status, categoria, bairro, pageable));
    }




}
