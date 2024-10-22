package com.spring_security.persistence.entity.repository;

import com.spring_security.persistence.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity,Long> {
    //por sentencia sql
    List<RoleEntity> findRoleEntitiesByRoleEnumIn(List<String> roleNames);
}
