package reporta.via.api.exceptions;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse(
        int status,
        String mensagem,
        String path,
        LocalDateTime hora,
        List<FieldErrorResponse> erros
) {
}
