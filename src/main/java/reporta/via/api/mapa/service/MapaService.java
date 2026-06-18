package reporta.via.api.mapa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reporta.via.api.ocorrencia.dto.response.OcorrenciaMapaDTO;
import reporta.via.api.ocorrencia.mapper.OcorrenciaMapper;
import reporta.via.api.ocorrencia.repository.OcorrenciaRepository;

import java.util.List;

@Service
public class MapaService {

    @Autowired
    private OcorrenciaRepository repository;

    @Autowired
    private OcorrenciaMapper mapper;

    public List<OcorrenciaMapaDTO> listarOcorrenciaNoMapa() {
        return repository.findAll().stream().map(mapper::toMapaDTO).toList();
    }
}
