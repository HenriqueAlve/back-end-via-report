package reporta.via.api.ocorrencia.dto.response;

import reporta.via.api.ocorrencia.enums.Categoria;
import reporta.via.api.ocorrencia.enums.Status;

import java.math.BigDecimal;
import java.util.UUID;

public record OcorrenciaMapaDTO(
        UUID id,
        String titulo,
        Categoria categoria,
        Status status,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
