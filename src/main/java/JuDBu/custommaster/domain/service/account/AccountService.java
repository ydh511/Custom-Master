package JuDBu.custommaster.domain.service.account;


import JuDBu.custommaster.domain.dto.account.AccountDto;
import JuDBu.custommaster.domain.entity.account.Account;
import JuDBu.custommaster.domain.repo.account.AccountRepo;
import JuDBu.custommaster.auth.facade.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepo accountRepo;
    private final AuthenticationFacade authFacade;
    private final PasswordEncoder passwordEncoder;

    public AccountDto readOne(Long id){
        Account account = accountRepo.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND));

        return AccountDto.fromEntity(account);
    }

    public AccountDto readProfile(){
        Account account = authFacade.getAccount();
        return  AccountDto.fromEntity(account);
    }

    public AccountDto delete(Long id){
        Account account = accountRepo.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND));

        accountRepo.deleteById(id);
        return AccountDto.fromEntity(account);
    }

    public List<AccountDto> readAll(){
        List<Account> accounts = accountRepo.findAll();
        if (accounts.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        List<AccountDto> dtos = new ArrayList<>();

        for (Account account : accounts){
            dtos.add(AccountDto.fromEntity(account));
        }
        return dtos;
    }
    // 사용자 정보 수정
    public AccountDto updateAccount(AccountDto dto) {
        Account target = authFacade.getAccount();

        // 비밀번호 확인
        if (!passwordEncoder.matches(dto.getPasswordCheck(), target.getPassword())) {
            log.error("비밀번호가 일치하지 않습니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        target.updateInfo(dto.getPassword(), dto.getEmail());
        return AccountDto.fromEntity(accountRepo.save(target));
    }

}
