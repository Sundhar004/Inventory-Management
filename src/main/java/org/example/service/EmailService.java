package org.example.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.example.dao.ProductDAOImpl;
import org.example.model.Product;
import org.example.util.CSVHelper;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class EmailService {

    private static final String FROM_EMAIL = System.getenv("MAIL_USER");
    private static final String APP_PASSWORD = System.getenv("MAIL_PASS");
    private static final String CSV_PATH = "product_report.csv";

    static {
        validateCredentials();
    }

    private static void validateCredentials() {
        if (isNullOrEmpty(FROM_EMAIL) || isNullOrEmpty(APP_PASSWORD)) {
            throw new IllegalStateException("❌ MAIL_USER or MAIL_PASS environment variable is not set!");
        }
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    // ✅ Sends the actual report from the database
    public static void sendProductReport(String toEmail, String subject, String body) {
        try {
            // 1️⃣ Fetch products from DB
            ProductDAOImpl productDAO = new ProductDAOImpl();
            List<Product> products = productDAO.getAllProducts();

            if (products.isEmpty()) {
                System.err.println("⚠️ No products found in the database!");
                return;
            }

            // 2️⃣ Save products to CSV
            CSVHelper.saveProducts(products, CSV_PATH);
//            System.out.println("📄 Product report saved to: " + CSV_PATH);

            // 3️⃣ Prepare and send the email
            Session session = createEmailSession();
            Message message = composeMessage(session, toEmail, subject, body, CSV_PATH);

            Transport.send(message);
//            System.out.println("✅ Product report sent successfully to " + toEmail);

        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Email sending failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Session createEmailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });
    }

    public static void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        // Validate credentials (existing static initializer will throw if missing)
        Session session = createEmailSession();

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);

        // Use plain text body
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body, "utf-8", "html"); // HTML-safe; change to "text/plain" if you want plain text

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        message.setContent(multipart);

        Transport.send(message);
    }


    private static Message composeMessage(Session session, String toEmail, String subject, String body, String attachmentPath)
            throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);

        Multipart multipart = new MimeMultipart();

        // Email body
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body);
        multipart.addBodyPart(textPart);

        // Attachment
        File file = new File(attachmentPath);
        if (file.exists()) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            try {
                attachmentPart.attachFile(file);
                attachmentPart.setFileName(file.getName());
                multipart.addBodyPart(attachmentPart);
            } catch (Exception e) {
                System.err.println("⚠️ Failed to attach CSV: " + e.getMessage());
            }
        } else {
            System.err.println("⚠️ CSV file not found: " + attachmentPath);
        }

        message.setContent(multipart);
        return message;
    }

    // ✅ For standalone testing
    public static void main(String[] args) {
        sendProductReport(
                FROM_EMAIL,  // you can change to recipient email
                "📦 Inventory Product Report",
                "Attached is the latest inventory report from the database."
        );
    }
}
