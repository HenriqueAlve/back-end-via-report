package reporta.via.api.ocorrencia.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import reporta.via.api.ocorrencia.enums.Status;

public record OcorrenciaStatusUpdateDTO(

        @NotBlank(message = "Status inválido. Valores aceitos: ABERTO, EM_ANDAMENTO, RESOLVIDO, CANCELADO.")
        Status status

) {
}
