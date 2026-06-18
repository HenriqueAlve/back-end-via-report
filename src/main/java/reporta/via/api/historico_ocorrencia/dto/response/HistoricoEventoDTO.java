package reporta.via.api.historico_ocorrencia.dto.response;

import java.time.LocalDateTime;

public record HistoricoEventoDTO(
        LocalDateTime data,
        String texto,
        String usuario
) {
}
