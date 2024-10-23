package com.project.demo.logic.entity.rol;

import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public AdminSeeder(
            RoleRepository roleRepository,
            UserRepository  userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createSuperAdministrator();
        this.createOtherUser();
    }

    private void createSuperAdministrator() {
        User superAdmin = new User();
        superAdmin.setName("Super");
        superAdmin.setLastname("Admin");
        superAdmin.setEmail("super.admin@gmail.com");
        superAdmin.setPassword("superadmin123");

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.SUPER_ADMIN);
        Optional<User> optionalUser = userRepository.findByEmail(superAdmin.getEmail());

        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            return;
        }

        var user = new User();
        user.setName(superAdmin.getName());
        user.setLastname(superAdmin.getLastname());
        user.setEmail(superAdmin.getEmail());
        user.setPassword(passwordEncoder.encode(superAdmin.getPassword()));
        user.setRole(optionalRole.get());

        userRepository.save(user);
    }

    private void createOtherUser() {
        User normalUser = new User();
        normalUser.setName("User");
        normalUser.setLastname("User");
        normalUser.setEmail("user@gmail.com");
        normalUser.setPassword("123");

        Optional<Role> optionalRole2 = roleRepository.findByName(RoleEnum.USER);
        Optional<User> optionalUser2 = userRepository.findByEmail(normalUser.getEmail());

        if (optionalRole2.isEmpty() || optionalUser2.isPresent()) {
            return;
        }

        var user2 = new User();
        user2.setName(normalUser.getName());
        user2.setLastname(normalUser.getLastname());
        user2.setEmail(normalUser.getEmail());
        user2.setPassword(passwordEncoder.encode(normalUser.getPassword()));
        user2.setRole(optionalRole2.get());

        userRepository.save(user2);
    }
}
