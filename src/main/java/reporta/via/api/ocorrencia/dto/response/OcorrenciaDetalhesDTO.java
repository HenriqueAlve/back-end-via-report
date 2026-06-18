package reporta.via.api.ocorrencia.dto.response;

import reporta.via.api.historico_ocorrencia.dto.response.HistoricoEventoDTO;
import reporta.via.api.ocorrencia.enums.Categoria;
import reporta.via.api.ocorrencia.enums.Status;
import reporta.via.api.usuario.dto.response.UsuarioResumoDTO;
import reporta.via.api.usuario.dto.response.UsuarioResumoDetalhesDTO;
import reporta.via.api.usuario.model.Usuario;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OcorrenciaDetalhesDTO(
        UUID id,
        String titulo,
        String descricao,
        Categoria categoria,
        Status status,
        LocalDateTime criadoEm,
        String fotoUrl,
        BigDecimal latitude,
        BigDecimal longitude,
        String endereco,
        UsuarioResumoDetalhesDTO reporter,
        List<HistoricoEventoDTO> historico
) {
}
