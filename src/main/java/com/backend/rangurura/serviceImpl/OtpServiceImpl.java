package com.backend.rangurura.serviceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backend.rangurura.exceptions.MessageSendingException;
import com.backend.rangurura.services.OtpService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import java.security.SecureRandom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OtpServiceImpl implements OtpService {
    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    @Override
    public boolean sendMessage(String toPhoneNumber, String messageBody) {
        try {

            Twilio.init(accountSid, authToken);

            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    messageBody).create();
            System.out.println("Message sent to: " + toPhoneNumber);
            return true;
        } catch (MessageSendingException e) {
            throw new MessageSendingException("Error while sending OTP...");
        }
    }

    @Override
    public String generateOtp(int length) throws Exception {
        try {
            if (length <= 0) {
                throw new IllegalArgumentException("Length should be greater than zero");
            }

            StringBuilder otp = new StringBuilder();
            SecureRandom secureRandom = new SecureRandom();

            // Define the characters allowed in the OTP
            String characters = "0123456789";

            for (int i = 0; i < length; i++) {
                int randomIndex = secureRandom.nextInt(characters.length());
                char randomChar = characters.charAt(randomIndex);
                otp.append(randomChar);
            }

            return otp.toString();
        } catch (Exception e) {
            throw new Exception("Error while generating the OTP...");
        }
    }

}
