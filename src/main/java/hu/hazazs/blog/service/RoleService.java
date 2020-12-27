package hu.hazazs.blog.service;

import hu.hazazs.blog.entity.Role;
import hu.hazazs.blog.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Value("${service.role.default}")
    private String defaultRole;
    private RoleRepository roleRepository;
    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    public Role getDefaultRole() {
        return roleRepository.findByName(defaultRole);
    }
}