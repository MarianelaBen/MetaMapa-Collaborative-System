package ar.utn.ba.ddsi.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    @Getter
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long ACCESS_TOKEN_VALIDITY = 15 * 60 * 1000; // 15 min
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7 días


    //TODO el profe solo setea el user en el token, se puede meter el id o otras cosas
    public static String generarAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username) //para que lo podeamos recuperar despues
                .setIssuer("servicioUsuario")
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY)) //cuando expira
                .signWith(key) //le ponemos algoritmo de cifrado
                .compact();
    }


    public static String generarRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuer("servicioUsuario")
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .claim("type", "refresh") // diferenciamos refresh del access
                // .claim  + lo que queramos setear  -> para despues recuperarlo
                .signWith(key)
                .compact();
    }

    public static String validarToken(String token) { //trata de fijarse si puede obtener el nombre de usuario (porq tiene seteado eso el profesor)
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
