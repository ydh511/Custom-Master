package JuDBu.custommaster.domain.controller.ord.request;

import JuDBu.custommaster.domain.dto.ord.OrdRequestDto;
import JuDBu.custommaster.domain.dto.product.ProductDto;
import JuDBu.custommaster.domain.service.ProductService;
import JuDBu.custommaster.domain.service.ShopService;
import JuDBu.custommaster.domain.service.ord.request.OrdRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrdRequestController {

    private final OrdRequestService ordService;
    private final ShopService shopService;
    private final ProductService productService;

    @GetMapping("/{shopId}/{productId}/request")
    public String requestForm(
            @ModelAttribute("requestDto")
            OrdRequestDto requestDto,
            @PathVariable("shopId") Long shopId,
            @PathVariable("productId") Long productId,
            Model model
    ) {
        // 상점에 상품이 존재하는지 검증
        productService.shopContainsFindProduct(shopId, productId);

        ProductDto productDto = productService.readOne(productId);
        model.addAttribute("product", productDto);

        return "ord/order-form";
    }

    @PostMapping("/{shopId}/{productId}/request")
    public String request(
            @PathVariable("shopId") Long shopId,
            @PathVariable("productId") Long productId,
            @Validated
            @ModelAttribute("requestDto")
            OrdRequestDto requestDto,
            BindingResult bindingResult,
            @RequestParam("exImage") MultipartFile exImage,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            log.error("errors={}", bindingResult.getAllErrors());

            ProductDto productDto = productService.readOne(productId);
            model.addAttribute("product", productDto);

            return "ord/order-form";
        }

        log.info("requestDto = {}", requestDto);
        log.info("exImage = {}", exImage.getOriginalFilename());
        ordService.requestOrder(shopId, productId, requestDto, exImage);

        redirectAttributes.addAttribute("shopId", shopId);
        redirectAttributes.addAttribute("productId", productId);

        return "redirect:/profile/ord-list";
    }
}
