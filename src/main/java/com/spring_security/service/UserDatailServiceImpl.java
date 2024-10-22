package com.spring_security.service;

import com.spring_security.controller.dto.AuthLoginRequest;
import com.spring_security.controller.dto.AuthRegisterRequest;
import com.spring_security.controller.dto.AuthReponse;
import com.spring_security.persistence.entity.RoleEntity;
import com.spring_security.persistence.entity.UserEntity;
import com.spring_security.persistence.entity.repository.RoleRepository;
import com.spring_security.persistence.entity.repository.UserRepository;
import com.spring_security.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDatailServiceImpl implements UserDetailsService{
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    private UserDatailServiceImpl(UserRepository userRepository,
                                  JwtUtils jwtUtils,
                                  PasswordEncoder passwordEncoder,
                                  RoleRepository rolerepository){
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = rolerepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findUserEntityByUsername(username)
                //orElseThrow se usa para los optional en caso de no existir un user
                .orElseThrow(()-> new UsernameNotFoundException("El usuario "+username+" no existe."));

        //es de spring security
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        //estamos tomando los roles del usuario y transformando en un objeto que entienda spring security
        userEntity.getRoles()
                //agregamos las autoridades del user encontrado a la authority list
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        //.stream es como un pipe en rxjs
        //Aca estamos tomando los permisos de los roles y los autorizamos para que SS los tenga
        userEntity.getRoles().stream()
                //aplana el flujo de datos -- hacemos un flujo de dato de permisos
                .flatMap(role -> role.getPermissionList().stream())
                //a cada permiso se agrega a la lista de autoridades
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        //retornamos un User en lenguaje de spring security
        return new User(userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNoExpired(),
                userEntity.isCredentialNoExpired(),
                userEntity.isAccountNoLocked(),
                authorityList);
    }

    public AuthReponse loginUser(AuthLoginRequest authLoginRequest){
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();
        Authentication authentication = this.authenticate(username,password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = this.jwtUtils.createToken(authentication);
        //AuthReponse authReponse
        return new AuthReponse(username,"User loged succesfull",accessToken,true);

    }

    private Authentication authenticate(String username, String password){
        UserDetails userDetails = this.loadUserByUsername(username);
        if(userDetails == null){
            throw new BadCredentialsException("Invalid username or password.");
        }
        //comparamos los password con bcrip
        if(!passwordEncoder.matches(password,userDetails.getPassword())){
            throw new BadCredentialsException("Invalid password");
        }
        return  new UsernamePasswordAuthenticationToken(username,userDetails.getPassword(),userDetails.getAuthorities());
    }

    public AuthReponse createUser(AuthRegisterRequest authRegisterRequest){


        String username = authRegisterRequest.username();
        String password = authRegisterRequest.password();
        List<String> roleRequest = authRegisterRequest.roleRequest().roleListName();

        //convertimos la lista a un set
        Set<RoleEntity> roleEntities = this.roleRepository.findRoleEntitiesByRoleEnumIn(roleRequest).stream().collect(Collectors.toSet());
        if (roleEntities.isEmpty()){
            throw new IllegalArgumentException("The roles specified does not exist.");
        }
        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(roleEntities)
                .isEnabled(true)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .build();
    UserEntity userCreated = userRepository.save(userEntity);

    List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
    userCreated.getRoles().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));
    userCreated.getRoles()
            .stream()
            .flatMap(role -> role.getPermissionList().stream())
            .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getUsername(),userCreated.getPassword(),authorityList);

        String accessToken = jwtUtils.createToken(authentication);

      return new AuthReponse(userCreated.getUsername(),"User created successfully",accessToken,true);
    }
}
