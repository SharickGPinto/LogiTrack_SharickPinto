package com.s1.LogiTrack.auth;

import com.s1.LogiTrack.config.JwtService;
import com.s1.LogiTrack.exception.BusinessRuleException;
import com.s1.LogiTrack.model.Usuario;
import com.s1.LogiTrack.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {

        Usuario usuario = usuarioRepository.findByUsername(request.username());

        if (usuario != null && usuario.getPassword().equals(request.password())) {
            String token = jwtService.generateToken(
                    usuario.getUsername(),
                    usuario.getRol().name()
            );

            return Map.of(
                    "token", token,
                    "rol", usuario.getRol().name()
            );
        }

        throw new BusinessRuleException("Usuario o contraseña incorrectos");
    }
}