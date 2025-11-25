package com.dnyanai.DnyanAI.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnyanai.DnyanAI.model.User;
import com.dnyanai.DnyanAI.security.JwtUtil;
import com.dnyanai.DnyanAI.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> req) {
        Map<String, Object> response = new HashMap<>();
        try {
            String name = req.get("name");
            String email = req.get("email");
            String password = req.get("password");
            String className = req.get("className");
            String stream = req.get("stream");
            String board = req.get("board");

            String message = userService.registerUser(name, email, password, className, stream, board);
            boolean success = message.startsWith("Registration successful");

            response.put("success", success);
            response.put("message", message);

            if (success) {
                String token = jwtUtil.generateToken(email);
                Optional<User> userOpt = userService.getUserByEmail(email);

                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    response.put("token", token);
                    response.put("email", user.getEmail());
                    response.put("name", user.getName());
                    response.put("className", user.getClassName());
                    response.put("stream", user.getStream());
                    response.put("board", user.getBoard());
                    response.put("profilePic", user.getProfilePic());
                }
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> req) {
        try {
            String email = req.get("email");
            String password = req.get("password");

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            User user = userService.getUserByEmail(email).get();
            String token = jwtUtil.generateToken(email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful!");
            response.put("token", token);
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("className", user.getClassName());
            response.put("stream", user.getStream());
            response.put("board", user.getBoard());
            response.put("profilePic", user.getProfilePic());

            return response;

        } catch (BadCredentialsException e) {
            return Map.of("success", false, "message", "Invalid credentials!");
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("success", false, "message", "Login error: " + e.getMessage());
        }
    }

    @PostMapping("/google-login")
    public ResponseEntity<Map<String, Object>> googleLogin(@RequestBody Map<String, String> request) {
        System.out.println("🟢 Google login API hit");

        Map<String, Object> response = new HashMap<>();
        try {
            String idTokenString = request.get("idToken");
            if (idTokenString == null || idTokenString.isEmpty()) {
                response.put("success", false);
                response.put("message", "Missing Google ID token");
                return ResponseEntity.badRequest().body(response);
            }

            JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            NetHttpTransport transport = new NetHttpTransport();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList(
                            "415280119409-j3aekn3neqrhettgs0jdpr88pj1nua19.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                response.put("success", false);
                response.put("message", "Invalid Google token");
                return ResponseEntity.badRequest().body(response);
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");

            Optional<User> userOpt = userService.getUserByEmail(email);
            User user = userOpt.orElseGet(() -> userService.registerGoogleUser(name, email, picture));

            String token = jwtUtil.generateToken(email);

            response.put("success", true);
            response.put("message", "Google Login Successful!");
            response.put("token", token);
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            response.put("profilePic", user.getProfilePic());
            response.put("className", user.getClassName());
            response.put("stream", user.getStream());
            response.put("board", user.getBoard());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Google login failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/get-profile")
    public ResponseEntity<Map<String, Object>> getProfile(@RequestBody Map<String, String> req) {
        try {
            String email = req.get("email");
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email is required"));
            }

            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("name", user.getName());
                response.put("email", user.getEmail());
                response.put("className", user.getClassName());
                response.put("stream", user.getStream());
                response.put("board", user.getBoard());
                response.put("profilePic", user.getProfilePic());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "User not found!"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Internal Server Error: " + e.getMessage()));
        }
    }

    @PostMapping("/update-profile")
    public Map<String, Object> updateProfile(@RequestBody Map<String, String> req) {
        try {
            String email = req.get("email");
            String name = req.get("name");
            String className = req.get("className");
            String stream = req.get("stream");
            String board = req.get("board");
            String profilePic = req.get("profilePic");
            String message = userService.updateUserProfile(email, name, className, stream, board, profilePic);
            boolean success = message.startsWith("Profile updated successfully");
            return Map.of("success", success, "message", message);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Profile update failed: " + e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public Map<String, Object> forgotPassword(@RequestBody Map<String, String> req) {
        try {
            String email = req.get("email");
            String message = userService.sendResetPasswordEmail(email);
            boolean success = message.contains("sent");
            return Map.of("success", success, "message", message);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error: " + e.getMessage());
        }
    }
}
