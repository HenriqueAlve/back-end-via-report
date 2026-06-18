package reporta.via.api.auth.dto.response;

import java.util.UUID;

public record TokenResponseDTO(
        String token,
        String tipo,
        String nome,
        String email,
        String perfil,
        UUID id
) {
}
