package com.backend.rangurura.entities;

import com.backend.rangurura.enums.ECategory;
import com.backend.rangurura.enums.EProblem_Status;
import com.backend.rangurura.enums.EUrwego;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@Data
@Getter
@Setter
@Entity
@Table(name = "problems")
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ECategory category;

    @Enumerated(EnumType.STRING)
    private EUrwego urwego;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = true)
    private String ikibazo;

    @Column(nullable = true)
    private String proofUrl;

    @Column(nullable = true)
    private String recordUrl;

    @Column(nullable = false)
    private String owner;

    @Enumerated(EnumType.STRING)
    private EProblem_Status status;
}
