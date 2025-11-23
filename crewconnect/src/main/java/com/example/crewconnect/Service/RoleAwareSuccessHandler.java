package com.example.crewconnect.Service;

import com.example.crewconnect.Repository.EmployeeRepository;
import com.example.crewconnect.Repository.ManagerRepository;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class RoleAwareSuccessHandler implements AuthenticationSuccessHandler {

    private final EmployeeRepository empRepo;
    private final ManagerRepository mgrRepo;

    public RoleAwareSuccessHandler(EmployeeRepository empRepo, ManagerRepository mgrRepo) {
        this.empRepo = empRepo;
        this.mgrRepo = mgrRepo;
    }

    @Override
    public void onAuthenticationSuccess(
            jakarta.servlet.http.HttpServletRequest req,
            jakarta.servlet.http.HttpServletResponse res,
            org.springframework.security.core.Authentication auth) throws java.io.IOException {

        var authorities = auth.getAuthorities();
        String username = auth.getName();

        // Admins skip password-change flow by default
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            res.sendRedirect("/admin");
            return;
        }

        // Manager path
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
            var opt = mgrRepo.findByUsername(username);
            if (opt.isPresent() && opt.get().isMustChangePassword()) {
                res.sendRedirect("/password/set");
            } else {
                res.sendRedirect("/manager");
            }
            return;
        }

        // Employee path
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"))) {
            var opt = empRepo.findByUsername(username);
            if (opt.isPresent() && opt.get().isMustChangePassword()) {
                res.sendRedirect("/password/set");
            } else {
                res.sendRedirect("/employee");
            }
            return;
        }

        // Fallback
        res.sendRedirect("/");
    }
}