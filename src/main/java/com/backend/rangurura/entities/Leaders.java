package com.backend.rangurura.entities;//package com.backend.rangurura.entities;

import com.backend.rangurura.enums.ECategory;
import com.backend.rangurura.enums.EUrwego;
import com.backend.rangurura.enums.URole;

import lombok.*;

import javax.persistence.*;

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
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private EUrwego origanizationLevel;

    @Enumerated(EnumType.STRING)
    private ECategory category;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    private URole role;

    @Column(nullable = false)
    private boolean verified;

}
