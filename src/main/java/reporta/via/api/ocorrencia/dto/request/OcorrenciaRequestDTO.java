package reporta.via.api.ocorrencia.dto.request;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;
import reporta.via.api.ocorrencia.enums.Categoria;
import reporta.via.api.ocorrencia.enums.Prioridade;

import java.math.BigDecimal;
import java.util.List;

public record OcorrenciaRequestDTO(

        @NotBlank(message = "Título é obrigatório")
        @Size(min = 5, max = 120, message = "Título deve ter entre 5 e 120 caracteres")
        String titulo,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(min = 10, max = 1000, message = "Descrição deve ter entre 10 e 1000 caracteres")
        String descricao,

        @NotNull(message = "Categoria é obrigatória")
        Categoria categoria,

        @NotNull(message = "Prioridade é obrigatória")
        Prioridade prioridade,

        @NotNull(message = "Latitude é obrigatória")
        @DecimalMin(value = "-90.0", message = "Latitude deve estar entre -90 e 90")
        @DecimalMax(value = "90.0", message = "Latitude deve estar entre -90 e 90")
        BigDecimal latitude,

        @NotNull(message = "Longitude é obrigatória")
        @DecimalMin(value = "-180.0", message = "Longitude deve estar entre -180 e 180")
        @DecimalMax(value = "180.0", message = "Longitude deve estar entre -180 e 180")
        BigDecimal longitude,

        @Size(max = 150, message = "Endereço de referência deve ter no máximo 150 caracteres")
        String enderecoReferencia,

        @Size(min = 3, max = 100, message = "Bairro deve ter entre 3 e 100 caracteres")
        String bairro,

        List<MultipartFile> fotos
) {
}
