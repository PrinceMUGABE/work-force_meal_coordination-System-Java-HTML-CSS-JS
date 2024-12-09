package utils;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {

    public static void sendEmail(String recipient, String subject, String message) {
        if (recipient == null || recipient.isEmpty()) {
            throw new IllegalArgumentException("Recipient email is missing.");
        }
        // It's safer to use environment variables instead of hardcoding credentials
        final String senderEmail = System.getenv("princemugabe567@gmail.com.com");  // Replace with your environment variable for email
//        final String senderPassword = System.getenv("ebmz vdbu ttfo hwlw");  // Replace with your environment variable for password
        final String senderPassword = System.getenv("07288766");
        
        // Ensure these variables are set in your environment before running the code
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(senderEmail));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);

            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error sending email", e);
        }
    }
}

//        final String senderEmail = "princemugabe567@gmail.com.com"; // Replace with your email
//        final String senderPassword = "ebmz vdbu ttfo hwlw";       // Replace with your password
//

