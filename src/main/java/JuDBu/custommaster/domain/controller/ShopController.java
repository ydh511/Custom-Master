package JuDBu.custommaster.domain.controller;

import JuDBu.custommaster.domain.dto.shop.ShopCreateDto;
import JuDBu.custommaster.domain.dto.shop.ShopDto;
import JuDBu.custommaster.domain.dto.shop.ShopReadDto;
import JuDBu.custommaster.domain.dto.shop.ShopUpdateDto;
import JuDBu.custommaster.domain.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// 상점 개설
@Slf4j
@Controller
@RequestMapping("shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    // 상점에 대한 정보 입력 폼
    @GetMapping("create")
    public String createForm(
            @ModelAttribute("createDto")
            ShopCreateDto createDto
    ) {
        shopService.validAccount();
        return "shop/shop-create-form";
    }

    // 상점에 대한 정보 입력
    @PostMapping("create")
    public String create(
            @Validated
            @ModelAttribute("createDto")
            ShopCreateDto createDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            log.error("errors={}", bindingResult.getAllErrors());
            return "shop/shop-create-form";
        }

        //TODO: 상점의 이름, 주소, 전화번호가 중복되지 않은지 검증 추가

        log.info("shopCreateDto = {}", createDto);
        Long shopId = shopService.createShop(createDto);
        redirectAttributes.addAttribute("shopId", shopId);
        return "redirect:/shop/{shopId}";
    }

    // 상점 리스트 조회
    @GetMapping
    public String readPage(
            @PageableDefault(size = 30, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable,
            Model model
    ) {
        Page<ShopReadDto> shopReadPageDto = shopService.readPage(pageable);
        model.addAttribute("pageable", pageable);
        model.addAttribute("shopReadPageDto", shopReadPageDto);
        // TODO 상점 리스트 페이지
        return "shop/shop-readPage";
    }

    // 상점 상세 조회
    @GetMapping("{shopId}")
    public String readOne(
            @PathVariable("shopId")
            Long shopId,
            Model model) {
        ShopReadDto readDto = shopService.readOne(shopId);
        model.addAttribute("readDto", readDto);
        return "shop/shop";
    }

    // 상점 수정 입력 폼
    @GetMapping("{shopId}/update")
    public String updateForm(
            @PathVariable("shopId")
            Long shopId,
            Model model
    ) {
        ShopDto findShop = shopService.findShop(shopId);
        model.addAttribute("updateDto", findShop);
        return "shop/shop-update-form";
    }

    // 상점 수정 정보 입력
    @PostMapping("{shopId}/update")
    public String update(
            @PathVariable("shopId") Long shopId,
            @Validated
            @ModelAttribute("updateDto")
            ShopUpdateDto updateDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            log.error("errors={}", bindingResult.getAllErrors());
            return "shop/shop-update-form";
        }

        Long updateShopId = shopService.updateShop(shopId, updateDto);
        redirectAttributes.addAttribute("shopId", updateShopId);
        return "redirect:/shop/{shopId}";
    }

    // 상점 삭제
    @PostMapping("{shopId}/delete")
    public String delete(
            @PathVariable("shopId")
            Long shopId) {
        shopService.deleteShop(shopId);
        return "redirect:/shop";
    }
}
