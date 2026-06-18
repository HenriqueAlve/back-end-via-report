package reporta.via.api.ocorrencia.dto.response;

import reporta.via.api.ocorrencia.enums.Categoria;
import reporta.via.api.ocorrencia.enums.Prioridade;
import reporta.via.api.ocorrencia.enums.Status;
import reporta.via.api.usuario.dto.response.UsuarioResumoDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OcorrenciaResponseDTO(
        UUID id,

        String titulo,

        String descricao,

        Categoria categoria,

        Status status,

        Prioridade prioridade,

        String latitude,

        String longitude,

        String enderecoReferencia,

        String bairro,

        String responsavel,

        LocalDateTime criadaEm,

        LocalDateTime atualizadaEm,

        UsuarioResumoDTO usuario,

        List<String> fotos
) {
}
