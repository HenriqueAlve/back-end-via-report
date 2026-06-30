package reporta.via.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reporta.via.api.exceptions.RecursoNaoEncontradoException;
import reporta.via.api.ocorrencia.dto.response.OcorrenciaResponseDTO;
import reporta.via.api.ocorrencia.mapper.OcorrenciaMapper;
import reporta.via.api.ocorrencia.model.Ocorrencia;
import reporta.via.api.ocorrencia.repository.OcorrenciaRepository;
import reporta.via.api.ocorrencia.service.OcorrenciaService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OcorrenciaServiceTest {

    @Mock
    private OcorrenciaRepository repository;

    @Mock
    private OcorrenciaMapper mapper;

    @InjectMocks
    private OcorrenciaService service;

    @Test
    void deveBuscarOcorrenciaPorId(){
        UUID id = UUID.randomUUID();
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setId(id);
        ocorrencia.setTitulo("Buraco na rua");
        when(repository.findById(id)).thenReturn(Optional.of(ocorrencia));
        OcorrenciaResponseDTO dtoFalso = new OcorrenciaResponseDTO(
                id, "Buraco na rua", null, null, null, null, null, null, null, null, null, null, null, null, null
        );
        when(mapper.toResponseDTO(ocorrencia)).thenReturn(dtoFalso); // mocka o mapper também

        // Act
        OcorrenciaResponseDTO resultado = service.buscarOcorrenciaPorId(id);

        // Assert
        assertNotNull(resultado);
        System.out.println("Resultado: " + resultado);
        assertEquals("Buraco na rua", resultado.titulo());
    }

    @Test
    void deveLancarExcecaoQuandoOcorrenciaNaoExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act + Assert (juntos aqui, porque queremos capturar a exceção)
        assertThrows(RecursoNaoEncontradoException.class, () -> {
            service.buscarOcorrenciaPorId(id);
        });
    }

    @Test
    void buscarResponsavelDaOcorrencia(){
        UUID id = UUID.randomUUID();
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setId(id);
        ocorrencia.setResponsavel("Henrique");

        when(repository.findById(id)).thenReturn(Optional.of(ocorrencia));

        String resultado = service.buscarResponsavelDaOcorrencia(id);

        System.out.println("Resultado: " + resultado);
        // Assert
        assertEquals("Henrique", resultado);
    }

    @Test
    void deveRetornarSemResponsavelQuandoNulo() {
        UUID id = UUID.randomUUID();
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setId(id);
        ocorrencia.setResponsavel(null); // ✅ null, não string vazia

        when(repository.findById(id)).thenReturn(Optional.of(ocorrencia));

        String resultado = service.buscarResponsavelDaOcorrencia(id);

        System.out.println("Resultado: " + resultado);

        assertEquals("Sem responsavel", resultado); // ✅ verifica o texto default
        //4985 8101 7445 5165      03/32 133
    }


}
