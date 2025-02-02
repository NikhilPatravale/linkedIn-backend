package com.linkedIn.linkedIn.features.authentication.filter;

import com.linkedIn.linkedIn.features.authentication.model.AuthenticationUser;
import com.linkedIn.linkedIn.features.authentication.service.AuthenticationUserService;
import com.linkedIn.linkedIn.features.authentication.utils.JsonWebToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthenticationFilter extends HttpFilter {
    @Autowired
    private JsonWebToken jsonWebToken;

    @Autowired
    private AuthenticationUserService authenticationUserService;

    List<String> unsecuredRoutes = Arrays.asList(
            "/api/v1/authentication/login",
            "/api/v1/authentication/register",
            "/api/v1/authentication/send-password-reset-token",
            "/api/v1/authentication/reset-password"
    );

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.addHeader("Access-Control-Allow-Credentials", "true");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String path = request.getRequestURI();

        if (unsecuredRoutes.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authorisation = request.getHeader("Authorization");
            String token = null;

            if (path.startsWith("/ws")) {
                token = request.getParameter("token");
            } else if (authorisation != null && authorisation.startsWith("Bearer ")) {
                token = authorisation.substring(7);
            }

            if (token == null || token.isEmpty()) {
                throw new ServletException("Token missing");
            }

            if (jsonWebToken.isTokenExpired(token)) {
                throw new ServletException("Invalid token");
            }

            String email = jsonWebToken.getEmailFromToken(token);
            AuthenticationUser user = authenticationUserService.findByEmail(email);

            request.setAttribute("authenticatedUser", user);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Invalid authentication token or token missing \"}");
        }
    }
}
