package com.dnyanai.DnyanAI.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnyanai.DnyanAI.model.User;
import com.dnyanai.DnyanAI.service.UserService;

@RestController
@CrossOrigin(origins = "*")
public class ResetPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder encoder;


    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPasswordPage(@RequestParam String email) {

        String html = """
                <html>
                <head>
                    <title>Reset Password - DnyanAI</title>
                    <meta name="viewport" content="width=device-width, initial-scale=1"/>

                    <style>
                        body {
                            font-family: Poppins, Arial, sans-serif;
                            background: #F4F2FF;
                            padding: 20px;
                            text-align: center;
                        }
                        .container {
                            background: white;
                            max-width: 420px;
                            margin: 20px auto;
                            padding: 25px;
                            border-radius: 20px;
                            box-shadow: 0 0 18px rgba(106,90,224,0.25);
                        }
                        h2 { color: #6A5AE0; }
                        input {
                            width: 90%%;
                            padding: 12px;
                            margin: 10px 0;
                            border-radius: 12px;
                            border: 1px solid #bbb;
                            font-size: 15px;
                            outline: none;
                        }
                        button {
                            background: #6A5AE0;
                            color: white;
                            padding: 12px 32px;
                            border: none;
                            border-radius: 10px;
                            font-size: 16px;
                            cursor: pointer;
                            margin-top: 15px;
                        }
                        button:hover { background: #5948DA; }
                        .footer {
                            color: #888;
                            font-size: 12px;
                            margin-top: 20px;
                        }
                    </style>
                </head>

                <body>
                    <div class="container">
                        <img src="images/Logo.png" alt="DnyanAI Logo"
                             style="width:100px; border-radius:50%%; margin-bottom:15px"/>

                        <h2>Reset Password</h2>
                        <p>for <b>%s</b></p>

                        <form method="POST" action="/reset-password">
                            <input type="hidden" name="email" value="%s"/>

                            <input type="password" name="newPassword" placeholder="Enter New Password" required/>
                            <input type="password" name="confirmPassword" placeholder="Confirm Password" required/>

                            <button type="submit">Reset Password</button>
                        </form>

                        <div class="footer">© 2025 DnyanAI</div>
                    </div>
                </body>
                </html>
                """.formatted(email, email);

        return ResponseEntity.ok()
                .header("Content-Type", "text/html")
                .body(html);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> handlePasswordReset(
            @RequestParam String email,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {

        try {
            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest().body("""
                        <html><body style='text-align:center; font-family:Arial;'>
                        <h2 style='color:red;'>❌ Passwords do not match!</h2>
                        <p>Please go back and try again.</p>
                        </body></html>
                        """);
            }

            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body("""
                        <html><body style='text-align:center; font-family:Arial;'>
                        <h2 style='color:red;'>⚠️ User not found!</h2>
                        </body></html>
                        """);
            }

            User user = userOpt.get();
            user.setPassword(encoder.encode(newPassword));
            userService.saveUser(user);

            return ResponseEntity.ok("""
                    <html>
                    <body style='text-align:center; font-family:Arial; padding:40px;'>
                        <h2 style='color:green;'>✅ Password Reset Successful!</h2>
                        <p>You can now open the DnyanAI app and log in.</p>

                        <script>
                            setTimeout(() => { window.location.href = "https://dnyanai.com"; }, 3000);
                        </script>
                    </body>
                    </html>
                    """);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("""
                    <html><body style='text-align:center; font-family:Arial;'>
                    <h2 style='color:red;'>❌ Error:</h2>
                    <p>%s</p>
                    </body></html>
                    """.formatted(e.getMessage()));
        }
    }
}
