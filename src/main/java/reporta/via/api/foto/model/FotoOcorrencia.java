package reporta.via.api.foto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import reporta.via.api.ocorrencia.model.Ocorrencia;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class FotoOcorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "ocorrencia_id")
    @JsonIgnore
    private Ocorrencia ocorrencia;

    private String url;

    private String nomeArquivo;

    private String tipoArquivo;

    private LocalDateTime criadoEm;
}
