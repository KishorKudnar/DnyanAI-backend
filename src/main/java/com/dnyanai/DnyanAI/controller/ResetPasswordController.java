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

    String htmlForm = """
        <html>
          <head>
            <title>Reset Password - DnyanAI</title>
            <style>
              body {
                font-family: 'Poppins', Arial, sans-serif;
                text-align: center;
                background: linear-gradient(145deg, #EAE8FD, #F8F6FF);
                padding: 40px;
              }
              .container {
                background: #fff;
                max-width: 420px;
                margin: 40px auto;
                padding: 30px;
                border-radius: 18px;
                box-shadow: 0 6px 25px rgba(106,90,224,0.25);
              }
              h2 {
                color: #6A5AE0;
                margin-bottom: 20px;
                font-weight: 600;
              }
              .password-container {
                position: relative;
                display: flex;
                justify-content: center;
                align-items: center;
              }
              input {
                width: 85%%;
                padding: 12px;
                margin: 10px 0;
                border-radius: 10px;
                border: 1px solid #ccc;
                font-size: 14px;
                outline: none;
                transition: 0.3s;
              }
              input:focus {
                border-color: #6A5AE0;
                box-shadow: 0 0 6px rgba(106,90,224,0.3);
              }
              .toggle-btn {
                position: absolute;
                right: 40px;
                top: 50%%;
                transform: translateY(-50%%);
                cursor: pointer;
                width: 22px;
                height: 22px;
              }
              .strength {
                width: 85%%;
                height: 8px;
                border-radius: 6px;
                margin: 5px auto 15px auto;
                background-color: #eee;
                overflow: hidden;
              }
              .strength-bar {
                height: 100%%;
                width: 0%%;
                transition: width 0.3s ease;
              }
              .strength-text {
                font-size: 13px;
                color: #555;
                margin-bottom: 10px;
              }
              button {
                background-color: #6A5AE0;
                color: white;
                border: none;
                padding: 12px 40px;
                border-radius: 8px;
                font-size: 16px;
                cursor: pointer;
                margin-top: 15px;
                transition: all 0.3s ease;
              }
              button:hover {
                background-color: #5A4ED8;
                transform: translateY(-2px);
              }
              .footer {
                margin-top: 20px;
                color: #777;
                font-size: 12px;
              }
              img {
                width: 90px;
                height: auto;
                margin-bottom: 15px;
                border-radius: 50%%;
                box-shadow: 0 0 10px rgba(106,90,224,0.2);
              }
            </style>

            <script>
              function togglePassword(id, iconId) {
                const input = document.getElementById(id);
                const icon = document.getElementById(iconId);

                if (input.type === 'password') {
                  input.type = 'text';
                  icon.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" fill="none" stroke="#6A5AE0" stroke-width="2" viewBox="0 0 24 24"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8S1 12 1 12z"/><circle cx="12" cy="12" r="3"/></svg>';
                } else {
                  input.type = 'password';
                  icon.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" fill="none" stroke="#6A5AE0" stroke-width="2" viewBox="0 0 24 24"><path d="M17.94 17.94A10.93 10.93 0 0 1 12 20C5 20 1 12 1 12s4-8 11-8c2.12 0 4.08.74 5.74 1.94M1 1l22 22"/></svg>';
                }
              }

              function checkStrength() {
                const password = document.getElementById('newPassword').value;
                const bar = document.getElementById('strength-bar');
                const text = document.getElementById('strength-text');
                let strength = 0;

                if (password.length > 5) strength += 1;
                if (password.length > 8) strength += 1;
                if (/[A-Z]/.test(password)) strength += 1;
                if (/[0-9]/.test(password)) strength += 1;
                if (/[^A-Za-z0-9]/.test(password)) strength += 1;

                switch (strength) {
                  case 0:
                    bar.style.width = '0%%';
                    text.innerText = '';
                    break;
                  case 1:
                    bar.style.width = '20%%';
                    bar.style.backgroundColor = '#FF4B4B';
                    text.innerText = 'Weak';
                    text.style.color = '#FF4B4B';
                    break;
                  case 2:
                  case 3:
                    bar.style.width = '60%%';
                    bar.style.backgroundColor = '#FFC300';
                    text.innerText = 'Medium';
                    text.style.color = '#FFC300';
                    break;
                  case 4:
                  case 5:
                    bar.style.width = '100%%';
                    bar.style.backgroundColor = '#00C851';
                    text.innerText = 'Strong';
                    text.style.color = '#00C851';
                    break;
                }
              }
            </script>
          </head>

          <body>
            <div class='container'>
              <img src="/images/Logo.png" alt="DnyanAI Logo"/>
              <h2>Reset Password</h2>
              <p style='color:#555;'>for <b>%s</b></p>

              <form method='POST' action='/reset-password'>
                <input type='hidden' name='email' value='%s'/>

                <div class='password-container'>
                  <input type='password' id='newPassword' name='newPassword'
                    placeholder='Enter New Password' required onkeyup='checkStrength()'/>

                  <div id='toggleNew' class='toggle-btn' onclick="togglePassword('newPassword','toggleNew')">
                    <svg xmlns='http://www.w3.org/2000/svg' fill='none' stroke='#6A5AE0' stroke-width='2'
                      viewBox='0 0 24 24'><path d='M1 12s4-8 11-8 11 8 11 8-4 8-11 8S1 12 1 12z'/>
                      <circle cx='12' cy='12' r='3'/></svg>
                  </div>
                </div>

                <div class='strength'>
                  <div id='strength-bar' class='strength-bar'></div>
                </div>
                <div id='strength-text' class='strength-text'></div>

                <div class='password-container'>
                  <input type='password' id='confirmPassword' name='confirmPassword'
                    placeholder='Confirm Password' required/>

                  <div id='toggleConfirm' class='toggle-btn'
                       onclick="togglePassword('confirmPassword','toggleConfirm')">
                    <svg xmlns='http://www.w3.org/2000/svg' fill='none' stroke='#6A5AE0' stroke-width='2'
                      viewBox='0 0 24 24'><path d='M1 12s4-8 11-8 11 8 11 8-4 8-11 8S1 12 1 12z'/>
                      <circle cx='12' cy='12' r='3'/></svg>
                  </div>
                </div>

                <button type='submit'>Reset Password</button>
              </form>

              <div class='footer'>© 2025 DnyanAI Team</div>
            </div>
          </body>
        </html>
        """
        .formatted(email, email);

    return ResponseEntity.ok()
        .header("Content-Type", "text/html")
        .body(htmlForm);
  }

  @PostMapping("/reset-password")
  public ResponseEntity<String> handlePasswordReset(@RequestParam String email,
      @RequestParam String newPassword,
      @RequestParam String confirmPassword) {

    try {
      if (!newPassword.equals(confirmPassword)) {
        return ResponseEntity.badRequest().body("""
                <html><body style='text-align:center; font-family:Arial; color:red;'>
                <h3>❌ Passwords do not match!</h3>
                <p>Please go back and try again.</p>
                </body></html>
            """);
      }

      Optional<User> userOpt = userService.getUserByEmail(email);
      if (userOpt.isEmpty()) {
        return ResponseEntity.status(404).body("""
                <html><body style='text-align:center; font-family:Arial; color:red;'>
                <h3>⚠️ User not found!</h3>
                <p>This email is not registered with DnyanAI.</p>
                </body></html>
            """);
      }

      User user = userOpt.get();
      user.setPassword(encoder.encode(newPassword));
      userService.saveUser(user);

      return ResponseEntity.ok("""
              <html>
                <body style='font-family:Arial; text-align:center; padding:40px; background-color:#f9f9ff;'>
                  <h2 style='color:green;'>✅ Password Reset Successful!</h2>
                  <p>Your password is successfully reset. You can now open the app and log in.</p>

                </body>
              </html>
          """);

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.internalServerError().body("""
              <html><body style='text-align:center; font-family:Arial; color:red;'>
              <h3>❌ Error resetting password:</h3>
              <p>%s</p>
              </body></html>
          """.formatted(e.getMessage()));
    }
  }
}
