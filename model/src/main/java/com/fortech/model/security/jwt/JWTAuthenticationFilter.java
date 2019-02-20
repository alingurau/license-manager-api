package com.fortech.model.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fortech.model.entity.User;
import com.fortech.model.exceptions.RestExceptions;
import com.fortech.model.message.request.AuthenticationResponse;
import com.fortech.model.message.response.AuthResponse;
import com.fortech.model.security.services.UserDetailsServiceImpl;
import com.fortech.model.repository.UserRepository;
import com.fortech.model.security.services.BaseLogger;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService) {

        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(SecurityConstants.SIGN_IN_URL, "POST"));
        this.authenticationManager = authenticationManager;
        this.userRepository = userDetailsService.getUserEntityOnSignIn();

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) {

        try {

            User user = new ObjectMapper().readValue(req.getInputStream(), User.class);
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getPassword(),
                    new ArrayList<>()
            );

            return authenticationManager.authenticate(authRequest);

        } catch (IOException e) {

            BaseLogger.log(JWTAuthenticationFilter.class).error(e.getMessage());
            throw new RestExceptions.Forbidden(e.getMessage());

        }

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        try {

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter out = response.getWriter();

            AuthenticationResponse body = new AuthenticationResponse();
            body.setMessage("Invalid credentials. Please check and try again.");

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(out, body);
            out.close();

        } catch (IOException e) {

            BaseLogger.log(JWTAuthenticationFilter.class).error(e.getMessage());
            throw new RestExceptions.OperationFailed(e.getMessage());

        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) {

        String token = Jwts.builder()
                .setSubject(((org.springframework.security.core.userdetails.User) auth.getPrincipal())
                        .getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET.getBytes())
                .compact();

        String username = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username);

        try {

            res.setContentType("application/json");
            PrintWriter out = res.getWriter();

            AuthResponse response = new AuthResponse();
            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());
            response.setRole(user.getRole());
            response.setToken(token);
            response.setUsername(user.getUsername());
            response.setId(user.getId());

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(out, response);
            out.close();

        } catch (Exception e) {

            BaseLogger.log(JWTAuthenticationFilter.class).error(e.getMessage());
            throw new RestExceptions.OperationFailed(e.getMessage());

        }

    }
}
