package com.backend.proj.Services;

public interface OtpService {
    public void sendMessage(String phoneNumber, String messageBody);

    public String generateOtp(int length) throws Exception;
}
