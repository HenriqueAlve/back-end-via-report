package reporta.via.api.ocorrencia.specification;

import org.springframework.data.jpa.domain.Specification;
import reporta.via.api.ocorrencia.enums.Categoria;
import reporta.via.api.ocorrencia.enums.Status;
import reporta.via.api.ocorrencia.model.Ocorrencia;

public class OcorrenciaSpec {

    public static Specification<Ocorrencia> comStatus(String status) {
        return (root, query, builder) -> {
            if (status == null || status.isBlank()) {
                return builder.conjunction(); // equivale a "sem filtro"
            }
            return builder.equal(root.get("status"), Status.valueOf(status));
        };
    }

    // Filtro por categoria
    public static Specification<Ocorrencia> comCategoria(String categoria) {
        return (root, query, builder) -> {
            if (categoria == null || categoria.isBlank()) {
                return builder.conjunction();
            }
            return builder.equal(root.get("categoria"), Categoria.valueOf(categoria));
        };
    }
}
