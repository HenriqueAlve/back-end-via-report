package reporta.via.api.ocorrencia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reporta.via.api.exceptions.OcorrenciaNaoEncontradaException;
import reporta.via.api.exceptions.RegraDeNegocioException;
import reporta.via.api.foto.model.FotoOcorrencia;
import reporta.via.api.foto.repository.FotoOcorrenciaRepository;
import reporta.via.api.historico_ocorrencia.repository.HistoricoOcorrenciaRepository;
import reporta.via.api.historico_ocorrencia.dto.response.HistoricoEventoDTO;
import reporta.via.api.ocorrencia.dto.request.OcorrenciaRequestDTO;
import reporta.via.api.ocorrencia.dto.request.OcorrenciaStatusUpdateDTO;
import reporta.via.api.ocorrencia.dto.response.*;
import reporta.via.api.ocorrencia.enums.Status;
import reporta.via.api.ocorrencia.mapper.OcorrenciaMapper;
import reporta.via.api.ocorrencia.model.Ocorrencia;
import reporta.via.api.ocorrencia.repository.OcorrenciaRepository;
import reporta.via.api.ocorrencia.specification.OcorrenciaSpec;
import reporta.via.api.usuario.dto.response.UsuarioResumoDetalhesDTO;
import reporta.via.api.usuario.model.Usuario;
import reporta.via.api.usuario.repository.UsuarioRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
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

            ocorrencia.setStatus(dto.status());
            ocorrencia = repository.save(ocorrencia);
            return mapper.toResponseDTO(ocorrencia);
        }
 }

}
