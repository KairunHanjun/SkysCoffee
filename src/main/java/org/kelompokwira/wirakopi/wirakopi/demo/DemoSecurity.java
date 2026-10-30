package org.kelompokwira.wirakopi.wirakopi.demo;

import org.kelompokwira.wirakopi.wirakopi.Repository.WiraRepo;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile({"demo", "test"})
public class DemoSecurity {
    @Bean PasswordEncoder demoPasswordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean UserDetailsService demoUserDetails(WiraRepo users) {
        return username -> {
            var user = users.findByUsername(username).stream().findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("Account not found."));
            return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword()).roles("CUSTOMER").disabled(!user.isEnabled()).build();
        };
    }
    @Bean SecurityFilterChain demoFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(request -> request
            .requestMatchers("/", "/menu", "/product", "/about", "/static/**", "/auth/**", "/css/**", "/js/**", "/img/**", "/error").permitAll()
            .anyRequest().authenticated())
            .formLogin(form -> form.loginPage("/auth/login").defaultSuccessUrl("/menu", true).failureUrl("/auth/login?error"))
            .logout(logout -> logout.logoutSuccessUrl("/auth/login?logout"))
            .build();
    }
}
