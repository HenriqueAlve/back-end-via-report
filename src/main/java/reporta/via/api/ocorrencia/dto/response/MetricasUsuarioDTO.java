package reporta.via.api.ocorrencia.dto.response;

public record MetricasUsuarioDTO(
        int enviadas,
        int resolvidas,
        int emAndamento
) {
}
