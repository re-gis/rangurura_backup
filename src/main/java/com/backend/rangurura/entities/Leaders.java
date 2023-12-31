package com.backend.rangurura.entities;

import com.backend.rangurura.enums.URole;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "leaders")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Leaders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nationalId;

    @Column(nullable = false)
    private String origanizationLevel;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    private URole role;

    @Column(nullable = false)
    private boolean verified;

}
