package com.backend.proj.entities;

import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.EProblem_Status;
import com.backend.proj.enums.EUrwego;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

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
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private ECategory category;

    @Enumerated(EnumType.STRING)
    private EUrwego urwego;

    @Column(nullable=false)
    private String target;

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

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


}
