package com.s1.LogiTrack.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration// Le digo a Spring que esta clase es de configuración
@RequiredArgsConstructor
public class SecurityConfig {

    // Inyecto mi filtro JWT personalizado
    // Este filtro es el que va a leer el token en cada petición
    private final JwtFilter jwtFilter;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*
         * Aquí estoy configurando toda la seguridad de la aplicación.
         * En Spring Security moderno ya no se usa WebSecurityConfigurerAdapter,
         * sino que se define un SecurityFilterChain como Bean.
         */
        return http
                // ESTA LÍNEA ES NUEVA PARA EL CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Desactivo CSRF porque mi API es REST y trabaja con JWT (stateless).
                // CSRF se usa más cuando hay sesiones y formularios.
                .csrf(csrf -> csrf.disable())

                // Le digo a Spring que no quiero sesiones.
                // Cada request debe venir autenticado con su propio token.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Aquí defino qué endpoints son públicos y cuáles no.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuario").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/usuario/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/bodega/public/**",
                                "/api/producto/public/**",
                                "/api/usuario/public/**",
                                "/api/movimiento/public/**",
                                "/api/auditoria/public/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                /*
                 * Aquí agrego mi filtro JWT antes del filtro
                 * UsernamePasswordAuthenticationFilter.
                 *
                 * Porque quiero que primero se valide el token
                 * antes de que Spring intente autenticar usuario y contraseña.
                 *
                 * Este filtro será el encargado de:
                 * - Leer el header Authorization
                 * - Extraer el token
                 * - Validarlo
                 * - Cargar el usuario en el contexto de seguridad
                 */
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                // Finalmente construyo la configuración
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of("http://127.0.0.1:*", "http://localhost:*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}