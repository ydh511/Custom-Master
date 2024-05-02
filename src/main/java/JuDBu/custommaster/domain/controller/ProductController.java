package JuDBu.custommaster.domain.controller;

import JuDBu.custommaster.domain.dto.product.ProductCreateDto;
import JuDBu.custommaster.domain.dto.product.ProductDto;
import JuDBu.custommaster.domain.dto.product.ProductUpdateDto;
import JuDBu.custommaster.domain.service.ProductService;
import JuDBu.custommaster.domain.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/shop/{shopId}/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ShopService shopService;

    // 상품에 대한 정보 입력 폼
    @GetMapping("create")
    public String createForm(
            @ModelAttribute("createDto")
            ProductCreateDto createDto,
            @PathVariable("shopId")
            Long shopId
    ) {
        shopService.findAccountShop(shopId);
        return "product/product-create-form";
    }

    // 상품에 대한 정보 입력
    @PostMapping("create")
    public String create(
            @Validated
            @ModelAttribute("createDto")
            ProductCreateDto createDto,
            BindingResult bindingResult,
            @PathVariable("shopId")
            Long shopId,
            @RequestParam("exImage") MultipartFile exImage,
            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            log.error("errors={}", bindingResult.getAllErrors());
            return "product/product-create-form";
        }

        log.info("createDto={}", createDto);
        log.info("exImage={}", exImage);

        productService.createProduct(shopId, createDto, exImage);

        redirectAttributes.addAttribute("shopId", shopId);
        return "redirect:/shop/{shopId}";
    }

    // 상품 수정 입력 폼
    @GetMapping("{productId}/update")
    public String updateForm(
            @PathVariable("productId")
            Long productId,
            @PathVariable("shopId")
            Long shopId,
            Model model
    ) {
        ProductUpdateDto productDto = productService.findProduct(shopId, productId);
        model.addAttribute("shopId", shopId);
        model.addAttribute("updateDto", productDto);
        return "product/product-update-form";
    }

    // 상점 수정 정보 입력
    @PostMapping("{productId}/update")
    public String update(
            @PathVariable("productId") Long productId,
            @Validated
            @ModelAttribute("updateDto")
            ProductUpdateDto updateDto,
            BindingResult bindingResult,
            @RequestParam("exImage") MultipartFile exImage,
            @PathVariable("shopId")
            Long shopId,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            log.error("errors={}", bindingResult.getAllErrors());
            return "product/product-update-form";
        }

        log.info("updateDto={}", updateDto);
        log.info("exImage.getOriginalFilename()={}", exImage.getOriginalFilename());

        productService.updateProduct(shopId, productId, updateDto, exImage);

        redirectAttributes.addAttribute("shopId", shopId);
        return "redirect:/shop/{shopId}";
    }

    // 상품 삭제
    @PostMapping("{productId}/delete")
    public String delete(
            @PathVariable("productId")
            Long productId,
            @PathVariable("shopId")
            Long shopId,
            RedirectAttributes redirectAttributes
    ) {
        productService.deleteProduct(shopId, productId);
        redirectAttributes.addAttribute("shopId", shopId);
        return "redirect:/shop/{shopId}";
    }

    @GetMapping
    public List<ProductDto> readAll() {
        return productService.readAll();
    }

    @GetMapping("{id}")
    public ProductDto readOne(
            @PathVariable("id")
            Long id
    ) {
        return productService.readOne(id);
    }
}
