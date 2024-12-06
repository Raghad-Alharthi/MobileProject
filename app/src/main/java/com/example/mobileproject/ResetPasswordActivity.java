package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnSendCode;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etEmail = findViewById(R.id.etEmail);
        btnSendCode = findViewById(R.id.btnSendResetEmail);

        btnSendCode.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(ResetPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else if (!isValidEmail(email)) {
                Toast.makeText(ResetPasswordActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            } else {
                verificationCode = generateVerificationCode();
                sendEmail(email, verificationCode);
            }
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private String generateVerificationCode() {
        return String.valueOf((int) ((Math.random() * 900000) + 100000)); // Generates a 6-digit code
    }

    private void sendEmail(String recipientEmail, String code) {
        // SMTP configuration
        final String username = "apikey"; // Use "apikey" as the username
        final String password = "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"; // Your SendGrid API key

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.sendgrid.net");
        props.put("mail.smtp.port", "587");

        // Create a session with authentication
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a MimeMessage object
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("applicationprogrammingproject@hotmail.com")); // Replace with your verified email
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Reset Password Verification Code");
            message.setText("Your verification code is: " + code);

            // Send the email
            new Thread(() -> {
                try {
                    Transport.send(message);

                    // Email sent successfully, navigate to VerifyCodeActivity
                    runOnUiThread(() -> {
                        Toast.makeText(ResetPasswordActivity.this, "Email sent successfully!", Toast.LENGTH_SHORT).show();

                        // Navigate to the next activity
                        Intent intent = new Intent(ResetPasswordActivity.this, VerifyCodeActivity.class);
                        intent.putExtra("EMAIL", recipientEmail); // Pass the email to the next activity
                        intent.putExtra("CODE", code); // Pass the verification code
                        startActivity(intent);
                    });

                } catch (MessagingException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(ResetPasswordActivity.this, "Failed to send email: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).start();

        } catch (MessagingException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}