package reporta.via.api.ocorrencia.dto.response;

import reporta.via.api.foto.model.FotoOcorrencia;
import reporta.via.api.ocorrencia.enums.Status;

import java.util.List;

public record OcorrenciaAplicativoDTO(
        List<OcorrenciaItemDTO> ocorrencias
        ) {
}
