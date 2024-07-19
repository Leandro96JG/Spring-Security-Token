package com.spring_security.config.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.spring_security.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

//Ejecuta el este filtro por cada request
public class JwtTokenValidator extends OncePerRequestFilter {

    private JwtUtils jwtUtils;

    public JwtTokenValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    //agregar nonnull porque nunca pueden ser nulos
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(jwtToken != null){ //token = Bearer sasdksadaksodzxc
            //extrae el string despues de los 7 caracteres
            jwtToken = jwtToken.substring(7);
            //valida el token recibo desde los headers
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
            //extraemos el username token
            String username = jwtUtils.extractUserName(decodedJWT);
            //claims del token en forma de string
            String stringAuthorities = jwtUtils.returnClaim(decodedJWT, "authorities");
            //Recibe los permisos separados por coma
            //Collection indíca que la colección puede contener cualquier tipo que sea una subclase de GrantedAuthority.
            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(stringAuthorities);

            //almacena la información de autenticación y de seguridad del usuario actual
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = new UsernamePasswordAuthenticationToken(username,null,authorities);
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }
        //rechaza en caso de cualquier error
        filterChain.doFilter(request,response);

    }
}
