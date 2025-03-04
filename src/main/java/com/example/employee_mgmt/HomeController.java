package com.example.employee_mgmt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // 🔹 Change from @RestController to @Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String home(@AuthenticationPrincipal OidcUser user) {
        if (user != null) {
            return "redirect:/employee"; // 🔹 Redirect to dashboard if logged in
        }
        return "redirect:/oauth2/authorization/keycloak"; // 🔹 Redirect to login page if not logged in
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal OidcUser user) {
        model.addAttribute("user", user);
        return "employee"; // 🔹 Render the dashboard page
    }
}

