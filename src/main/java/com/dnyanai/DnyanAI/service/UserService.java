package com.dnyanai.DnyanAI.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.dnyanai.DnyanAI.model.User;
import com.dnyanai.DnyanAI.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private BCryptPasswordEncoder encoder;

    private static final String FROM_EMAIL = "9c75ef001@smtp-brevo.com";

    public String registerUser(String name, String email, String password, String className, String stream,
            String board) {
        if (userRepository.findByEmail(email).isPresent()) {
            return "Email already registered!";
        }

        String hashedPassword = encoder.encode(password);

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setClassName(className != null ? className : "11th");
        user.setStream(stream != null ? stream : "Science");
        user.setBoard(board != null ? board : "State Board");

        userRepository.save(user);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(email);
            message.setSubject("Welcome to DnyanAI!");
            message.setText("Hello " + name + ",\n\nWelcome to DnyanAI!\n\nBest Regards,\nDnyanAI Team");
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return "Registration successful, but failed to send email!";
        }

        return "Registration successful! A welcome email has been sent.";
    }

    public Optional<User> loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && encoder.matches(password, userOpt.get().getPassword())) {
            return userOpt;
        }
        return Optional.empty();
    }

    public String updateUserProfile(String email, String name, String className, String stream, String board,
            String profilePic) {
        System.out.println("🟣 Updating full profile for email: " + email);

        Optional<User> optionalUser = userRepository.findByEmail(email.trim());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (name != null && !name.isEmpty()) {
                user.setName(name);
            }
            if (className != null && !className.isEmpty()) {
                user.setClassName(className);
            }
            if (stream != null && !stream.isEmpty()) {
                user.setStream(stream);
            }
            if (board != null && !board.isEmpty()) {
                user.setBoard(board);
            }
            if (profilePic != null) {
                user.setProfilePic(profilePic);
            }

            userRepository.save(user);
            return "Profile updated successfully!";
        } else {
            return "User not found!";
        }
    }

    public String sendResetPasswordEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "Email not registered!";
        }

        String resetLink = "https://dnyanai-backend-1.onrender.com/reset-password?email=" + email;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@dnyanai.com"); // Use any sender name (Brevo allows custom)
            message.setTo(email);
            message.setSubject("DnyanAI - Password Reset");
            message.setText("Hello " + userOpt.get().getName() + ",\n\n"
                    + "Click the link below to reset your password:\n"
                    + resetLink + "\n\n"
                    + "If you did not request this, please ignore this email.\n\n"
                    + "Regards,\nDnyanAI Team");

            mailSender.send(message);

            return "Password reset link sent to your registered email!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to send email. " + e.getMessage();
        }
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerGoogleUser(String name, String email, String picture) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (name != null && !name.equals(user.getName())) {
                user.setName(name);
            }
            if (picture != null && !picture.equals(user.getProfilePic())) {
                user.setProfilePic(picture);
            }
            userRepository.save(user);
            return user;
        } else {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(encoder.encode("google_user"));
            user.setProfilePic(picture);
            user.setClassName("11th");
            user.setStream("Science");
            user.setBoard("State Board");

            userRepository.save(user);

            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email);
                message.setSubject("Welcome to DnyanAI via Google Sign-In!");
                message.setText("Hello " + name + ",\n\nWelcome to DnyanAI via Google!\n\nBest Regards,\nDnyanAI Team");
                mailSender.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return user;
        }
    }

}
