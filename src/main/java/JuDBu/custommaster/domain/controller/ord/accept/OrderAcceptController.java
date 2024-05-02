package JuDBu.custommaster.domain.controller.ord.accept;

import JuDBu.custommaster.domain.dto.account.AccountDto;
import JuDBu.custommaster.domain.dto.ord.OrdDto;
import JuDBu.custommaster.domain.dto.product.ProductDto;
import JuDBu.custommaster.domain.service.ord.accept.OrdAcceptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/order-accept/{shopId}")
@RequiredArgsConstructor
public class OrderAcceptController {
    private final OrdAcceptService ordAcceptService;

    @GetMapping("/read-all")
    public String ordList(
            @PathVariable("shopId") Long shopId,
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable,
            Model model
    ) {
        Page<OrdDto> ords = ordAcceptService.readAllOrdByShop(shopId, pageable);
        List<String> productNames = ordAcceptService.orderProductName(shopId);
        List<String> accountNames = ordAcceptService.getAccountName(shopId);

        log.info(productNames.toString());
        log.info(accountNames.toString());

        Collections.reverse(productNames);
        Collections.reverse(accountNames);
        model.addAttribute("ords", ords);
        model.addAttribute("names", productNames);
        model.addAttribute("accounts", accountNames);
        return "ord/shop-order-list";
    }

    @GetMapping("/read/{id}")
    public String readOrd(
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long ordId,
            Model model
    ) {
        OrdDto ord = ordAcceptService.readDetails(shopId, ordId);
        ProductDto product = ordAcceptService.getProductName(shopId, ordId);
        AccountDto account = ordAcceptService.getAccountName(shopId, ordId);

        model.addAttribute("ord", ord);
        model.addAttribute("product", product);
        model.addAttribute("account", account);
        return "ord/ord-detail";
    }

    // 주문 요청 승락
    @PostMapping("/accept/{ordId}")
    public String accept(
            @PathVariable("shopId") Long shopId,
            @PathVariable("ordId") Long ordId,
            @RequestParam("price") String price
    ) {
        ordAcceptService.accept(shopId, ordId, price);
        return "redirect:/order-accept/{shopId}/read/{ordId}";
    }

    // 주문 요청 거절
    @PostMapping("/delete/{ordId}")
    public String delete(
            @PathVariable("shopId") Long shopId,
            @PathVariable("ordId") Long ordId
    ) {
        ordAcceptService.deleteOrd(shopId, ordId);
        return "redirect:/order-accept/{shopId}/read-all";
    }
}
