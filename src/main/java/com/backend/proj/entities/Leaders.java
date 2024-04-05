package com.backend.proj.entities;//package com.backend.proj.entities;

import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.EUrwego;
import com.backend.proj.enums.URole;

import lombok.*;

import java.util.UUID;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "leaders")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Leaders {
   @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(nullable = false)
    private String nationalId;

//    @Column(nullable = false)
//    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private EUrwego organizationLevel;

    @Enumerated(EnumType.STRING)
    private ECategory category;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    private URole role;

    @Column(nullable = false)
    private boolean verified;

}
