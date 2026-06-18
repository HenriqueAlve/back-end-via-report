package reporta.via.api.ocorrencia.dto.response;

import reporta.via.api.ocorrencia.enums.Status;

public record DashboardResumoDTO(
        long ocorrenciasAtivas,
        long pendentes,
        long emAndamento,
        long resolvidas,
        int taxaResolucao
) {
}
