package reporta.via.api.historico_ocorrencia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reporta.via.api.historico_ocorrencia.model.HistoricoOcorrencia;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface HistoricoOcorrenciaRepository extends JpaRepository<HistoricoOcorrencia, UUID> {


    List<HistoricoOcorrencia> findByOcorrenciaId(UUID id);
}
