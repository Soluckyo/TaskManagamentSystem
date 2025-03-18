package org.lib.taskmanagamentsystem.jwt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.lib.taskmanagamentsystem.dto.JwtResponseDTO;
import org.lib.taskmanagamentsystem.entity.Role;
import org.lib.taskmanagamentsystem.entity.User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
public class JwtUtils {

    private static final String SECRET = "qBTmv4oXFFR2GwjexDJ4t6fsIUIUhhXqlktXjXdkcyygs8nPVEwMfo29VDRRepYDVV5IkIxBMzr7OEHXEHd37w==";

    //генерация аутентификационного токена(складываем токены в dto, чтобы потом отдать)
    public JwtResponseDTO generateAuthToken(User user) {
        JwtResponseDTO jwtResponseDTO = new JwtResponseDTO();
        jwtResponseDTO.setAccessToken(generateJwtToken(user));
        jwtResponseDTO.setRefreshToken(generateRefreshToken(user));
        return jwtResponseDTO;
    }

    //обновление jwt токена с помощью refresh токена
    public JwtResponseDTO refreshBaseToken(User user, String refreshToken) {
        JwtResponseDTO jwtResponseDTO = new JwtResponseDTO();
        jwtResponseDTO.setAccessToken(generateJwtToken(user));
        jwtResponseDTO.setRefreshToken(refreshToken);
        return jwtResponseDTO;
    }

    //проверка на валидность(правильный ли токен)
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        }catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
            return false;
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
            return false;
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
            return false;
        } catch (SignatureException sEx) {
            log.error("Invalid signature", sEx);
            return false;
        } catch (Exception e) {
            log.error("invalid token", e);
            return false;
        }
    }


    //генерация jwt токена
    public String generateJwtToken(User user) {
        Date date = Date.from(LocalDateTime.now().plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant());
        Role role = user.getRole();
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", role.name())
                .setExpiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    //генерация refresh токена
    public String generateRefreshToken(User user) {
        Date date = Date.from(LocalDateTime.now().plusDays(10).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    //расшифровка секрета
    public SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    }

    //получения почты из токена
    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    //получение роли из токена
    public Role getRoleFromToken(String token) {
        return Role.valueOf(getClaimsFromToken(token).get("role", String.class));
    }

    //получение claims
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
