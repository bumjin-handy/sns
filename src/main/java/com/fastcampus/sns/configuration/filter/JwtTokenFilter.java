package com.fastcampus.sns.configuration.filter;

import com.fastcampus.sns.model.User;
import com.fastcampus.sns.service.UserService;
import com.fastcampus.sns.util.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final String key;

    private final UserService userService;

    private final static List<String> TOKEN_IN_PARAM_URLS = List.of("/api/v1/users/alarm/subscribe");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String token;

        try {
            if (TOKEN_IN_PARAM_URLS.contains(request.getRequestURI())) {
                log.info("Request with {} check the query param", request.getRequestURI());
                token = request.getQueryString().split("=")[1].trim();
            } else if (header == null || !header.startsWith("Bearer ")) {
                log.error("Authorization Header does not start with Bearer {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            } else {
                token = header.split(" ")[1].trim();
            }

            // check token is valid
            if (JwtTokenUtils.isExpired(token, key)) {
                log.error("Key is expired");
                filterChain.doFilter(request, response);
                return;
            }
            // get user from token
            String username = JwtTokenUtils.userName(token, key);
            // check the user is valid
            User user = userService.loadUserByUserName(username);

            //UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
            //if(jwtService.validateToken(token, userDetails)){
                //UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
           // }


    //        filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
                log.error("Error occurs while validating {}", e.toString());
                filterChain.doFilter(request, response);
                return;
        }
        filterChain.doFilter(request, response);
    }
}
