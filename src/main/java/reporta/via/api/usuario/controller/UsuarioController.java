package reporta.via.api.usuario.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reporta.via.api.usuario.dto.request.RequestUsuarioDTO;
import reporta.via.api.usuario.dto.response.UsuarioResumoDTO;
import reporta.via.api.usuario.model.Usuario;
import reporta.via.api.usuario.service.UsuarioService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

//    @PostMapping("/cadastrar")
//    public ResponseEntity<ResponseUsuarioDTO> salvarUsuario(@RequestBody RequestUsuarioDTO dto){
//        ResponseUsuarioDTO usuario = service.salvarUsuario(dto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
//    }

    @GetMapping("/por-perfil")
    public ResponseEntity<Map<String, List<Usuario>>> listarUsuariosPorPerfil() {
        Map<String, List<Usuario>> resultado = service.listarUsuariosPorPerfil();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResumoDTO> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.buscarUsuarioPorEmail(email));
    }


}
