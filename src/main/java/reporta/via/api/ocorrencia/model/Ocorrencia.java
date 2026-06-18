package reporta.via.api.ocorrencia.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import reporta.via.api.foto.model.FotoOcorrencia;
import reporta.via.api.historico_ocorrencia.model.HistoricoOcorrencia;
import reporta.via.api.ocorrencia.enums.Categoria;
import reporta.via.api.ocorrencia.enums.Prioridade;
import reporta.via.api.ocorrencia.enums.Status;
import reporta.via.api.usuario.model.Usuario;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    private BigDecimal latitude;

    private BigDecimal  longitude;

    private String enderecoReferencia;

    private String bairro;

    private LocalDateTime criadaEm;

    private LocalDateTime atualizadaEm;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String responsavel;

    @OneToMany(mappedBy = "ocorrencia", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<FotoOcorrencia> fotos = new ArrayList<>();

    @OneToMany(mappedBy = "ocorrencia", cascade = CascadeType.ALL)
    private List<HistoricoOcorrencia> historicos = new ArrayList<>();


    @PrePersist
    public void prePersist() {
        criadaEm = LocalDateTime.now();
        atualizadaEm = LocalDateTime.now();
        if (status == null){
            status = Status.ABERTO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        atualizadaEm = LocalDateTime.now();
    }


}
