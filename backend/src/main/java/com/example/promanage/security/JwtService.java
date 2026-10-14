package com.example.promanage.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * Responsable de generar y validar los JSON Web Token.
 * - Obtiene la clave configurada (jwt.secret, en Base64).
 * - Genera el JWT con subject (username), fecha de emisión y expiración.
 * - Extrae y valida el username a partir de un token.
 */
@Service
public class JwtService {

	private final SecretKey key;
	private final long expiration;

	public JwtService(
			@Value("${jwt.secret}") String secret,
			@Value("${jwt.expiration}") long expiration) {
		this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
		this.expiration = expiration;
	}

	/** Genera un JWT firmado para el usuario autenticado. */
	public String generarToken(String username) {
		Date ahora = new Date();
		return Jwts.builder()
				.subject(username)
				.issuedAt(ahora)
				.expiration(new Date(ahora.getTime() + expiration))
				.signWith(key)
				.compact();
	}

	/** Valida la firma del token y devuelve el username (subject). */
	public String extraerUsername(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}
}
