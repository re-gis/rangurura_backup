package com.backend.rangurura.entities;

import com.backend.rangurura.enums.ECategory;
import com.backend.rangurura.enums.EProblem_Status;
import com.backend.rangurura.enums.EUrwego;

import lombok.*;

import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor
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

    @Column(nullable = false)
    private String ikibazo;

    @Column(nullable = false)
    private String proofUrl;

    @Column(nullable = false)
    private String recordUrl;

    @Column(nullable = false)
    private String owner_id;

    @Enumerated(EnumType.STRING)
    private EProblem_Status status;
}
