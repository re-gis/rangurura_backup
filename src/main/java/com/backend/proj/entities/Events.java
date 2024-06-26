package com.backend.proj.entities;

import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.EEvent;
import com.backend.proj.enums.EUrwego;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Events {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(nullable = false)
    private String eventName;

    @Enumerated(EnumType.STRING)
    private EUrwego organizationLevel;

    @Enumerated(EnumType.STRING)
    private ECategory category;

    @Column(nullable = false)
    private String location;

    // @Column(nullable = false)
    // private String startDate;

    // @Column(nullable = false)
    // private String endDate;

    @Column(nullable = false, name = "startDateTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime;

    @Column(nullable = false, name = "endDateTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private String descriptions;

    @Column(nullable = false)
    private String owner;

    @Enumerated(EnumType.STRING)
    private EEvent status;

}
