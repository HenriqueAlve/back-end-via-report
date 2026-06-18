package reporta.via.api.ocorrencia.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import reporta.via.api.foto.model.FotoOcorrencia;
import reporta.via.api.ocorrencia.dto.request.OcorrenciaRequestDTO;
import reporta.via.api.ocorrencia.dto.response.OcorrenciaMapaDTO;
import reporta.via.api.ocorrencia.dto.response.OcorrenciaResponseDTO;
import reporta.via.api.ocorrencia.model.Ocorrencia;
import reporta.via.api.usuario.mapper.UsuarioMapper;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = UsuarioMapper.class
)
public interface OcorrenciaMapper {

    Ocorrencia toEntity(OcorrenciaRequestDTO dto);


    OcorrenciaResponseDTO toResponseDTO(Ocorrencia entity);

    default List<String> mapFotos(List<FotoOcorrencia> fotos){
        if(fotos == null){
            return List.of();
        }

        return fotos.stream().map(FotoOcorrencia::getUrl).toList();
    }

    OcorrenciaMapaDTO toMapaDTO(Ocorrencia entity);


}
