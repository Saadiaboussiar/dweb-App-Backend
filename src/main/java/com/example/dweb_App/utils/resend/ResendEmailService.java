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

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Email sent successfully to: " + toEmail);
                System.out.println("API Response: " + response.getBody()); // Log the response
                return true;
            } else {
                // Log if we get any other unexpected status code
                System.err.println("Failed to send email. Received status: " + response.getStatusCode());
                System.err.println("Response body: " + response.getBody());
                return false;
            }


        } catch (Exception e) {
            System.err.println("Failed to send email to: " + toEmail);
            System.err.println("Exception: " + e.getClass().getSimpleName());

            // This will print the full error response from Resend if available
            if (e instanceof org.springframework.web.client.HttpClientErrorException httpClientErrorException) {
                System.err.println("Status Code: " + httpClientErrorException.getStatusCode());
                System.err.println("Response Body: " + httpClientErrorException.getResponseBodyAsString()); // <-- This is GOLD
            } else {
                System.err.println("Error Message: " + e.getMessage());
            }
            return false;

        }
    }

    public void sendWelcomeEmail(String toEmail, String username, String tempPassword) {
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <body>
                <h2>Bienvenue sur notre application, %s !</h2>
                <p>Votre compte a été créé avec succès.</p>
                <p><strong>E-mail :</strong> %s</p>
                <p><strong>Mot de passe temporaire :</strong> %s</p>
                <p>Veuillez changer votre mot de passe après votre première connexion.</p>
                
            </body>
            </html>
            """.formatted(username,toEmail,tempPassword);

        boolean success = sendEmail(toEmail, "Bienvenue sur notre application", htmlContent);

        if (success) {
            System.out.println("Welcome email sent to: " + toEmail);
        } else {
            System.out.println("Failed to send welcome email to: " + toEmail);
        }
    }

    public void sendVerifyCodeEmail(String toEmail,String username ,String verifyCode) {
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <body>
                <h2>Application des interventions de Dweb Technology </h2>
                <p>Vous avez oublié votre mmode passe, %s!</p>
                <p><strong>Votre code:</strong> %s</p>
                </body>
            </html>
            """.formatted(username,verifyCode);

        boolean success = sendEmail(toEmail, "Code de vérification", htmlContent);

        if (success) {
            System.out.println("Verify Code sent to: " + toEmail);
        } else {
            System.out.println("Failed to send verify code to: " + toEmail);
        }
    }

}