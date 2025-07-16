package com.train.user.security;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Jwts;

import org.springframework.stereotype.Component;

@Component
public class JWTUtil {
	
	private static final long EXPIRATION_TIME = 86400000; // 1 day
    private static final Key key = Jwts.SIG.HS256.key().build();
    
    public String generateToken(String userName) {
    	return Jwts.builder()
    			.setSubject(userName)
    			.setIssuedAt(new Date())
    			.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
    			.signWith(key)
    			.compact();
    			
    }
    
    public String validateToken(String token) {
    	return Jwts.parser()
    			.setSigningKey(key)
    			.build()
    			.parseClaimsJwt(token)
    			.getBody()
    			.getSubject();
    }
}
