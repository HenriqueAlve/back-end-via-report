package reporta.via.api.ocorrencia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reporta.via.api.exceptions.OcorrenciaNaoEncontradaException;
import reporta.via.api.exceptions.RecursoNaoEncontradoException;
import reporta.via.api.exceptions.RegraDeNegocioException;
import reporta.via.api.foto.model.FotoOcorrencia;
import reporta.via.api.foto.repository.FotoOcorrenciaRepository;
import reporta.via.api.historico_ocorrencia.repository.HistoricoOcorrenciaRepository;
import reporta.via.api.historico_ocorrencia.dto.response.HistoricoEventoDTO;
import reporta.via.api.ocorrencia.dto.request.OcorrenciaRequestDTO;
import reporta.via.api.ocorrencia.dto.request.OcorrenciaStatusUpdateDTO;
import reporta.via.api.ocorrencia.dto.response.*;
import reporta.via.api.ocorrencia.enums.Status;
import reporta.via.api.ocorrencia.event.StatusAlteradoEvent;
import reporta.via.api.ocorrencia.mapper.OcorrenciaMapper;
import reporta.via.api.ocorrencia.model.Ocorrencia;
import reporta.via.api.ocorrencia.repository.OcorrenciaRepository;
import reporta.via.api.ocorrencia.specification.OcorrenciaSpec;
import reporta.via.api.usuario.dto.response.UsuarioResumoDetalhesDTO;
import reporta.via.api.usuario.model.Usuario;
import reporta.via.api.usuario.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OcorrenciaService {

    @Autowired
    private OcorrenciaRepository repository;

    @Autowired
    private FotoOcorrenciaRepository fotoOcorrenciaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HistoricoOcorrenciaRepository historicoOcorrenciaRepository;

    @Autowired
    private OcorrenciaMapper mapper;


    private final ApplicationEventPublisher publisher;





    public OcorrenciaResponseDTO criar(OcorrenciaRequestDTO dto, MultipartFile foto) {
        Ocorrencia ocorrencia = mapper.toEntity(dto);

        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ocorrencia.setUsuario(usuario);

        boolean foraDoPerimetro =
                ocorrencia.getLatitude().doubleValue() < -22.693440 ||
                        ocorrencia.getLatitude().doubleValue() > -22.622423 ||
                        ocorrencia.getLongitude().doubleValue() < -50.455086 ||
                        ocorrencia.getLongitude().doubleValue() > -50.353974;

        if (foraDoPerimetro) {
            throw new RegraDeNegocioException("Localização inválida e fora do perímetro");
        }

        ocorrencia = repository.save(ocorrencia);

        if (foto != null && !foto.isEmpty()) {
            salvarFoto(foto, ocorrencia);
        }

        return mapper.toResponseDTO(ocorrencia);
    }

    private void salvarFoto(MultipartFile foto, Ocorrencia ocorrencia) {
        try {
            String pasta = "uploads/ocorrencias/";
            Files.createDirectories(Path.of(pasta));

            String nomeArquivo = UUID.randomUUID() + "_" + foto.getOriginalFilename();
            Path destino = Path.of(pasta + nomeArquivo);
            Files.copy(foto.getInputStream(), destino);

            FotoOcorrencia fotoOcorrencia = new FotoOcorrencia();
            fotoOcorrencia.setOcorrencia(ocorrencia);
            fotoOcorrencia.setNomeArquivo(nomeArquivo);
            fotoOcorrencia.setTipoArquivo(foto.getContentType());
            fotoOcorrencia.setUrl(pasta + nomeArquivo);
            fotoOcorrencia.setCriadoEm(LocalDateTime.now());

            fotoOcorrenciaRepository.save(fotoOcorrencia);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar foto", e);
        }
    }

    public OcorrenciaResponseDTO listarOcorrenciaPorId(UUID id) {
        var ocorrencia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ocorrencia não encontrada"));
        return mapper.toResponseDTO(ocorrencia);
    }

    public List<OcorrenciaMapaDTO> listarComFiltros(String status, String categoria) {

        Specification<Ocorrencia> spec = Specification
                .where(OcorrenciaSpec.comStatus(status))
                .and(OcorrenciaSpec.comCategoria(categoria));


        return repository.findAll(spec)
                .stream()
                .map(mapper::toMapaDTO)
                .toList();
    }

    public DashboardResumoDTO dashboardResumoCards() {
        long pendentes = repository.countByStatus(Status.ABERTO);
        long emAndamento = repository.countByStatus(Status.EM_ANDAMENTO);
        long resolvidas = repository.countByStatus(Status.RESOLVIDO);
        long total = repository.count();

        long ativas = pendentes + emAndamento;

        int taxa = total > 0 ? (int) (resolvidas * 100/ total) : 0;

        return new DashboardResumoDTO(ativas, pendentes, emAndamento, resolvidas, taxa);
    }

    public MapaResumoCardDTO mapaResumoCard() {
        long ocorrenciaTotal = repository.count();
        long pendentes = repository.countByStatus(Status.ABERTO);
        long emAndamento = repository.countByStatus(Status.EM_ANDAMENTO);
        long resolvidas = repository.countByStatus(Status.RESOLVIDO);

        return new MapaResumoCardDTO(ocorrenciaTotal, pendentes, emAndamento , resolvidas);
    }

    public OcorrenciaDetalhesDTO ocorrenciaDetalhes(UUID id) {
        var ocorrencia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ocorrencia não encontrada"));

        var reporter = new UsuarioResumoDetalhesDTO(
                ocorrencia.getUsuario().getNome(),
                ocorrencia.getUsuario().getTelefone(),
                ocorrencia.getUsuario().getEmail()
        );

        var historico = historicoOcorrenciaRepository.findByOcorrenciaId(id)
                .stream()
                .map(h -> new HistoricoEventoDTO(
                        h.getCriadoEm(),
                        h.getObservacao(),
                        h.getUsuario().getNome()
                ))
                .toList();

        return new OcorrenciaDetalhesDTO(
                ocorrencia.getId(),
                ocorrencia.getTitulo(),
                ocorrencia.getDescricao(),
                ocorrencia.getCategoria(),
                ocorrencia.getStatus(),
                ocorrencia.getCriadaEm(),
                ocorrencia.getFotos().get(0).getUrl(),
                ocorrencia.getLatitude(),
                ocorrencia.getLongitude(),
                ocorrencia.getEnderecoReferencia(),
                reporter,
                historico
        );
    }

    public MetricasUsuarioDTO metricasDeCadaUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Usuario não encontrado")
        );

        int enviadas = repository.countByUsuario(usuario);

        int resolvidas = repository.countByUsuarioAndStatus(usuario,Status.RESOLVIDO);

        int emAndamento = repository.countByUsuarioAndStatus(usuario,Status.EM_ANDAMENTO);

        return new MetricasUsuarioDTO(enviadas,resolvidas,emAndamento);

    }

    public OcorrenciaAplicativoDTO listaDeOcorrenciaPorUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Usuario não encontrado")
        );
       List<Ocorrencia> list = repository.findByUsuario(usuario);

        List<OcorrenciaItemDTO> dtos = list.stream()
                .map(o -> new OcorrenciaItemDTO(
                        o.getId(),
                        o.getTitulo(),
                        o.getStatus(),
                        o.getFotos().isEmpty() ? null : o.getFotos().get(0)
                )).toList();

        return new OcorrenciaAplicativoDTO(dtos);
    }

    public OcorrenciaResponseDTO atualizarStatusOcorrencia(OcorrenciaStatusUpdateDTO dto, UUID id) {
        Ocorrencia ocorrencia = repository.findById(id).orElseThrow(
                () -> new OcorrenciaNaoEncontradaException()
        );
        if (ocorrencia.getStatus() == Status.RESOLVIDO || ocorrencia.getStatus() == Status.CANCELADO){
            throw new RegraDeNegocioException("Não é possivel alterar o status dessa ocorrencia");
        }else{
            Status statusAnterior = ocorrencia.getStatus();
            Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ocorrencia.setStatus(dto.status());
            ocorrencia = repository.save(ocorrencia);
            publisher.publishEvent(new StatusAlteradoEvent(id, statusAnterior, dto.status(),usuarioLogado.getId()));
            return mapper.toResponseDTO(ocorrencia);
        }
    }

    public Map<String, List<OcorrenciaResponseDTO>> listarOcorrenciasAgrupadasPorStatus() {

        List<Ocorrencia> lista = repository.findAll();

        return lista.stream().map(
                mapper::toResponseDTO
        ).collect(
                Collectors.groupingBy(
                        o -> o.status().name()
                )
        );


    }

    public Map<String,Long> listarOcorrenciasPorCategoria() {
        List<Ocorrencia> lista = repository.findAll();

        return lista.stream().collect(
                Collectors.groupingBy(
                        u -> u.getCategoria().name() , Collectors.counting()
                )
        );
    }

    public Map<String, List<OcorrenciaResponseDTO>> listarOcorrenciasPorPrioridade() {

        List<Ocorrencia> lista = repository.findAll();

        return lista.stream().filter(
                f -> f.getResponsavel() != null
        ).map(
                mapper::toResponseDTO
                ).
                collect(
                Collectors.groupingBy(
                        o -> o.prioridade().name()
                )
        );
    }

    public Map<String, List<OcorrenciaResponseDTO>> listarOcorrenciasPorBairro() {
        List<Ocorrencia> lista = repository.findAll();

        return lista.stream().map(
                    mapper::toResponseDTO
                ).
                collect(
                Collectors.groupingBy(
                        u -> u.bairro()
                )
        );
    }

    public Map<String, Long> listarOcorrenciasExistemPorStatus() {
        List<Ocorrencia> lista = repository.findAll();

        return lista.stream().collect(
                Collectors.groupingBy(
                        o -> o.getStatus().name(), Collectors.counting()
                )
        );
    }

    public Map<String, List<OcorrenciaResponseDTO>> listarOcorrenciasPorPrioridadeComStatusAberto() {

        List<Ocorrencia> lista = repository.findAll();

        return lista.stream().filter(
                s -> s.getStatus().name().equals("ABERTO")
        ).map(
                mapper::toResponseDTO
        ).collect(
                Collectors.groupingBy(
                        p -> p.prioridade().name()
                )
        );
    }

    public Map<String, Long> listarOcorrenciasPorUsuarioAberto() {
        List<Ocorrencia> lista = repository.findAll();

        return lista.stream().collect(
                Collectors.groupingBy(
                        o -> o.getUsuario().getNome(), Collectors.counting()
                )
        );
    }

    public Map<String, List<String>> listarOcorrenciasPorCategoriaETitulo() {
        List<Ocorrencia> lista = repository.findAll();

        return lista.stream().collect(
                Collectors.groupingBy(
                        o -> o.getCategoria().name(),Collectors.mapping(
                                o -> o.getTitulo(), Collectors.toList()
                        )
                )
        );
    }

    public Map<String, Long> listarOcorrenciasPorMes() {

        List<Ocorrencia> lista = repository.findAll();

        return lista.stream().collect(
                Collectors.groupingBy(
                        o -> o.getCriadaEm().getMonth().name(), Collectors.counting()
                )
        );
    }

    public List<OcorrenciaResponseDTO> ordenarOcorrenciaDoRecenteProMaisAntigo() {
        List<Ocorrencia> lista = repository.findAll();

        return lista.stream().map(
                mapper::toResponseDTO
        ).sorted(Comparator.comparing(
                (OcorrenciaResponseDTO o) -> o.criadaEm()
        ).reversed()).collect(
                Collectors.toList()
        );
    }

    public Map<String,Long> contagemPorMesComPrioridadeAlta() {
        List<Ocorrencia> lista = repository.findAll();

        return lista.stream().filter(
                ocorrencia -> ocorrencia.getPrioridade().name().equals("ALTA")
        ).collect(
                Collectors.groupingBy(
                        o -> o.getCriadaEm().getMonth().name(), Collectors.counting()
                )
        );
    }


    public Map<String, Long> contagemDeOcorrenciasPorBairroQueNaoEstaoVazios() {
        List<Ocorrencia> lista = repository.findAll();

        return lista.stream().filter(ocorrencia -> ocorrencia.getBairro() != null && !ocorrencia.getBairro().isEmpty()).collect(
                Collectors.groupingBy(
                        o -> o.getBairro(), Collectors.counting()
                )
        );
    }

    public List<String> titulosDeTodasOcorrencias() {
        List<Ocorrencia> lista = repository.findAll();

        return lista.stream().filter(
                ocorrencia -> ocorrencia.getResponsavel() == null || ocorrencia.getResponsavel().isEmpty()
        ).map(
                ocorrencia -> ocorrencia.getTitulo()
        ).sorted(Comparator.naturalOrder()).toList();

    }

    public List<String> listaDeTodasAsFotos() {
        List<Ocorrencia> lista = repository.findAll();

        return lista.stream().flatMap(
                ocorrencia -> ocorrencia.getFotos().stream()
        ).map(
                fotoOcorrencia -> fotoOcorrencia.getUrl()
        ).collect(
                Collectors.toList()
        );
    }

    public List<String> listaDeTodasObservacoes() {
        List<Ocorrencia> lista = repository.findAll();

        List<String> resultado = lista.stream().flatMap(
                ocorrencia -> ocorrencia.getHistoricos().stream()
        ).filter(
                o -> o.getObservacao() != null || !o.getObservacao().isEmpty()
        ).map(
                o -> o.getObservacao().toString()
        ).distinct().toList();

        if(resultado.isEmpty()){
            throw new RegraDeNegocioException("Lista vazia");
        }
        return resultado;
    }

    public OcorrenciaResponseDTO buscarOcorrenciaPorId(UUID id) {
        Ocorrencia resultado = repository.findById(id).orElseThrow(
                () -> new RecursoNaoEncontradoException("Ocorrencia com o " + id + "não encontrado!")
        );

        return mapper.toResponseDTO(resultado);

    }

    public String buscarResponsavelDaOcorrencia(UUID id) {
        Ocorrencia resultado = repository.findById(id).orElseThrow(
                () -> new RecursoNaoEncontradoException("Ocorrencia com o " + id + " não encontrado!")
        );
        return Optional.ofNullable(resultado.getResponsavel()).orElse(
                "Sem responsavel"
        );
    }

    public String buscarNomeDoUsuarioDaOcorrencia(UUID id) {
        Ocorrencia resultado = repository.findById(id).orElseThrow(
                () -> new RecursoNaoEncontradoException("Ocorrencia com o " + id + " não encontrado!")
        );
        return Optional.ofNullable(resultado.getUsuario().getNome()).orElseThrow(
                () -> new RecursoNaoEncontradoException("Não foi possivel encontrar")
        );
    }

    public OcorrenciaResponseDTO validarSeOcorrenciaEstaAberta(UUID id) {
        Ocorrencia resultado = repository.findById(id).orElseThrow(
                () -> new RecursoNaoEncontradoException("Ocorrencia com o " + id + " não encontrado!")
        );
        if(resultado.getStatus() != Status.ABERTO){
            throw new RegraDeNegocioException("Ocorrência não está aberta");
        }
        return mapper.toResponseDTO(resultado);
    }

    public Page listarPaginado(Pageable pageable) {
        Page<Ocorrencia> pagina = repository.findAll(pageable);
        return pagina.map(mapper::toResponseDTO);
    }


    public Page<OcorrenciaResponseDTO> filtrar(
            String status, String categoria, String bairro, Pageable pageable
    ) {
        Specification<Ocorrencia> spec = Specification
                .where(OcorrenciaSpec.comStatus(status))
                .and(OcorrenciaSpec.comCategoria(categoria))
                .and(OcorrenciaSpec.comBairro(bairro));

        return repository.findAll(spec, pageable).map(mapper::toResponseDTO);
    }
}
