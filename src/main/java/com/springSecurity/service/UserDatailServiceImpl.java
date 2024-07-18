package com.springSecurity.service;

import com.springSecurity.persistence.entity.UserEntity;
import com.springSecurity.persistence.entity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDatailServiceImpl implements UserDetailsService{

    public final UserRepository userRepository;

    @Autowired
    private UserDatailServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
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
}
