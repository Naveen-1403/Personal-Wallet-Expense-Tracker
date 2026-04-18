package com.naveen.expensetracker.controller;

import com.naveen.expensetracker.model.User;
import com.naveen.expensetracker.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // 1. Show Registration Page
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // 2. Save New User details to Database
    @PostMapping("/registerUser")
    public String registerUser(@ModelAttribute("user") User user) {
        userRepository.save(user);
        return "redirect:/login?success"; // Save aanathum login page-ku anuppidum
    }

    // 3. Show Login Page
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // 4. Verify Login Details
    @PostMapping("/loginUser")
    public String loginUser(@RequestParam("email") String email, 
                            @RequestParam("password") String password, 
                            HttpSession session) {
        
        // Database-la intha email & password irukka nu thedurom
        User user = userRepository.findByEmailAndPassword(email, password);
        
        if (user != null) {
            // Login Success: User-oda detail-a "Session"-la save pandrom
            session.setAttribute("loggedInUser", user);
            return "redirect:/"; // Namma Dashboard-ku pogum
        } else {
            // Login Fail: Thappa iruntha thirumba login page-kuke error-oda anuppuvom
            return "redirect:/login?error";
        }
    }

    // 5. Logout feature
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // User-oda session-a azhichiduvom
        return "redirect:/login?logout";
    }
}