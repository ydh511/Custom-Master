package JuDBu.custommaster.domain.service.ord.accept;

import JuDBu.custommaster.auth.facade.AuthenticationFacade;
import JuDBu.custommaster.domain.dto.account.AccountDto;
import JuDBu.custommaster.domain.dto.ord.OrdDto;
import JuDBu.custommaster.domain.dto.product.ProductDto;
import JuDBu.custommaster.domain.entity.account.Account;
import JuDBu.custommaster.domain.entity.Ord;
import JuDBu.custommaster.domain.entity.Product;
import JuDBu.custommaster.domain.entity.Shop;
import JuDBu.custommaster.domain.repo.OrdRepo;
import JuDBu.custommaster.domain.repo.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdAcceptService {
    private final AuthenticationFacade authFacade;
    private final ShopRepository shopRepo;
    private final OrdRepo ordRepo;

    // Shop에 있는 주문 전체 불러오기
    public Page<OrdDto> readAllOrdByShop(Long shopId, Pageable pageable) {
        // 접근자 확인
        Account account = authFacade.getAccount();
        log.info("auth: {}", authFacade.getAuth().getName());

        // 매장 주인인지 체크
        if (!isOwner(account, shopId)) {
            log.error("매장 주인만 해당 페이지의 접근이 가능합니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // 해당 매장의 주문 불러오기
        Page<Ord> ords = ordRepo.findByShop_IdOrderByIdDesc(shopId, pageable)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        log.info(ords.toString());

        return ords.map(OrdDto::fromEntity);
    }

    // 주문 리스트에서 Product name 불러오기
    public List<String> orderProductName(Long shopId) {
        // 해당 매장의 주문 불러오기
        List<Ord> ords = ordRepo.findByShop_Id(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        log.info(ords.toString());

        return ords.stream()
                .map(Ord::getProduct)
                .map(Product::getName)
                .collect(Collectors.toList());
    }

    // 주문 리스트에서 주문자명 불러오기
    public List<String> getAccountName(Long shopId) {
        List<Ord> ordList = ordRepo.findByShop_Id(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ordList.stream()
                .map(Ord::getAccount)
                .map(Account::getName)
                .collect(Collectors.toList());

    }
    // 주문 리스트에서 주문 상태 불러오기
    public List<Ord.Status> getOrdStatus(Long shopId) {
        List<Ord> ordStatus = ordRepo.findByShop_Id(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ordStatus.stream()
                .sorted(Comparator.comparing(Ord::getOrdTime).reversed())
                .map(Ord::getStatus)
                .toList();
    }

    // Shop에 있는 주문 상세 확인
    public OrdDto readDetails(Long shopId, Long ordId) {
        // 접근자 확인
        Account account = authFacade.getAccount();
        log.info("auth: {}", authFacade.getAuth().getName());

        // 매장 주인인지 체크
        if (!isOwner(account, shopId)) {
            log.error("매장 주인만 해당 페이지의 접근이 가능합니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // 매장에 속한 주문인지 확인
        Ord ord = ordRepo.findByShop_IdAndId(shopId, ordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return OrdDto.fromEntity(ord);
    }

    // 특정 주문에 대한 Product Name 불러오기
    public ProductDto getProductName(Long shopId, Long ordId) {
        Ord ord = ordRepo.findByShop_IdAndId(shopId, ordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ProductDto.fromEntity(ord.getProduct());
    }

    // 특정 주문에 대한 Account Name 불러오기
    public AccountDto getAccountName(Long shopId, Long ordId) {
        Ord ord = ordRepo.findByShop_IdAndId(shopId, ordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return AccountDto.fromEntity(ord.getAccount());
    }

    // 주문 승락
    public OrdDto accept(Long shopId, Long ordId, String totalPrice) {
        // 접근자 확인
        Account account = authFacade.getAccount();
        log.info("auth: {}", authFacade.getAuth().getName());

        // 매장 주인인지 체크
        if (!isOwner(account, shopId)) {
            log.error("매장 주인만 해당 페이지의 접근이 가능합니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // 매장에 속한 주문인지 확인
        Ord target = ordRepo.findByShop_IdAndId(shopId, ordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 주문 승락
        if (target.getStatus().equals(Ord.Status.CONFIRMED)) {
            log.error("이미 완료된 주문입니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        target.setStatus(Ord.Status.CONFIRMED);
        if (Integer.parseInt(totalPrice) < target.getProduct().getExPrice()) {
            log.error("요청 메뉴 가격보다 작은 가격을 설정할 수 없습니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        target.setStatus(Ord.Status.CONFIRMED);
        target.setTotalPrice(Integer.parseInt(totalPrice));
        ordRepo.save(target);

        return OrdDto.fromEntity(target);
    }

    // 주문 거절
    public void deleteOrd(Long shopId, Long ordId) {
        // 접근자 확인
        Account account = authFacade.getAccount();
        log.info("auth: {}", authFacade.getAuth().getName());

        // 매장 주인인지 체크
        if (!isOwner(account, shopId)) {
            log.error("매장 주인만 해당 페이지의 접근이 가능합니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // 매장에 속한 주문인지 확인
        Ord target = ordRepo.findById(ordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Shop shop = target.getProduct().getShop();

        if (!shopId.equals(shop.getId())) {
            log.error("해당 매장의 주문이 아닙니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // 주문 거절
        target.setStatus(Ord.Status.DECLINED);
        log.info("Status: {}", target.getStatus());
        ordRepo.delete(target);
    }

    // 매장 주인인지 확인
    private boolean isOwner(Account account, Long shopId) {
        // 매장 불러오기
        Shop shop = shopRepo.findById(shopId)
                .orElseThrow(() ->new ResponseStatusException(HttpStatus.NOT_FOUND));
        log.info("shop Account: {}", shop.getAccount().getUsername());

        return shop.getAccount().equals(account);
    }
}
