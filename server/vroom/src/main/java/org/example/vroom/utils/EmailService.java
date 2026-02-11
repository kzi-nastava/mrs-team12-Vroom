package org.example.vroom.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.example.vroom.DTOs.RideDTO;
import org.example.vroom.DTOs.responses.ride.AcceptedRideDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class EmailService {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendActivationMail(String to, String id) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("teodor.perun@gmail.com");
        helper.setTo(to);
        helper.setSubject("Verify your account");

        String verificationUrl = "http://localhost:8080/api/auth/activate-account/"+id;
        ClassPathResource resource = new ClassPathResource("templates/account-activation.html");
        String htmlContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8)
                .replace("{{URL}}", verificationUrl);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @Async
    public void sendRideAcceptedMail(String to, AcceptedRideDTO ride) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("teodor.perun@gmail.com");
        helper.setTo(to);
        helper.setSubject("Ride accepted");
        ClassPathResource resource = new ClassPathResource("templates/accepted-ride.html");
        String htmlContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8)
                .replace("{{startAddress}}", ride.getStartAddress())
                .replace("{{endAddress}}", ride.getEndAddress())
                .replace("{{driverName}}", ride.getDriverName())
                .replace("{{vehicleInfo}}", ride.getVehicleInfo())
                .replace("{{licensePlate}}", ride.getLicensePlate());
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @Async
    public void sendRideStartedMail(String to, String rideID) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("teodor.perun@gmail.com");
        helper.setTo(to);
        helper.setSubject("Ride started");
        String url = "http://localhost:4200/ride-duration?rideID="+rideID;

        ClassPathResource resource = new ClassPathResource("templates/started-ride.html");
        String htmlContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8)
                .replace("{{URL}}", url);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @Async
    public void sendTokenMail(String to, String code) throws MessagingException, IOException{
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("teodor.perun@gmail.com");
        helper.setTo(to);
        helper.setSubject("Reset Password Code");

        ClassPathResource resource = new ClassPathResource("templates/reset-password-code.html");
        String htmlContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8)
                .replace("{{CODE}}", code);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @Async
    public void sendRideEndMail(String to) throws MessagingException, IOException{
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("teodor.perun@gmail.com");
        helper.setTo(to);
        helper.setSubject("Ride Finished");
        helper.setText("Thank you for riding with Vroom! Please leave a review in our app :) ", false);
        mailSender.send(message);
    }

    @Async
    public void sendDriverActivationMail(String to, String driverId) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("teodor.perun@gmail.com");
        helper.setTo(to);
        helper.setSubject("Aktivacija vozačkog naloga");

        String activationUrl = "http://localhost:4200/driver/set-password/" + driverId;

        ClassPathResource resource = new ClassPathResource("templates/driver-activation.html");
        String htmlContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8)
                .replace("{{URL}}", activationUrl);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

}
