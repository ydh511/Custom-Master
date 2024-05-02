package JuDBu.custommaster.domain.controller.account;

import JuDBu.custommaster.auth.facade.AuthenticationFacade;
import JuDBu.custommaster.domain.dto.account.CustomAccountDetails;
import JuDBu.custommaster.domain.entity.account.Authority;
import JuDBu.custommaster.domain.service.account.AccountService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    @GetMapping("/header")
    public String header(){
        return "account/header-test";
    }

    @GetMapping("/register")
    public String registerForm(
            HttpServletRequest request,
            Model model
    ) {
        return "account/register";
    }

    @GetMapping("/business-register")
    public String businessRegisterForm(
            HttpServletRequest request,
            Model model
    ) {
        return "account/business-register";
    }

    @GetMapping("/login")
    public String loginForm(
            HttpServletRequest request,
            Model model
    ) {
        String referer = request.getHeader("REFERER");
        log.info("referer:{}",referer);
        if(referer.contains("logout")) {
            referer = "/shop";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(referer).append("\"");
        referer = sb.toString();
        log.info("referer:{}",referer);
        model.addAttribute("referer",referer);
        return "account/login-form";
    }

    @GetMapping("/oauth")
    public String oauth() {
        return "account/oauth-login";
    }

    @GetMapping("/profile")
    public String profileForm() { return  "account/my-profile"; }

    @GetMapping("/update")
    public String updateForm() { return  "account/update"; }

    @GetMapping("/logout")
    public String logoutForm() {
        return "account/logout";
    }

    @GetMapping("/mail-auth")
    public String mailAuthForm(){ return "account/mail-auth";}
}