package reporta.via.api.usuario.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestUsuarioDTO {
    private String nome;
    private String email;
    private String senha;
    private String perfil;
}
