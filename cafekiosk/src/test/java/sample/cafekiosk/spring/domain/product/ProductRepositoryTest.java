package sample.cafekiosk.spring.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;

//@DataJpaTest
@SpringBootTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;
    @DisplayName("원하는 판매 상품 상태를 가진 제품을 전 조회한다.")
    @Test
    void findAllBySellingStatusIn() {
        // given
        Product sellingBakery = Product.builder()
                .productNumber("BK001")
                .type(ProductType.BAKERY)
                .sellingStatus(SELLING)
                .name("소금빵")
                .price(3500)
                .build();

        Product stopSellingLatte = Product.builder()
                .productNumber("HM001")
                .type(ProductType.HANDMADE)
                .sellingStatus(STOP_SELLING)
                .name("카페 라떼")
                .price(5000)
                .build();
        productRepository.saveAll(List.of(sellingBakery, stopSellingLatte));
        // when
        List<Product> results = productRepository.findAllBySellingStatusIn(List.of(SELLING, STOP_SELLING));

        // then
        assertThat(results).hasSize(2);
        assertThat(results).extracting("productNumber", "name", "sellingStatus")
                .containsExactlyInAnyOrder(
                        tuple("BK001", "소금빵", SELLING),
                        tuple("HM001", "카페 라떼", STOP_SELLING)
                );

    }
}