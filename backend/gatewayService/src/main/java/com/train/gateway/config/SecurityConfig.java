package com.train.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebFilter;

import com.train.gateway.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	
	private JwtAuthenticationFilter jwtFilter;
	
	public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
		this.jwtFilter = jwtFilter;
	}

	@Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/users/login/**", "/api/users/register/**").permitAll()
                .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                .anyExchange().authenticated()
                .and()
                .addFilterBefore((WebFilter)jwtFilter,  SecurityWebFiltersOrder.FIRST)
            )
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable());

        return http.build();
	}
	
}
