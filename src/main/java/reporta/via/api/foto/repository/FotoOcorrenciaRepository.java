package reporta.via.api.foto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reporta.via.api.foto.model.FotoOcorrencia;

import java.util.UUID;

@Repository
public interface FotoOcorrenciaRepository extends JpaRepository<FotoOcorrencia, UUID> {
}
