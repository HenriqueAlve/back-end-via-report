package reporta.via.api.ocorrencia.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reporta.via.api.historico_ocorrencia.model.HistoricoOcorrencia;
import reporta.via.api.historico_ocorrencia.repository.HistoricoOcorrenciaRepository;
import reporta.via.api.ocorrencia.event.StatusAlteradoEvent;
import reporta.via.api.ocorrencia.model.Ocorrencia;
import reporta.via.api.ocorrencia.repository.OcorrenciaRepository;
import reporta.via.api.usuario.model.Usuario;
import reporta.via.api.usuario.repository.UsuarioRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class StatusAlteradoListener {

    private final HistoricoOcorrenciaRepository historicoRepository;
    private final OcorrenciaRepository ocorrenciaRepository;
    private final UsuarioRepository usuarioRepository;

    @Async
    @EventListener
    public void aoAlterarStatus(StatusAlteradoEvent event) {
        Ocorrencia ocorrencia = ocorrenciaRepository.findById(event.getOcorrenciaId())
                .orElseThrow();

        Usuario usuario = usuarioRepository.findById(event.getUsuarioId())
                .orElseThrow();

        HistoricoOcorrencia historico = new HistoricoOcorrencia();
        historico.setOcorrencia(ocorrencia);
        historico.setStatusAnterior(event.getStatusAnterior());
        historico.setStatusNovo(event.getStatusNovo());
        historico.setObservacao("Status alterado automaticamente");
        historico.setCriadoEm(LocalDateTime.now());
        historico.setUsuario(usuario);

        historicoRepository.save(historico);
    }
}

