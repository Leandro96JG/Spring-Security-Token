package com.spring_security.persistence.entity.repository;

import com.spring_security.persistence.entity.UserEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity,Long> {

    Optional<UserEntity> findUserEntityByUsername(String username);

    //Otra forma
    //@Query("SELECT u FROM UserEntity u WHERE u.username = ?")
    //Optional<UserEntity> findUser(String username);
}
