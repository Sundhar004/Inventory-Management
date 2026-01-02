package org.example.service;

import jakarta.mail.MessagingException;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * OTPService handles generating, storing, and validating one-time passwords (OTPs)
 * for user email verification during registration.
 */
public class OTPService {

    // Store OTP and timestamp
    private static final Map<String, OTPEntry> otpStorage = new ConcurrentHashMap<>();

    // OTP validity (5 minutes)
    private static final long OTP_VALIDITY_MS = TimeUnit.MINUTES.toMillis(5);

    /**
     * Generates and sends a new OTP to the given email.
     * @param email The user's email address.
     * @return The generated OTP (for testing/logging only, not shown to user in production).
     */
    public static String generateOTP(String email) {
        // Generate random 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(1_000_000));

        // Store OTP and timestamp
        otpStorage.put(email, new OTPEntry(otp, System.currentTimeMillis()));

        // Prepare email content
        String subject = "🔐 Inventory System - Email Verification";
        String body = "Your verification OTP is: <b>" + otp + "</b><br><br>"
                + "It is valid for 5 minutes. Do not share it with anyone.";


        // Attempt to send the OTP
        try {
            EmailService.sendEmail(email, subject, body);
            System.out.println("📧 OTP sent successfully to " + email);
        } catch (MessagingException me) {
            System.err.println("❌ Failed to send OTP email: " + me.getMessage());
        } catch (IllegalStateException ise) {
            System.err.println("⚠️ Email service not configured: " + ise.getMessage());
        }

        return otp; // For testing or logging (not for user display)
    }

    /**
     * Validates the OTP entered by the user.
     * @param email The user's email address.
     * @param enteredOTP The OTP entered by the user.
     * @return True if valid, false otherwise.
     */
    public static boolean validateOTP(String email, String enteredOTP) {
        OTPEntry entry = otpStorage.get(email);

        if (entry == null) {
            System.out.println("⚠️ No OTP found for this email. Please request a new one.");
            return false;
        }

        long currentTime = System.currentTimeMillis();

        // Check expiry
        if (currentTime - entry.timestamp > OTP_VALIDITY_MS) {
            otpStorage.remove(email);
            System.out.println("⏰ OTP expired. Please request a new one.");
            return false;
        }

        // Validate OTP
        if (entry.otp.equals(enteredOTP)) {
            otpStorage.remove(email);
            System.out.println("✅ OTP verified successfully!");
            return true;
        } else {
            System.out.println("❌ Invalid OTP. Please try again.");
            return false;
        }
    }

    /**
     * Deletes expired OTPs periodically (optional utility).
     */
    public static void cleanupExpiredOTPs() {
        long now = System.currentTimeMillis();
        otpStorage.entrySet().removeIf(e -> now - e.getValue().timestamp > OTP_VALIDITY_MS);
    }

    // === INNER CLASS ===
    private static class OTPEntry {
        String otp;
        long timestamp;

        OTPEntry(String otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }
    }
}
