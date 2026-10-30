package org.kelompokwira.wirakopi.wirakopi.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// AUTO GENERATED DO NOT EDIT (EDIT AT YOUR OWN RISK)

@org.springframework.context.annotation.Profile("!demo & !test")
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests((req) -> req.requestMatchers("/auth/**").permitAll()
        .requestMatchers("/static/**").permitAll().requestMatchers("/").permitAll()
        .requestMatchers("/css/**").permitAll().requestMatchers("/img/**").permitAll()
        .requestMatchers("/error/**").permitAll().requestMatchers("/error").permitAll()
        .requestMatchers("/auth/login/{username}").permitAll()
        .requestMatchers("/auth/login/{password}").permitAll()
        .anyRequest().authenticated())
        .exceptionHandling(
            (error) -> error
            .accessDeniedPage("/error/forbidYou")
            )
        .formLogin(
            (login) -> 
            login.loginPage("/auth/login").permitAll()
            .usernameParameter("username")
            .passwordParameter("password")
            .defaultSuccessUrl("/", true)
            .failureUrl("/error/errorForYou")
        )
        .logout(
            (logout) -> logout
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl("/auth/login")    
        )
        .sessionManagement((session) -> session.maximumSessions(1).expiredUrl("/auth/login?expired=true"))
        .build();
    }

    @Bean
    public WebMvcConfigurer cors(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@SuppressWarnings("null") CorsRegistry registry){
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8080")
                        .allowedHeaders(HttpHeaders.CONTENT_TYPE, "X-CSRF-TOKEN")
                        .allowedMethods(HttpMethod.GET.name(), HttpMethod.POST.name())
                        .allowCredentials(true);
            }

            @Override
            public void addResourceHandlers(@SuppressWarnings("null") ResourceHandlerRegistry registry){
                registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
                registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/");
                registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
                registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
            } 
        };
    }

    @Bean
    PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
}