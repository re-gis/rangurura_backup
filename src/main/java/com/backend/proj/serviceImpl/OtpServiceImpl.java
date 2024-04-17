package com.backend.proj.serviceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backend.proj.Services.OtpService;
import com.backend.proj.exceptions.MessageSendingException;
import com.nexmo.client.NexmoClient;
import com.nexmo.client.sms.MessageStatus;
import com.nexmo.client.sms.SmsSubmissionResponse;
import com.nexmo.client.sms.messages.TextMessage;
import java.security.SecureRandom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OtpServiceImpl implements OtpService {
    @Value("${nexmo.api.key}")
    private String apiKey;

    @Value("${nexmo.secret.key}")
    private String apiSecret;

    @Value("${nexmo.phone.number}")
    private String fromNumber;

    @Override
    public void sendMessage(String toPhoneNumber, String messageBody) {

        NexmoClient client = new NexmoClient.Builder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .build();

        TextMessage message = new TextMessage(
                fromNumber,
                toPhoneNumber,
                messageBody);

        try {
            SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);
            if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
                System.out.println("SMS sent successfully!");
            } else {
                System.err.println("Error sending SMS: " + response.getMessages().get(0).getErrorText());
                // Handle the case where message is rejected or failed to send
                // For example, you can throw a custom exception or log the error
                throw new MessageSendingException("Error sending SMS: " + response.getMessages().get(0).getErrorText());
            }
        } catch (MessageSendingException e) {
            // Handle the custom exception thrown when message sending fails
            e.printStackTrace(); // Log or handle the exception accordingly
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace(); // Log or handle the exception accordingly
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
