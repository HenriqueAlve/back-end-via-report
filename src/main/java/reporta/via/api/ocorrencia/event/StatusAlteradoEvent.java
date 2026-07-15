package reporta.via.api.ocorrencia.event;

import lombok.Getter;
import lombok.Setter;
import reporta.via.api.ocorrencia.enums.Status;

import java.util.UUID;

@Getter
@Setter
public class StatusAlteradoEvent {
    private final UUID ocorrenciaId;
    private final Status statusAnterior;
    private final Status statusNovo;
    private final UUID usuarioId;

    public StatusAlteradoEvent(UUID ocorrenciaId, Status statusAnterior, Status statusNovo, UUID usuarioId) {
        this.ocorrenciaId = ocorrenciaId;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.usuarioId = usuarioId;
    }
}
