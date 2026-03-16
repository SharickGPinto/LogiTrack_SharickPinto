package com.s1.LogiTrack.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component // Le digo a Spring que este filtro es un componente manejado por el contenedor
@RequiredArgsConstructor
public class JwtFilter extends GenericFilter {
    /*
     * Inyecto mi JwtService.
     * Este servicio es el que sabe validar el token.
     */
    private final JwtService jwtService;

    /*
     * Este método se ejecuta en CADA petición que llega al servidor.
     * Aquí es donde revisamos si el request trae un token válido.
     */
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        // Convierto el request genérico en HttpServletRequest
        HttpServletRequest http = (HttpServletRequest) request;

        // Obtengo el header Authorization
        String header = http.getHeader("Authorization");

        /*
         * Verifico:
         * 1) Que el header exista
         * 2) Que empiece con "Bearer "
         */
        //Bearer Token
        if (header != null && header.startsWith("Bearer ")) {

            // Quito la palabra "Bearer " y me quedo solo con el token
            String token = header.substring(7);

            // Valido el token usando mi servicio
            String username = jwtService.validateToken(token);
            String rol = jwtService.extractRol(token);

            /*
             * Si el username no es null significa que:
             * - La firma es válida
             * - El token no está vencido
             */
            if (username != null) {
                /*
                 * Creo un objeto de autenticación.
                 * Aquí estoy diciendo:
                 * "Este usuario ya está autenticado"
                 *
                 * No paso contraseña porque en JWT ya no la necesito.
                 * Aquí sí paso los roles para que Spring Security
                 * pueda aplicar hasRole("ADMIN").
                 */
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                rol != null
                                        ? List.of(new SimpleGrantedAuthority("ROLE_" + rol))
                                        : List.of()
                        );
                /*
                 * Aquí es donde realmente le digo a Spring Security:
                 * "Este usuario está autenticado en esta petición"
                 */
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        /*
         * Muy importante:
         * Siempre debo continuar la cadena de filtros.
         * Si no llamo a chain.doFilter, la petición se queda bloqueada.
         */
        chain.doFilter(request, response);
    }
}