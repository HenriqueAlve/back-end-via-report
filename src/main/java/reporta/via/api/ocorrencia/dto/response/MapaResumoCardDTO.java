package reporta.via.api.ocorrencia.dto.response;

public record MapaResumoCardDTO(
        long ocorrenciaTotal,
        long pendentes,
        long em_andamento,
        long resolvidas
) {
}
