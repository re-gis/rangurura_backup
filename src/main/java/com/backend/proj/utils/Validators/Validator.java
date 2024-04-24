package com.backend.proj.utils.Validators;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.backend.proj.entities.Events;
import com.backend.proj.enums.EEvent;
import com.backend.proj.exceptions.BadRequestException;
import com.backend.proj.repositories.EventRepository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Component
// @AllArgsConstructor
// @NoArgsConstructor
public class Validator {
    private static EventRepository eventRepository;

    @Autowired
    public Validator(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public static void ValidateEventTime(LocalDateTime openDate, LocalDateTime closeDate) {
        System.out.println(openDate + " : " + closeDate);
        if (openDate.isBefore(LocalDateTime.now()))
            throw new BadRequestException("The start date should not be lower than to day ");
        if (closeDate.isBefore(openDate))
            throw new BadRequestException("The close date should not be lower than open date");
        if (closeDate.isBefore(LocalDateTime.now()) || openDate.isBefore(LocalDateTime.now()))
            throw new BadRequestException("The close/open dates should not be lower than to day");
    }

    public static void validateTimeInterval(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            System.out.println(startDate + " and : " + endDate);
            throw new BadRequestException("The start date should not be after the end date");
        }
        if (startDate.equals(endDate)) {
            throw new BadRequestException("The start and end dates should not be equal");
        }
    }

    public static boolean isEventClosed(Events event) {
        Optional<Events> e = eventRepository.findByIdAndStatus(event.getId(), EEvent.CANCELED);
        if (e.get() == null || !e.isPresent()) {
            return false;
        }
        if (e.get().getStatus() == EEvent.PENDING || e.get().getStatus() == EEvent.POSTPONED) {
            return false;
        }
        return true;
    }

    public static void validatePhonePasswordAndNationalId(String phone, String password, String nationalId){
        if(!password.matches("^(?=.*[A-Z])(?=.*[!@#$%^&*()-_+=]).{8,20}$")){
            throw new BadRequestException("Pasword must be between 8 and 20 characters, contain at least one capital case and at least one special character!");
        }

        if(!nationalId.matches("^\\d{20}$")){
            throw new BadRequestException("Please provide a valid national id");
        }

        if(!phone.matches("^\\+250\\d{9}$")){
            throw new BadRequestException("Please provide a valid phone number!");
        }
    }
}
