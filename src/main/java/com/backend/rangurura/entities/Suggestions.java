package com.backend.rangurura.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "suggestions")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Suggestions{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String phoneNumber;


    @Column(nullable = false)
    private String urwego;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String igitekerezo;



}
