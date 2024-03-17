package sample.cafekiosk.spring.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {
    private Product productBuilder(String productNumber, int price) {
        return Product.builder()
                .name("메뉴 이름")
                .sellingStatus(ProductSellingStatus.SELLING)
                .type(ProductType.HANDMADE)
                .price(price)
                .productNumber(productNumber)
                .build();
    }

    @DisplayName("주문 생성 시 상품 리스트에 있는 주문 상품들의 총 금액을 계산한다.")
    @Test
    void calculateOrderTotalPrice() {
        // given
        List<Product> products = List.of(
                productBuilder("001", 1000),
                productBuilder("002", 2000)
        );
        // when
        Order result = Order.from(products, LocalDateTime.now());
        // then
        assertThat(result.getTotalPrice()).isEqualTo(3000);
    }

    @DisplayName("주문 생성 시 상품 리스트에 있는 주문 상품들의 주문 상태는 INIT이다.")
    @Test
    void orderStatusIsInit() {
        // given
        List<Product> products = List.of(
                productBuilder("001", 1000),
                productBuilder("002", 2000)
        );
        // when
        Order result = Order.from(products, LocalDateTime.now());
        // then
        assertThat(result.getStatus()).isEqualByComparingTo(OrderStatus.INIT);
    }

    @DisplayName("주문 생성 시 주문 등록 시각을 기록한다.")
    @Test
    void registeredDateTime() {
        // given
        List<Product> products = List.of(
                productBuilder("001", 1000),
                productBuilder("002", 2000)
        );
        // when
        LocalDateTime registeredDateTime = LocalDateTime.now();
        Order result = Order.from(products, registeredDateTime);
        // then
        assertThat(result.getOrderedDateTime()).isEqualTo(registeredDateTime);
    }
}