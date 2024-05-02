package JuDBu.custommaster.domain.controller.ord;

import JuDBu.custommaster.domain.dto.account.AccountDto;
import JuDBu.custommaster.domain.dto.ord.OrdDto;
import JuDBu.custommaster.domain.dto.product.ProductDto;
import JuDBu.custommaster.domain.dto.shop.ShopReadDto;
import JuDBu.custommaster.domain.service.ord.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    public String readOneAccount(Model model) {
        AccountDto account = profileService.readOneAccount();
        model.addAttribute("account", account);
        return "account/my-profile";
    }

    // 주문 확인하기
    @GetMapping("/ord-list")
    public String ordList(
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable,
            Model model
    ) {
        AccountDto account = profileService.readOneAccount();
        Page<OrdDto> ords = profileService.readAllOrdByAccount(pageable);
        List<String> productNames = profileService.ordProductName();
        List<String> shopNames = profileService.getShopName();

        model.addAttribute("account", account);
        model.addAttribute("ords", ords);
        model.addAttribute("names", productNames);
        model.addAttribute("shops", shopNames);
        return "ord/account/list";
    }

    // 주문 상세보기
    @GetMapping("/read/{ordId}")
    public String readOrd(
            @PathVariable("ordId") Long ordId,
            Model model
    ) {
        OrdDto ord = profileService.readDetails(ordId);
        ProductDto product = profileService.getProductName(ordId);
        ShopReadDto shop = profileService.getShopName(ordId);

        model.addAttribute("ord", ord);
        model.addAttribute("product", product);
        model.addAttribute("shop", shop);
        return "ord/account/read-one";
    }

}
