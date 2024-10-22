package com.spring_security.config;

import com.spring_security.config.filter.JwtTokenValidator;
import com.spring_security.service.UserDatailServiceImpl;
import com.spring_security.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;

//    @Bean
//    //httpSecurity pasa por todos los filtros y lo van modificando
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        return httpSecurity
//                //proteje de atancante usando usuarios logeados
//                .csrf(csrf -> csrf.disable())
//                //Para logearse sin token
//                .httpBasic(Customizer.withDefaults())
//                //para cuanto tiempo mantener una sesion activa
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                //
//                .authorizeHttpRequests(http -> {
//
//                    // Configurar los endpoints publicos
//                    http.requestMatchers(HttpMethod.GET,"/auth/hello").permitAll();
//
//                    // Configurar los endpoints privados
//                    http.requestMatchers(HttpMethod.GET,"/auth/hello-secured").hasAuthority("READ");
//
//                    // Configurar el resto de endpoint - NO ESPECIFICADOS
//                    http.anyRequest().denyAll();
//                })
//                .build();
//    }

    @Bean
    //httpSecurity pasa por todos los filtros
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                //proteje de atancante usando usuarios logeados
                .csrf(csrf -> csrf.disable())
                //Para logearse sin token
                .httpBasic(Customizer.withDefaults())
                //para cuanto tiempo mantener una sesion activa
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //validar antes del filtro basico de auth
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    //control de las authenticaciones
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    //Un metodo de authenticacion
    @Bean
    public AuthenticationProvider authenticationProvider(UserDatailServiceImpl userDatailService){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDatailService);
        return provider;
    }
//    @Bean
//    public UserDetailsService userDetailsService(){
//        List<UserDetails> userDetailsList = new ArrayList<>();
//
//        userDetailsList.add(User.withUsername("leandro")
//                .password("root1")
//                .roles("ADMIN")
//                .authorities("READ","CREATE")
//                .build());
//
//        userDetailsList.add(User.withUsername("jesus")
//                .password("root2")
//                .roles("USER")
//                .authorities("READ")
//                .build());
//
//        return new InMemoryUserDetailsManager(userDetailsList);
//    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
