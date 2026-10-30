package org.kelompokwira.wirakopi.wirakopi.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

// AUTO GENERATED DO NOT EDIT (EDIT AT YOUR OWN RISK)

@Data
@NoArgsConstructor
@lombok.EqualsAndHashCode(onlyExplicitlyIncluded = true)
@lombok.ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "Users", uniqueConstraints = @UniqueConstraint(columnNames = {"username"}))
public class User implements UserDetails{
    @NotEmpty(message = "Username cannot be empty")
    private String username;
    @NotEmpty(message = "Password cannot be empty")
    private String password;
    @NotEmpty(message = "Email cannot be empty")
    private String email;
    private boolean enabled = false;
    private Long OTP = (long) 0;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @lombok.EqualsAndHashCode.Include
    @lombok.ToString.Include
    private Long id;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = false)
    private List<UserAuthorities> listAuthorities = new ArrayList<>();
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = false)
    @JsonManagedReference
    private UserStuff userStuff;
    @NotEmpty
    private String name;
    @NotEmpty
    private String no_telp;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.listAuthorities;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}
