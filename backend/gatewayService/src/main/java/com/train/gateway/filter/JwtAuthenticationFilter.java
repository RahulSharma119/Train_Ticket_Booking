package com.train.gateway.filter;

import java.util.Arrays;
import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.train.gateway.security.JWTUtil;

import io.jsonwebtoken.JwtException;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtAuthenticationFilter implements WebFilter {
	

	private static final String HEADER_EMAIL = "X-User-Email";
    private static final String HEADER_ROLES = "X-User-Role";
	
	
	private JWTUtil jwtService;
	
	public JwtAuthenticationFilter(JWTUtil jwtService) {
		this.jwtService = jwtService;
	}



	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		String path = exchange.getRequest().getPath().value();
		if(path.startsWith("/api/users/login") || path.startsWith("/api/users/register")) {
			return chain.filter(exchange);
		}
		
		final String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return chain.filter(exchange);
        }
		
        
        try {
        	final String jwt = authHeader.substring(7);
        	final String userEmail = jwtService.extractUsername(jwt);
        	String role = (String)jwtService.extractClaim(jwt, c -> c.get("role"));
        	if(!jwtService.isTokenValid(jwt, userEmail)) {
        		throw new JwtException("Token Expired");
        	}
        	List<SimpleGrantedAuthority> authority = Arrays.stream(role.split(","))
					.map(String::trim)
					.filter(r -> !r.isEmpty() && r.length() > 0)
					.map(r -> new SimpleGrantedAuthority(r))
					.toList();
        	Authentication auth = new UsernamePasswordAuthenticationToken(userEmail, null, authority);
            
            exchange = exchange.mutate().request(exchange.getRequest().mutate()
                    .header(HEADER_EMAIL, userEmail)
                    .header(HEADER_ROLES, role != null ? role : "")
                    .build()).build();
            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
        } catch (JwtException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
	}
}
