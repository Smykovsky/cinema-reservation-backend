package pl.smyk.common.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

@RequiredArgsConstructor
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String userRolesHeader = request.getHeader("X-User-Roles");
        String userPermissionsHeader = request.getHeader("X-User-Permissions");
        String userEmailHeader = request.getHeader("X-User-Email");

        if (userEmailHeader != null) {
            Collection<GrantedAuthority> authorities = new java.util.ArrayList<>();

            if (userRolesHeader != null && !userRolesHeader.isEmpty()) {
                Arrays.stream(userRolesHeader.split(","))
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                        .forEach(authorities::add);
            }

            if (userPermissionsHeader != null && !userPermissionsHeader.isEmpty()) {
                Arrays.stream(userPermissionsHeader.split(","))
                        .map(permission -> new SimpleGrantedAuthority(permission.trim()))
                        .forEach(authorities::add);
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userEmailHeader, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
