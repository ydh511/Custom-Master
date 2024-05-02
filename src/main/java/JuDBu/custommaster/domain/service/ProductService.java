package JuDBu.custommaster.domain.service;

import JuDBu.custommaster.domain.dto.product.ProductCreateDto;
import JuDBu.custommaster.domain.dto.product.ProductDto;
import JuDBu.custommaster.domain.dto.product.ProductUpdateDto;
import JuDBu.custommaster.domain.entity.Product;
import JuDBu.custommaster.domain.entity.Shop;
import JuDBu.custommaster.domain.repo.ProductRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ShopService shopService;
    private final ProductRepo productRepository;
    private final FileHandlerUtils fileHandlerUtils;

    /*// 상품 임시 생성
    public ProductService(ProductRepo productRepository) {
        this.productRepository = productRepository;
        if (this.productRepository.count() == 0) {
            this.productRepository.saveAll(List.of(
                    Product.builder()
                            .name("mouse")
                            .exPrice(40)
                            .build(),
                    Product.builder()
                            .name("keyboard")
                            .exPrice(50)
                            .build()
            ));
        }
    }*/

    public List<ProductDto> readAll() {
        return productRepository.findAll().stream()
                .map(ProductDto::fromEntity)
                .toList();
    }

    public ProductDto readOne(Long id) {
        return productRepository.findById(id)
                .map(ProductDto::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // 상점에 상품 추가
    @Transactional
    public void createProduct(Long shopId, ProductCreateDto createDto, MultipartFile exImage) {

        // 상점에 상품 추가 시, 상점의 주인만이 상품을 추가 할 수 있다.

        Shop findShop = shopService.findEntity(shopId);

        // 예시 이미지 저장
        String exImagePath = getExImagePath(shopId, exImage);

        Product product = Product.createProduct(findShop, createDto.getName(), createDto.getExPrice(), exImagePath);
        log.info("product={}", product);

        Product savedProduct = productRepository.save(product);
        log.info("savedProduct={}", savedProduct);
    }

    @Transactional
    public void updateProduct(Long shopId, Long productId, ProductUpdateDto updateDto, MultipartFile exImage) {

        Product findProduct = accountShopContainsFindProduct(shopId, productId);

        // 수정 이미지가 없는 경우 기존 이미지 경로 추가
        String exImagePath = findProduct.getExImage();

        // 수정 이미지가 있는 경우 이미지 저장 후 경로 생성 후 추가
        if (!exImage.isEmpty()) {
            // TODO: 기존 예시 이미지 삭제
            //fileHandlerUtils.deleteFile(findProduct.getExImage());

            // 예시 이미지 저장
            exImagePath = getExImagePath(shopId, exImage);
        }

        findProduct.updateProduct(updateDto.getName(), updateDto.getExPrice(), exImagePath);
        log.info("updateShop={}", findProduct);
    }

    @Transactional
    public void deleteProduct(Long shopId, Long productId) {

        Product findProduct = accountShopContainsFindProduct(shopId, productId);

        log.info("delete product={}", findProduct);
        productRepository.deleteById(productId);
    }

    public ProductUpdateDto findProduct(Long shopId, Long productId) {

        // 상점에 속해있는 상품인지 검증
        Product findProduct = accountShopContainsFindProduct(shopId, productId);

        return ProductUpdateDto.fromEntity(findProduct);
    }

    private Product accountShopContainsFindProduct(Long shopId, Long productId) {

        // 해당 상점과 인증된 유저에 대한 검증
        Shop findShop = shopService.findAccountShop(shopId);

        return shopContainsFindProduct(shopId, productId);
    }

    public Product shopContainsFindProduct(Long shopId, Long productId) {

        Product findProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        log.info("findProduct={}", findProduct);

        Shop findShop = shopService.findEntity(shopId);

        if (!findShop.getProducts().contains(findProduct)) {
            log.error("상점에 존재하지 않는 상품입니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return findProduct;
    }

    private String getExImagePath(Long shopId, MultipartFile exImage) {
        String exImagePath;
        exImagePath = fileHandlerUtils.saveFile(
                String.format("shops/%d/items/", shopId),
                UUID.randomUUID().toString(),
                exImage
        );
        log.info("exImagePath={}", exImagePath);
        return exImagePath;
    }
}
