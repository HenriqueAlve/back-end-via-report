package reporta.via.api.ocorrencia.dto.response;

import reporta.via.api.foto.model.FotoOcorrencia;
import reporta.via.api.ocorrencia.enums.Status;

import java.util.UUID;

public record OcorrenciaItemDTO(
        UUID id,
        String titulo,
        Status status,
        FotoOcorrencia fotoOcorrencia
) {
}
