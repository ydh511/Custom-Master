package JuDBu.custommaster.domain.service;

import JuDBu.custommaster.auth.facade.AuthenticationFacade;
import JuDBu.custommaster.domain.dto.shop.ShopCreateDto;
import JuDBu.custommaster.domain.dto.shop.ShopDto;
import JuDBu.custommaster.domain.dto.shop.ShopReadDto;
import JuDBu.custommaster.domain.dto.shop.ShopUpdateDto;
import JuDBu.custommaster.domain.entity.Shop;
import JuDBu.custommaster.domain.entity.account.Account;
import JuDBu.custommaster.domain.entity.account.Authority;
import JuDBu.custommaster.domain.repo.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final ShopRepository shopRepository;
    private final AuthenticationFacade authenticationFacade;

    // 상정 리스트 조회
    public Page<ShopReadDto> readPage(Pageable pageable) {
        return shopRepository.findAll(pageable)
                .map(ShopReadDto::fromEntity);
    }

    // 상점 상세 조회
    public ShopReadDto readOne(Long shopId) {
        return ShopReadDto.fromEntity(shopRepository.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    // 상점 생성
    @Transactional
    public Long createShop(ShopCreateDto createDto) {

        // 인증된 Account의 정보
        Account account = validAccount();

        Shop shop = Shop.createShop(account, createDto.getName(), createDto.getAddress(), createDto.getPhoneNumber());
        log.info("createShop={}", shop);

        Shop savedShop = shopRepository.save(shop);
        log.info("savedShop={}", savedShop);

        return savedShop.getId();
    }

    // 상점 정보 수정
    @Transactional
    public Long updateShop(Long shopId, ShopUpdateDto updateDto) {
        Shop findShop = findAccountShop(shopId);
        findShop.updateShop(updateDto.getName(), updateDto.getAddress(), updateDto.getPhoneNumber());
        return findShop.getId();
    }

    // 상점 삭제
    @Transactional
    public void deleteShop(Long shopId) {
        findAccountShop(shopId);
        shopRepository.deleteById(shopId);
    }

    public ShopDto findShop(Long shopId) {
        Shop findShop = findAccountShop(shopId);
        return ShopDto.fromEntity(findShop);
    }

    public Shop findAccountShop(Long shopId) {

        Account account = getAccount();

        Shop findShop = findEntity(shopId);

        if (!findShop.getAccount().equals(account)) {
            log.error("account가 틀립니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return findShop;
    }

    public Shop findEntity(Long shopId) {
        Shop findShop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
        log.info("findShop={}", findShop);
        return findShop;
    }

    // 인증된 Account가 Shop을 가지고 있는지 검증
    public Account validAccount() {

        Account account = getAccount();
        log.info("account={}", account);

        Shop findShop = shopRepository.findByAccount(account);
        log.info("findShop={}", findShop);

        // 인증된 Account의 상점이 없는지
        if (findShop != null) {
            log.error("이미 상점이 존재합니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // 인증된 Account가 BusinessAccount 인지
        if (!account.getAuthority().equals(Authority.ROLE_BUSINESS_USER)) {
            log.error("사업자가 아닙니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return account;
    }

    private Account getAccount() {
        Account account = authenticationFacade.getAccount();
        log.info("account={}", account);
        return account;
    }
}
