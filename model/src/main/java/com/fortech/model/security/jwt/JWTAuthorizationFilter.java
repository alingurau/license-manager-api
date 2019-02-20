package com.fortech.model.security.jwt;

import com.fortech.model.security.services.UserDetailsServiceImpl;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserDetailsServiceImpl userDetailsService;

    public JWTAuthorizationFilter(AuthenticationManager authManager, UserDetailsServiceImpl userDetailsService) {
        super(authManager);
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        String header = req.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        if (authentication == null) {
            chain.doFilter(req, res);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        String token = request.getHeader(SecurityConstants.HEADER_STRING);

        try {

            if (token != null) {
                // parse the token.
                String user = Jwts.parser()
                        .setSigningKey(SecurityConstants.SECRET.getBytes())
                        .parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                        .getBody()
                        .getSubject();

                if (user != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user);
                    return new UsernamePasswordAuthenticationToken(user, null, userDetails.getAuthorities());
                }
            }

        } catch (Exception e) {

            return null;

        }

        return null;
    }
}
