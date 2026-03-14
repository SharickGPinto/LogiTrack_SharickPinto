package com.s1.LogiTrack.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service // Lo marco como servicio porque será usado para generar y validar tokens
public class JwtService {
    /*
     * Esta es mi clave secreta.
     * Con esta clave se firma el token.
     * IMPORTANTE: en producción esto NO va en el código.
     * Se guarda en variables de entorno.
     *
     * Pero para clase lo dejo así para que sea más didáctico.
     */
    private final String SECRET = "clave_super_secreta_para_clase_2026";
    /*
     * Tiempo de expiración del token.
     * Aquí lo estoy configurando a 30 minutos.
     * (1000 ms * 60 seg * 30 min)
     */
    private final long EXPIRATION = 1000 * 60 * 30; // 30 minutos

    /*
     * Este método convierte el String secreto en una Key válida
     * para poder firmar el token con algoritmo HS256.
     */
    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Generar token
    public String generateToken(String username) {
        /*
         * Aquí construyo el JWT.
         *
         * El token tendrá:
         * - Subject → el username
         * - Fecha de creación
         * - Fecha de expiración
         * - Firma digital
         */
        return Jwts.builder()
                // Información principal del token (quién es el usuario)
                .setSubject(username)

                // Fecha en que se crea el token
                .setIssuedAt(new Date())

                // Fecha en que expira
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))

                // Firma con algoritmo HS256 usando mi clave secreta
                .signWith(getKey(), SignatureAlgorithm.HS256)

                // Construye el token final en formato String
                .compact();
    }

    // Validar token
    public String validateToken(String token) {
        /*
         * Este método:
         * - Verifica que la firma sea correcta
         * - Verifica que no esté expirado
         * - Si todo está bien, devuelve el username
         *
         * Si algo falla, devuelve null.
         */
        try {
            return Jwts.parserBuilder()
                    // Le paso la misma clave con la que firmé
                    .setSigningKey(getKey())
                    .build()

                    // Intenta parsear el token
                    .parseClaimsJws(token)

                    // Obtengo el body (claims)
                    .getBody()

                    // Devuelvo el subject (username)
                    .getSubject();
        } catch (Exception e) {

            /*
             * Si el token:
             * - Está vencido
             * - Fue modificado
             * - La firma no coincide
             *
             * Lanza excepción y retorna en null.
             */

            return null;
        }
    }
}