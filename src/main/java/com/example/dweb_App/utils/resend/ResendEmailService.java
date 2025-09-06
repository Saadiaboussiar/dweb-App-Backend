package com.example.dweb_App.utils.resend;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class ResendEmailService {

    @Value("${resend.api-key}")
    private String apiKey;

    @Value("${resend.from-email}")  // Injected from properties
    private String fromEmail;

    private static final String RESEND_API_URL = "https://api.resend.com/emails";
    private final RestTemplate restTemplate = new RestTemplate();

    public boolean sendEmail(String toEmail, String subject, String htmlContent) {
        try {
            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("from", fromEmail);
            requestBody.put("to", new String[]{toEmail});
            requestBody.put("subject", subject);
            requestBody.put("html", htmlContent);

            // Create HTTP entity
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Send request
            ResponseEntity<String> response = restTemplate.exchange(
                    RESEND_API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return response.getStatusCode() == HttpStatus.OK;

        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            return false;
        }
    }

    public void sendWelcomeEmail(String toEmail, String username, String tempPassword) {
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <body>
                <h2>Welcome to Our App, %s!</h2>
                <p>Your account has been created successfully.</p>
                <p><strong>email:</strong> %s</p>
                <p><strong>Temporary Password:</strong> %s</p>
                <p>Please change your password after first login.</p>
            </body>
            </html>
            """.formatted(username,toEmail,tempPassword);

        boolean success = sendEmail(toEmail, "Welcome to Our App", htmlContent);

        if (success) {
            System.out.println("Welcome email sent to: " + toEmail);
        } else {
            System.out.println("Failed to send welcome email to: " + toEmail);
        }
    }
}