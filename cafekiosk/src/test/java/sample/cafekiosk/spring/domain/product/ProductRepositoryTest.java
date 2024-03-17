package sample.cafekiosk.spring.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;

//@SpringBootTest
@DataJpaTest        // @Transactional이 붙어있는 애노테이션
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;
    @DisplayName("원하는 판매 상품 상태를 가진 제품을 전체 조회한다.")
    @Test
    void findAllBySellingStatusIn() {
        // given
        Product sellingBakery = createProduct("001", ProductType.BAKERY, SELLING, "소금빵", 3500);

        Product stopSellingLatte = createProduct("001", ProductType.HANDMADE, STOP_SELLING, "카페 라떼", 5000);
        productRepository.saveAll(List.of(sellingBakery, stopSellingLatte));
        // when
        List<Product> results = productRepository.findAllBySellingStatusIn(List.of(SELLING, STOP_SELLING));

        // then
        assertThat(results).hasSize(2);
        assertThat(results).extracting("productNumber", "name", "sellingStatus")
                .containsExactlyInAnyOrder(
                        tuple("001", "소금빵", SELLING),
                        tuple("001", "카페 라떼", STOP_SELLING)
                );

    }

    /**
     * OrderServiceTest 작성하며 Prod 코드 작성하다가 Repository method 생성하게 되면 바로 여기로 와서 테스트코드 작성!
     */
    @DisplayName("상품번호 리스트로 상품들을 조회한다.")
    @Test
    void findAllByProductNumberIn() {
        // given
        Product sellingBakery = createProduct("001", ProductType.BAKERY, SELLING, "소금빵", 3500);
        Product stopSellingLatte = createProduct("002", ProductType.HANDMADE, STOP_SELLING, "카페 라떼", 5000);
        Product holdShavedIce = createProduct("003", ProductType.HANDMADE, HOLD, "팥빙수", 7000);
        productRepository.saveAll(List.of(sellingBakery, stopSellingLatte, holdShavedIce));
        // when
        List<Product> results = productRepository.findAllByProductNumberIn(List.of("001", "003"));

        // then
        assertThat(results)
                .hasSize(2)
                .extracting("productNumber", "name", "sellingStatus")
                .containsExactlyInAnyOrder(
                        tuple("001", "소금빵", SELLING),
                        tuple("003", "팥빙수", HOLD)
                );
    }

    @Test
    @DisplayName("가장 최근에 저장한 ProductNumber를 조회한다.")
    void findLatestProductNumberOrderByIdDesc() {
        // given
        String targetProductNumber = "003";
        Product sellingBakery = createProduct("001", ProductType.BAKERY, SELLING, "소금빵", 3500);
        Product stopSellingLatte = createProduct("002", ProductType.HANDMADE, STOP_SELLING, "카페 라떼", 5000);
        Product holdShavedIce = createProduct(targetProductNumber, ProductType.HANDMADE, HOLD, "팥빙수", 7000);
        productRepository.saveAll(List.of(sellingBakery, stopSellingLatte, holdShavedIce));

        // when
        String latestProductNumber = productRepository.findLatestProductNumberOrderByIdDesc();
        // then
        assertThat(latestProductNumber).isEqualTo(targetProductNumber);
    }
    @Test
    @DisplayName("가장 최근에 저장한 ProductNumber를 조회할 때, 상품이 하나도 없는 경우 Null을 반환한다.")
    void findLatestProductNumberOrderByIdDesc_noProduct() {
        // when
        String latestProductNumber = productRepository.findLatestProductNumberOrderByIdDesc();
        // then
        assertThat(latestProductNumber).isNull();
    }

    private Product createProduct(String productNumber, ProductType type, ProductSellingStatus sellingStatus, String name, int price) {
        return Product.builder()
                .productNumber(productNumber)
                .type(type)
                .sellingStatus(sellingStatus)
                .name(name)
                .price(price)
                .build();
    }

}