package reporta.via.api.historico_ocorrencia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reporta.via.api.historico_ocorrencia.repository.HistoricoOcorrenciaRepository;

import java.util.List;

@Service
public class HistoricoService {

    @Autowired
    private HistoricoOcorrenciaRepository repository;



}
