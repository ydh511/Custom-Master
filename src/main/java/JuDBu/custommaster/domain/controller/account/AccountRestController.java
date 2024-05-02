package JuDBu.custommaster.domain.controller.account;

import JuDBu.custommaster.auth.facade.AuthenticationFacade;
import JuDBu.custommaster.auth.jwt.dto.JwtRequestDto;
import JuDBu.custommaster.domain.dto.account.MailCode;
import JuDBu.custommaster.domain.entity.account.Account;
import JuDBu.custommaster.domain.service.account.AccountService;
import JuDBu.custommaster.domain.dto.account.AccountDto;
import JuDBu.custommaster.domain.dto.account.CustomAccountDetails;
import JuDBu.custommaster.domain.entity.account.Authority;
import JuDBu.custommaster.auth.jwt.dto.JwtResponseDto;
import JuDBu.custommaster.domain.service.account.MailService;
import JuDBu.custommaster.domain.service.account.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountRestController {
    private final UserDetailsManager manager;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacade authFacade;
    private final MailService mailService;
    private final TokenService tokenService;

    @PostMapping("/business-register")
    public String businessRegister(
            @RequestBody
            AccountDto dto
    ){
        log.info("on register");
        log.info("register username: {}",dto.getUsername());
        log.info("password: {}",dto.getPassword());
        log.info("check: {}",dto.getPasswordCheck());
        if (dto.getPassword().equals(dto.getPasswordCheck())) {
            manager.createUser(CustomAccountDetails.builder()
                    .username(dto.getUsername())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .name(dto.getName())
                    .email(dto.getEmail())
                    .authority(Authority.ROLE_INACTIVE_USER)
                    .businessNumber(dto.getBusinessNumber())
                    .build());
        }
        log.info("register done");
        return "register done";
    }

    @PostMapping("/register")
    public String register(
            @RequestBody
            AccountDto dto
    ){
        log.info("on register");
        log.info("register username: {}",dto.getUsername());
        log.info("password: {}",dto.getPassword());
        log.info("check: {}",dto.getPasswordCheck());
        if (dto.getPassword().equals(dto.getPasswordCheck())) {
            manager.createUser(CustomAccountDetails.builder()
                    .username(dto.getUsername())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .name(dto.getName())
                    .email(dto.getEmail())
                    .authority(Authority.ROLE_INACTIVE_USER)
                    .build());
        }
        log.info("register done");
        return "register done";
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> logIn(
            @RequestBody
            JwtRequestDto dto,
            HttpServletResponse response
    ){
        UserDetails userDetails = manager.loadUserByUsername(dto.getUsername());
        if(!passwordEncoder.matches(dto.getPassword(), userDetails.getPassword())){
            log.info("비밀번호 오류");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        JwtResponseDto accessTokenDto = tokenService.issueAccess(userDetails);;
        JwtResponseDto refreshTokenDto = tokenService.issueRefresh(userDetails, accessTokenDto.getAccessToken());
        Cookie cookie = new Cookie("CMToken", refreshTokenDto.getRefreshToken());
        cookie.setMaxAge(24 * 60 * 60 * 2);
        cookie.setPath("/"); // 쿠키의 경로 설정
        cookie.setDomain("localhost");
        cookie.setSecure(false);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.ok().body(refreshTokenDto);
    }

    @GetMapping("/profile")
    public ResponseEntity<AccountDto> profile(){
        AccountDto account = accountService.readProfile();
        log.info("profile username: {}",account.getUsername());
        return ResponseEntity.ok().body(account);
    }

    @PostMapping("/logout")
    public void logout(){
    }

    @GetMapping("/read/{id}")
    public AccountDto readOne(
            @PathVariable("id")
            Long id
    ){
        return accountService.readOne(id);
    }

    @PostMapping("/update")
    public String update(
            @RequestBody
            AccountDto userDto
    ){
        AccountDto dto = AccountDto.builder()
                .password(passwordEncoder.encode(userDto.getPassword()))
                .passwordCheck(userDto.getPasswordCheck())
                .email(userDto.getEmail())
                .build();
        accountService.updateAccount(dto);
        return "register done";
    }

    @PostMapping("/delete/{id}")
    public AccountDto deleteOne(
            @PathVariable("id")
            Long id
    ){
        return accountService.delete(id);
    }

    @PostMapping("/send-mail")
    public String sendMail(){
        Account account = authFacade.getAccount();
        mailService.send(account.getUsername(), account.getEmail());
        return "send mail";
    }

    @PostMapping("/check-mail")
    public String checkCode(
            @RequestBody
            MailCode dto
    ){
        Account account = authFacade.getAccount();

        if (!mailService.checkTimeLimit5L(account.getUsername())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if(!mailService.checkInfo(account.getUsername(), account.getEmail())){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if(!mailService.checkMailAuth(account.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!mailService.checkCode(account.getUsername(), dto.getCode())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return "mailauth done";
    }
    @PostMapping("authenticated")
    public void authenticated(){
        return;
    }
    @PostMapping("inactive")
    public void inactive(){
        return;
    }
    @PostMapping("active")
    public void active(){
        return;
    }
    @PostMapping("business")
    public void business(){
        return;
    }
    @PostMapping("admin")
    public void admin(){
        return;
    }
}
