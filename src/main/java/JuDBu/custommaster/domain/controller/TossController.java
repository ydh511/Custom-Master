package JuDBu.custommaster.domain.controller;

import JuDBu.custommaster.domain.dto.ord.OrdDto;
import JuDBu.custommaster.domain.dto.payment.PaymentConfirmDto;
import JuDBu.custommaster.domain.dto.product.ProductDto;
import JuDBu.custommaster.domain.entity.Product;
import JuDBu.custommaster.domain.service.ProductService;
import JuDBu.custommaster.domain.service.ord.OrdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/toss")
@RequiredArgsConstructor
public class TossController {
    private final OrdService ordService;
    private final ProductService productService;

    // object 는 임시
    @PostMapping("/confirm-payment/{ordId}")
    public Object confirmPayment(
            @RequestBody
            PaymentConfirmDto dto,
            @PathVariable
            Long ordId
    ) {
        log.info(dto.toString());
        return ordService.confirmPayment(dto,ordId);
    }

    // 임시 주문 생성
    @PostMapping("/ord-create")
    public Object ordCreate(
            @RequestBody
            OrdDto dto
    ){
        return ordService.ordCreate(dto);
    }

    // 임시 주문 전체 읽기
    @GetMapping("/ord-readAll")
    public List<OrdDto> readAll(){
        return ordService.readAll();
    }

    // 임시 주문 상세 정보 조회
    @GetMapping("/ord-detail/{ordId}")
    public Object getOrdDetail(@PathVariable Long ordId) {
        log.info("Fetching order details for ID: {}", ordId);
        OrdDto ordDetail = ordService.readOne(ordId);
        if (ordDetail == null) {
            log.error("Order not found for ID: {}", ordId);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ordDetail);
    }

    // 임시 주문 상품 정보 조회
    @GetMapping("/product-detail/{productId}")
    public ResponseEntity<?> getProductDetail(@PathVariable Long productId) {
        ProductDto product = productService.readOne(productId);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
