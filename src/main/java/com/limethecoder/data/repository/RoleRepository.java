package com.limethecoder.data.repository;


import com.limethecoder.data.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String>{
}
