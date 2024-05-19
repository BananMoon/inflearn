package sample.cafekiosk.spring.api.service.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import sample.cafekiosk.spring.IntigrationTestSupport;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductNumberFactoryTest extends IntigrationTestSupport {
    @Autowired
    private ProductNumberFactory productNumberFactory;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("다음 상품 번호를 생성한다.")
    @Test
    void createNextProductNumber() {
        // given
        String firstProductNumber = "001";
        // when
        String result = productNumberFactory.createNextProductNumber();
        // then
        assertThat(result).isEqualTo(firstProductNumber);
    }

    @DisplayName("순차적으로 다음 상품 번호를 생성한다.")
    @TestFactory
    Collection<DynamicTest> dynamicCreateProductNumber() {
        // given
        String firstProductNumber = "001";

        return List.of(
                DynamicTest.dynamicTest("첫번째 생성되는 상품 번호는 001이다.", () -> {
                    // given
                    String result = productNumberFactory.createNextProductNumber();
                    // then
                    assertThat(result).isEqualTo(firstProductNumber);
                }),
                DynamicTest.dynamicTest("상품번호는 001씩 증가한다.", () -> {
                    // given
                    Product firstProduct = Product.builder()
                            .name("")
                            .sellingStatus(ProductSellingStatus.SELLING)
                            .productNumber(firstProductNumber)
                            .type(ProductType.HANDMADE)
                            .build();
                    productRepository.saveAndFlush(firstProduct);
                    String secondProductNumber = "002";
                    // when
                    String result = productNumberFactory.createNextProductNumber();
                    // then
                    assertThat(result).isEqualTo(secondProductNumber);
                }));
    }
}