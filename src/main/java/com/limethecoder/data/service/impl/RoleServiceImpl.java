package com.limethecoder.data.service.impl;


import com.limethecoder.data.domain.Role;
import com.limethecoder.data.repository.RoleRepository;
import com.limethecoder.data.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends AbstractJPAService<Role, String> implements RoleService {
    @Autowired
    RoleRepository repository;

    @Override
    protected JpaRepository<Role, String> getRepository() {
        return repository;
    }
}
