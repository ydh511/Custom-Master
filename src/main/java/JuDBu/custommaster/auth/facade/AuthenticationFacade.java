package JuDBu.custommaster.auth.facade;


import JuDBu.custommaster.domain.entity.account.Account;
import JuDBu.custommaster.domain.dto.account.CustomAccountDetails;
import JuDBu.custommaster.domain.repo.account.AccountRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFacade {
    private final AccountRepo accountRepo;

    public Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public Account getAccount() {
        Authentication authentication = getAuth();
        log.info("auth = {}", authentication);
        if (authentication.getPrincipal() instanceof CustomAccountDetails) {
            CustomAccountDetails customAccountDetails = (CustomAccountDetails) authentication.getPrincipal();
            return accountRepo.findByUsername(customAccountDetails.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        }else {
            String name = authentication.getName();
            log.info("name = {}", name);
            Account a =  accountRepo.findByEmail(name)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));


            log.info("a = {}", a);
            return a;
        }
    }
}
