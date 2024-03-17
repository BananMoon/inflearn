package sample.cafekiosk.spring.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTypeTest {
    @DisplayName("[True] 상품 타입이 재고 관리 상품 타입인지를 체크한다.")
    @Test
    void containsStockType_true() {
        // given
        ProductType givenType = ProductType.BAKERY;
        ProductType givenType2 = ProductType.BOTTLE;
        // when
        boolean result = ProductType.containsStockType(givenType);
        boolean result2 = ProductType.containsStockType(givenType2);
        // then
        assertThat(result).isTrue();
        assertThat(result2).isTrue();
    }

    @DisplayName("[False] 상품 타입이 재고 관리 상품 타입인지를 체크한다.")
    @Test
    void containsStockType_false() {
        // given
        ProductType givenType = ProductType.HANDMADE;
        // when
        boolean result = ProductType.containsStockType(givenType);
        // then
        assertThat(result).isFalse();
    }
}