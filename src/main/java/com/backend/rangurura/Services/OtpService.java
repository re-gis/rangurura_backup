package com.backend.rangurura.services;


public interface OtpService {
    public boolean sendMessage(String phoneNumber, String messageBody);
    public String generateOtp(int length) throws Exception;
}
