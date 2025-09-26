package com.train.user.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.train.user.security.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	
	JWTUtil jwtUtil;
	
	public JwtAuthFilter(JWTUtil util) {
		jwtUtil = util;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String authHeader = request.getHeader("Authorization");
		
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			try {
                String token = authHeader.substring(7);

                final String userEmail = jwtUtil.extractUsername(token);

                String role = (String)jwtUtil.extractClaim(token, c -> c.get("role"));
                
                if(jwtUtil.isTokenValid(token, userEmail)) {
                	List<SimpleGrantedAuthority> authority = Arrays.stream(role.split(","))
        					.map(String::trim)
        					.filter(r -> !r.isEmpty() && r.length() > 0)
        					.map(r -> new SimpleGrantedAuthority(r))
        					.toList();
                	Authentication auth = new UsernamePasswordAuthenticationToken(userEmail, null, authority);
                	SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
		}
		
		filterChain.doFilter(request, response);
	}

}
