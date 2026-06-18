package reporta.via.api.usuario.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reporta.via.api.usuario.dto.request.RequestUsuarioDTO;
import reporta.via.api.usuario.service.UsuarioService;

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
}
