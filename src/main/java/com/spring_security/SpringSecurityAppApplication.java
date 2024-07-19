package com.spring_security;

import com.spring_security.persistence.entity.PermissionEntity;
import com.spring_security.persistence.entity.RoleEntity;
import com.spring_security.persistence.entity.RoleEnum;
import com.spring_security.persistence.entity.UserEntity;
import com.spring_security.persistence.entity.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class SpringSecurityAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityAppApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository){
		return args -> {
			//! Create Permssions
			PermissionEntity createPermission = PermissionEntity.builder()
					.name("CREATE")
					.build();
			PermissionEntity readPermission = PermissionEntity.builder()
					.name("READ")
					.build();
			PermissionEntity updatePermission = PermissionEntity.builder()
					.name("UPDATE")
					.build();
			PermissionEntity deletePermission = PermissionEntity.builder()
					.name("DELETE")
					.build();
			PermissionEntity refactorPermission = PermissionEntity.builder()
					.name("REFACTOR")
					.build();
			//! Create Roles
			RoleEntity roleAdmin = RoleEntity.builder()
					.roleEnum(RoleEnum.ADMIN)
					.permissionList(Set.of(createPermission,readPermission,updatePermission,deletePermission))
					.build();
			RoleEntity roleUser = RoleEntity.builder()
					.roleEnum(RoleEnum.USER)
					.permissionList(Set.of(readPermission,createPermission))
					.build();
			RoleEntity roleInvited = RoleEntity.builder()
					.roleEnum(RoleEnum.INVITED)
					.permissionList(Set.of(readPermission))
					.build();
			RoleEntity roleDeveloper = RoleEntity.builder()
					.roleEnum(RoleEnum.DEVELOPER)
					.permissionList(Set.of(readPermission,refactorPermission,createPermission))
					.build();

			//! Create User

			UserEntity user1 = UserEntity.builder()
					.username("leadro")
					.password("$2a$10$v0eXIkCE/2Z51GzghWwvI.rWRZq9CKce0LV.hq6xaJnTcBvMmuzGe")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleAdmin))
					.build();
			UserEntity user2 = UserEntity.builder()
					.username("jesus")
					.password("$2a$10$v0eXIkCE/2Z51GzghWwvI.rWRZq9CKce0LV.hq6xaJnTcBvMmuzGe")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleUser))
					.build();
			UserEntity user3 = UserEntity.builder()
					.username("ramon")
					.password("$2a$10$v0eXIkCE/2Z51GzghWwvI.rWRZq9CKce0LV.hq6xaJnTcBvMmuzGe")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleInvited))
					.build();
			UserEntity user4 = UserEntity.builder()
					.username("carlitos")
					.password("$2a$10$v0eXIkCE/2Z51GzghWwvI.rWRZq9CKce0LV.hq6xaJnTcBvMmuzGe")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleDeveloper))
					.build();
			
			userRepository.saveAll(List.of(user1,user2,user3,user4));
		};
	}
}
