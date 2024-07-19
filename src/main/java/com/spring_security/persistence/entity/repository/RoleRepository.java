package com.spring_security.persistence.entity.repository;

import com.spring_security.persistence.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoleRepository extends CrudRepository<RoleEntity,Long> {
    //por sentencia sql
    List<RoleEntity> findRoleEntitiesByRoleEnumIn(List<String> roleNames);
}
