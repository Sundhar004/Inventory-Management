package org.example.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.example.dao.ProductDAOImpl;
import org.example.model.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class StockAlertService {

    private static final String FROM_EMAIL = System.getenv("MAIL_USER");
    private static final String APP_PASSWORD = System.getenv("MAIL_PASS");
    private static final ProductDAOImpl productDAO = new ProductDAOImpl();

    public static void sendLowStockAlerts(String recipientEmail) {
        if (isNullOrEmpty(FROM_EMAIL) || isNullOrEmpty(APP_PASSWORD)) {
            System.err.println("⚠️ Email credentials not set. Skipping alert.");
            return;
        }

        try {
            List<Product> products = productDAO.getAllProducts();
            StringBuilder alertBody = new StringBuilder();

            for (Product p : products) {
                if (p.getQuantity() <= p.getThreshold()) { // ✅ Correct comparison
                    alertBody.append("🔻 LOW STOCK: ")
                            .append(p.getName())
                            .append(" (Qty: ").append(p.getQuantity())
                            .append(", Threshold: ").append(p.getThreshold()).append(")\n");
                }
            }

            if (alertBody.length() == 0) {
                System.out.println("✅ All products are sufficiently stocked.");
            } else {
                sendEmail(recipientEmail, "📦 Inventory Low Stock Alert", alertBody.toString());
                System.out.println("📩 Low stock alert sent to " + recipientEmail);
                System.out.println(alertBody);
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to fetch products: " + e.getMessage());
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }

    private static void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
