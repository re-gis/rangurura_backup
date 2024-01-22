package com.backend.rangurura.entities;

import lombok.*;

import javax.persistence.*;

import com.backend.rangurura.enums.ECategory;
import com.backend.rangurura.enums.ESuggestion;
import com.backend.rangurura.enums.EUrwego;

@Entity
@Table(name = "suggestions")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Suggestions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String phoneNumber;

    @Column(nullable = false)
    private String nationalId;

    @Column(nullable = false)
    private EUrwego urwego;

    @Column(nullable = false)
    private String location;

    @Column
    private String upperLevel;

    @Column(nullable = false)
    private ECategory category;

    @Column(nullable = false)
    private String igitekerezo;

    @Enumerated(EnumType.STRING)
    private ESuggestion status;

}
