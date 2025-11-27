
package ar.utn.ba.ddsi.services;

import ar.utn.ba.ddsi.exceptions.NotFoundException;
import ar.utn.ba.ddsi.models.dtos.UserRolesPermissionsDTO;
import ar.utn.ba.ddsi.models.entities.Usuario;
import ar.utn.ba.ddsi.models.repositories.IUsuariosRepository;
import ar.utn.ba.ddsi.utils.JwtUtil;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final IUsuariosRepository usuariosRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginService(IUsuariosRepository usuariosRepository) {
        this.usuariosRepository = usuariosRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Usuario autenticarUsuario(String username, String password) {
        Optional<Usuario> usuarioOpt = usuariosRepository.findBymail(username);

        if (usuarioOpt.isEmpty()) {
            throw new NotFoundException("Usuario", username);
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar la contraseña usando BCrypt
        if (!passwordEncoder.matches(password, usuario.getContrasenia())) {
            throw new NotFoundException("Usuario", username);
        }

        return usuario;
    }

    public String generarAccessToken(String username) {
        return JwtUtil.generarAccessToken(username);
    }

    public String generarRefreshToken(String username) {
        return JwtUtil.generarRefreshToken(username);
    }

    public UserRolesPermissionsDTO obtenerRolesYPermisosUsuario(String username) {
        Optional<Usuario> usuarioOpt = usuariosRepository.findBymail(username);

        if (usuarioOpt.isEmpty()) {
            throw new NotFoundException("Usuario", username);
        }

        Usuario usuario = usuarioOpt.get();
//TODO
        return UserRolesPermissionsDTO.builder()
                .username(usuario.getNombreDeUsuario())
                .rol(usuario.getRol())
                .permisos(usuario.getPermisos())
                .build();
    }
}