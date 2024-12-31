package org.tuanna.xcloneserver.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
//        final String header = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
//        if (Strings.isNullOrEmpty(header) || !header.startsWith("Bearer ")) {
//            chain.doFilter(servletRequest, servletResponse);
//            return;
//        }
//
//        final String token = header.split(" ")[1].trim();
//        if (!jwtTokenUtil.validate(token)) {
//            chain.doFilter(servletRequest, servletResponse);
//            return;
//        }
//
//        UsernamePasswordAuthenticationToken
//                authentication = new UsernamePasswordAuthenticationToken(
//                userDetails, null,
//                userDetails == null ?
//                        List.of() : userDetails.getAuthorities()
//        );
//
//        authentication.setDetails(
//                new WebAuthenticationDetailsSource().buildDetails(servletRequest)
//        );
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(servletRequest, servletResponse);
    }

}
