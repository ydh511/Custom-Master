package JuDBu.custommaster.domain.service.ord;

import JuDBu.custommaster.auth.facade.AuthenticationFacade;
import JuDBu.custommaster.domain.dto.account.AccountDto;
import JuDBu.custommaster.domain.dto.ord.OrdDto;
import JuDBu.custommaster.domain.dto.product.ProductDto;
import JuDBu.custommaster.domain.dto.shop.ShopReadDto;
import JuDBu.custommaster.domain.entity.Ord;
import JuDBu.custommaster.domain.entity.Product;
import JuDBu.custommaster.domain.entity.Shop;
import JuDBu.custommaster.domain.entity.account.Account;
import JuDBu.custommaster.domain.repo.OrdRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {
    private final AuthenticationFacade authFacade;
    private final OrdRepo ordRepo;

    // 사용자 정보 불러오기
    public AccountDto readOneAccount() {
        Account account = authFacade.getAccount();

        log.info("auth user: {}", authFacade.getAuth().getName());
        log.info("page username: {}", account.getUsername());

        // 토큰으로 접근 시도한 유저와, 페이지의 유저가 다른경우 예외
        if (!authFacade.getAuth().getName().equals(account.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return AccountDto.fromEntity(account);
    }

    public Page<OrdDto> readAllOrdByAccount(Pageable pageable) {
        // 접근자 확인
        Account account = authFacade.getAccount();
        log.info("auth: {}", authFacade.getAuth().getName());

        Page<Ord> ords = ordRepo.findByAccount(account, pageable)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND) {
                });

        return ords.map(OrdDto::fromEntity);
    }

    // 주문 리스트에서 Product name 불러오기
    public List<String> ordProductName() {
        Account account = authFacade.getAccount();
        log.info("auth: {}", authFacade.getAuth().getName());

        List<Ord> ords = ordRepo.findByAccount(account)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ords.stream()
                .map(Ord::getProduct)
                .map(Product::getName)
                .collect(Collectors.toList());
    }

    // 주문 리스트에서 ShopName 불러오기
    public List<String> getShopName() {
        Account account = authFacade.getAccount();
        log.info("auth: {}", authFacade.getAuth().getName());

        List<Ord> ordList = ordRepo.findByAccount(account)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ordList.stream()
                .map(Ord::getShop)
                .map(Shop::getName)
                .collect(Collectors.toList());
    }

    // Account에 있는 주문 상세 확인
    public OrdDto readDetails(Long ordId) {
        // 접근자 확인
        Account account = authFacade.getAccount();
        log.info("auth: {}", authFacade.getAuth().getName());

        // 계정에 속한 주문인지 확인
        Ord ord = ordRepo.findByAccountAndId(account, ordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return OrdDto.fromEntity(ord);
    }

    // 특정 주문에 대한 Product Name 불러오기
    public ProductDto getProductName(Long ordId) {
        // 접근자 확인
        Account account = authFacade.getAccount();
        log.info("auth: {}", authFacade.getAuth().getName());

        // 계정에 속한 주문인지 확인
        Ord ord = ordRepo.findByAccountAndId(account, ordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ProductDto.fromEntity(ord.getProduct());
    }

    // 특정 주문에 대한 Shop Name 불러오기
    public ShopReadDto getShopName(Long ordId) {
        // 접근자 확인
        Account account = authFacade.getAccount();
        log.info("auth: {}", authFacade.getAuth().getName());

        // 계정에 속한 주문인지 확인
        Ord ord = ordRepo.findByAccountAndId(account, ordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ShopReadDto.fromEntity(ord.getShop());
    }
}