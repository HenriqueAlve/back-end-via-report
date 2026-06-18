package reporta.via.api.ocorrencia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import reporta.via.api.ocorrencia.enums.Status;
import reporta.via.api.ocorrencia.model.Ocorrencia;
import reporta.via.api.usuario.model.Usuario;

import java.util.List;
import java.util.UUID;

@Repository
public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, UUID>,
        JpaSpecificationExecutor<Ocorrencia> {

    long countByStatus(Status status);

    int countByUsuario(Usuario usuario);

    int countByUsuarioAndStatus(Usuario usuario, Status status);

    List<Ocorrencia> findByUsuario(Usuario usuario);
}