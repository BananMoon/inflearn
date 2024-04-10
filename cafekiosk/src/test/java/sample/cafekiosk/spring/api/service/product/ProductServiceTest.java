package sample.cafekiosk.spring.api.service.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.service.product.dto.ProductCreateServiceRequest;
import sample.cafekiosk.spring.api.service.product.dto.ProductResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ProductServiceTest {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("기존 상품이 있을 때 상품을 등록하면, 상품 번호가 +1 정상 증가한 상품이 생성된다.")
    void createProduct() {
        // given
        String targetProductNumber = "002";
        Product givenProduct = createProduct("001", HANDMADE, SELLING, "바닐라 라떼", 5000);
        productRepository.save(givenProduct);

        ProductCreateServiceRequest request = new ProductCreateServiceRequest(HANDMADE, SELLING, "와플", 7000);
        // when
        ProductResponse result = productService.createProduct(request);
        // then
        assertThat(result)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .contains(targetProductNumber, request.getType(), request.getSellingStatus(), request.getName(), request.getPrice());

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(2)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .containsExactlyInAnyOrder(
                        tuple(givenProduct.getProductNumber(), givenProduct.getType(), givenProduct.getSellingStatus(), givenProduct.getName(), givenProduct.getPrice()),
                        tuple(targetProductNumber, request.getType(), request.getSellingStatus(), request.getName(), request.getPrice())
                );
    }

    @Test
    @DisplayName("기존 상품이 없을 때 상품을 등록하면, 상품 번호가 001인 상품이 생성된다.")
    void createProduct_noProduct() {
        // given
        String targetProductNumber = "001";
        ProductCreateServiceRequest request = new ProductCreateServiceRequest(HANDMADE, SELLING, "와플", 7000);
        // when
        ProductResponse result = productService.createProduct(request);
        // then
        assertThat(result.getProductNumber()).isEqualTo(targetProductNumber);
        assertThat(result)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .containsExactly(targetProductNumber, request.getType(), request.getSellingStatus(), request.getName(), request.getPrice());

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .contains(tuple(targetProductNumber, request.getType(), request.getSellingStatus(), request.getName(), request.getPrice()));

    }

    private Product createProduct(String productNumber, ProductType type, ProductSellingStatus sellingStatus, String name, int price) {
        return Product.builder()
                .productNumber(productNumber)
                .type(type)
                .price(price)
                .sellingStatus(sellingStatus)
                .name(name)
                .build();
    }

}