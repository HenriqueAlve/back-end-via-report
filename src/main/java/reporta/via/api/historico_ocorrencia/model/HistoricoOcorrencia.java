package reporta.via.api.historico_ocorrencia.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import reporta.via.api.ocorrencia.enums.Status;
import reporta.via.api.ocorrencia.model.Ocorrencia;
import reporta.via.api.usuario.model.Usuario;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class HistoricoOcorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "ocorrencia_id")
    private Ocorrencia ocorrencia;

    @Enumerated(value = EnumType.STRING)
    private Status statusAnterior;

    @Enumerated(value = EnumType.STRING)
    private Status statusNovo;

    private String observacao;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private LocalDateTime criadoEm;
}
