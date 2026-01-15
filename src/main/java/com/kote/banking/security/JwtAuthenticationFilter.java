package com.kote.banking.security;

import com.kote.banking.service.CustomUserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final CustomUserDetailsServiceImpl customUserDetailsServiceImpl;
    protected final JwtService jwtService;

    public JwtAuthenticationFilter(CustomUserDetailsServiceImpl customUserDetailsServiceImpl, JwtService jwtService) {
        this.customUserDetailsServiceImpl = customUserDetailsServiceImpl;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = parseJwt(request);

        try {
            if (token != null) {
                Object tenant = jwtService.getTenant(token);
                if (tenant != null) {
                    TenantContext.setTenantName(tenant.toString());
                }

                String username = jwtService.getUsername(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = customUserDetailsServiceImpl.loadUserByUsername(username);

                    if (jwtService.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Could not set user authentication in security context: {}", e.getMessage());
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
    public String parseJwt(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")){
            return authorization.substring(7);
        }
        return null;
    }
}
