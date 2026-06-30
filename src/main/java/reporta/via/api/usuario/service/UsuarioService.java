package reporta.via.api.usuario.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reporta.via.api.exceptions.RecursoNaoEncontradoException;
import reporta.via.api.usuario.dto.request.RequestUsuarioDTO;
import reporta.via.api.usuario.dto.response.UsuarioResumoDTO;
import reporta.via.api.usuario.enums.Perfil;
import reporta.via.api.usuario.model.Usuario;
import reporta.via.api.usuario.repository.UsuarioRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;


    public Map<String,List<Usuario>> listarUsuariosPorPerfil() {
       List<Usuario> lista = usuarioRepository.findAll();

       return lista.stream().collect(
               Collectors.groupingBy(usuario -> usuario.getPerfil().name())
       );
    }

    public UsuarioResumoDTO buscarUsuarioPorEmail(String email) {
        Usuario resultado = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new RecursoNaoEncontradoException("Não foi possivel encontrar o usuario com esse email: " + email)
        );

        return new UsuarioResumoDTO(
                resultado.getId(),
                resultado.getNome()
        );
    }
}
