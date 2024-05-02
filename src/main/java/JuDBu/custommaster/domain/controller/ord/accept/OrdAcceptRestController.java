package JuDBu.custommaster.domain.controller.ord.accept;

import JuDBu.custommaster.domain.dto.ord.OrdDto;
import JuDBu.custommaster.domain.entity.Ord;
import JuDBu.custommaster.domain.service.ord.accept.OrdAcceptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/order-accept/{shopId}")
@RequiredArgsConstructor
public class OrdAcceptRestController {
    private final OrdAcceptService ordAcceptService;

    // 주문 리스트 불러오기
    @GetMapping("/read-all")
    public Page<OrdDto> ordList(
            @PathVariable("shopId") Long shopId
    ) {
        return ordAcceptService.readAllOrdByShop(shopId, PageRequest.of(0,3));
    }

    // 주문 리스트에서 Product name 불러오기
    @GetMapping("/read-name")
    public List<String> orderProductName(
            @PathVariable("shopId") Long shopId
    ) {
        return ordAcceptService.orderProductName(shopId);
    }

    // 주문 리스트에서 주문 상태 불러오기
    @GetMapping("/status")
    public List<Ord.Status> getOrdStatus(
            @PathVariable("shopId") Long shopId
    ) {
        return ordAcceptService.getOrdStatus(shopId);
    }

    // READ ONE ord
    @GetMapping("/read/{id}")
    public OrdDto readOrd(
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long ordId
    ) {
        return ordAcceptService.readDetails(shopId, ordId);
    }
}
