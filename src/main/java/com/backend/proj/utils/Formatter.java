package com.backend.proj.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.ResponseEntity;

import com.backend.proj.response.ApiResponse;

public class Formatter {
    public static DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ofPattern("yyyy/MM/dd");
    }

    public static DateTimeFormatter getLocalDateFormatter() {
        return DateTimeFormatter.ofPattern("yyyy/MM/dd");
    }

    public static DateTimeFormatter getLocalDateTimeFormater() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public static ResponseEntity<ApiResponse<Object>> ok(Object body) {
        return ResponseEntity.ok(ApiResponse.builder().data(body).success(true).build());
    }

    public static ResponseEntity<ApiResponse<Object>> ok(String message) {
        return ResponseEntity.ok(ApiResponse.builder().data(message).success(true).build());
    }

    public static ResponseEntity<ApiResponse<Object>> done() {
        return ResponseEntity.ok(ApiResponse.builder().data("Done").success(true).build());
    }

    public static ResponseEntity<ApiResponse<Object>> failed(String message) {
        return ResponseEntity.badRequest().body(ApiResponse.builder().data(message).success(false).build());
    }

    public static LocalDateTime getStartLocalDateTime(String startDate) {
        LocalDateTime parsedStartDate = LocalDateTime.parse(startDate, Formatter.getLocalDateFormatter());
        return parsedStartDate;
    }

    public static LocalDateTime getEndLocalDateTime(String endDate) {
        LocalDateTime parsedEndDate = LocalDateTime.parse(endDate, Formatter.getLocalDateFormatter());
        return parsedEndDate;
    }

    public static String isFormedLocalDate() {
        return "verified";
    }
}
