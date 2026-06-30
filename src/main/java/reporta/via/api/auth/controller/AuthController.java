package reporta.via.api.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reporta.via.api.auth.dto.request.LoginRequestDTO;
import reporta.via.api.auth.dto.request.RegistroRequestDTO;
import reporta.via.api.auth.dto.response.TokenResponseDTO;
import reporta.via.api.infra.security.TokenService;
import reporta.via.api.usuario.model.Usuario;
import reporta.via.api.usuario.repository.UsuarioRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        var authToken = new UsernamePasswordAuthenticationToken(dto.email(), dto.senha());
        var auth = authenticationManager.authenticate(authToken);

        var usuario = (Usuario) auth.getPrincipal();

        var token = tokenService.gerarToken(usuario);

        return ResponseEntity.ok(new TokenResponseDTO(
                token,
                "Bearer",
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil().name(),
                usuario.getId()
        ));
    }

    @PostMapping("/registrar")
    public ResponseEntity<Void> registrar(@RequestBody @Valid RegistroRequestDTO dto) {
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        var usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha())); // SEMPRE encodar
        usuario.setPerfil(dto.perfil());

        usuarioRepository.save(usuario);

        return ResponseEntity.status(201).build();
    }


}
