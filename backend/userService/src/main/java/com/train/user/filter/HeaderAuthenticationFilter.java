package com.train.user.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {
	
	private static final String HEADER_EMAIL = "X-User-Email";
    private static final String HEADER_ROLES = "X-User-Role";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String email = request.getHeader(HEADER_EMAIL);
		String role = request.getHeader(HEADER_ROLES);
		
		if(email != null && role != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			List<SimpleGrantedAuthority> roles = Arrays.stream(role.split(","))
					.map(String::trim)
					.filter(r -> !r.isEmpty() && r.length() > 0)
					.map(r -> new SimpleGrantedAuthority(r))
					.toList();
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, roles);
			auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
		
		filterChain.doFilter(request, response);
	}

}
