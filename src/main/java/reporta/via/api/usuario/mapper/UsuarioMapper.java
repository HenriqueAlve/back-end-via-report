package reporta.via.api.usuario.mapper;

import org.mapstruct.Mapper;
import reporta.via.api.usuario.dto.response.UsuarioResumoDTO;
import reporta.via.api.usuario.model.Usuario;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioResumoDTO toResumoDTO(Usuario usuario);
}
