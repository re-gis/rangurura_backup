package com.backend.proj.entities;

import com.backend.proj.enums.URole;
import lombok.*;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;


@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String sector;

    @Column(nullable = false)
    private String cell;

    @Column(nullable = false)
    private String village;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String nationalId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private boolean verified;

    @Enumerated(EnumType.STRING)
    private URole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getUsername(){
        return nationalId;
    }

    public String getRealName(){
        return this.username;
    }
}
