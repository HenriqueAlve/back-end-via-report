package reporta.via.api.usuario.dto.response;

import java.util.UUID;

public record UsuarioResumoDTO(
        UUID id,
        String nome
) {
}
