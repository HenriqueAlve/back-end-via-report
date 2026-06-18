package reporta.via.api.usuario.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reporta.via.api.usuario.dto.request.RequestUsuarioDTO;
import reporta.via.api.usuario.model.Usuario;
import reporta.via.api.usuario.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;


}
