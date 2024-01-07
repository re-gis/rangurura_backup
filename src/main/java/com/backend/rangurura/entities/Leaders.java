package com.backend.rangurura.entities;//package com.backend.rangurura.entities;
//
import com.backend.rangurura.enums.ECategory;
import com.backend.rangurura.enums.EUrwego;
import com.backend.rangurura.enums.URole;

import lombok.*;

import javax.persistence.*;


//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;

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

    @Enumerated(EnumType.STRING)
    private EUrwego origanizationLevel;

    @Enumerated(EnumType.STRING)
    private ECategory category;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    private URole role;

//    @Column(nullable = false)
//    private boolean verified;

}



//import lombok.*;
//
//import javax.persistence.*;

//@Entity
//@Table(name = "patient")
//@Data
//public class Patient {
//    @Id
//    @Column(name = "patient_id", length = 45)
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private int patientID;
//
//    @Column(name = "patient_name", length = 100, nullable = false)
//    private String patientName;
//
//    @Column(name = "patient_address", length = 150, nullable = false)
//    private String patientAddress;
//
//    @Column(name = "contact_number", length = 11)
//    private String contactNumber;
//
//    @Column(name = "patient_disease")
//    private String patientDisease;
//
//}