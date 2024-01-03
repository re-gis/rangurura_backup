package com.backend.rangurura.serviceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backend.rangurura.exceptions.MessageSendingException;
import com.backend.rangurura.Services.OtpService;
import com.nexmo.client.NexmoClient;
import com.nexmo.client.sms.MessageStatus;
import com.nexmo.client.sms.SmsSubmissionResponse;
import com.nexmo.client.sms.messages.TextMessage;
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
    public void sendMessage(String toPhoneNumber, String messageBody) {
       String apiKey = "0c822d84";
        String apiSecret = "KfMnOEvRWn5CAwpD";
        String fromNumber = "+250790539434"; // Nexmo virtual number

        NexmoClient client = new NexmoClient.Builder()
            .apiKey(apiKey)
            .apiSecret(apiSecret)
            .build();

        TextMessage message = new TextMessage(
            fromNumber,
            toPhoneNumber,
            messageBody
        );

        try {
            SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);
            if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
                System.out.println(response);
                System.out.println("SMS sent successfully!");
            } else {
                System.err.println("Error sending SMS: " + response.getMessages().get(0).getErrorText());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
