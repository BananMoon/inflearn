package sample.cafekiosk.spring.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

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

    private static Stream<Arguments> provideProductTypesRelatedStock() {
        return Stream.of(
                Arguments.of(ProductType.BAKERY, true),
                Arguments.of(ProductType.BOTTLE, true),
                Arguments.of(ProductType.HANDMADE, false)
        );
    }

    // 하나의 메서드에서 여러 소스를 전달하여 모든 케이스를 테스트할 수 있다.
    @DisplayName("상품 타입이 재고 관리 상품 타입인지를 체크한다.")
    @MethodSource("provideProductTypesRelatedStock")
    @ParameterizedTest(name = "{index}. ''{0}'' => {1}")
    void containsStockType_TrueFalse_methodSource(ProductType productType, boolean expected) {
        // when
        boolean result = ProductType.containsStockType(productType);
        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("상품 타입이 재고 관리 상품 타입인지를 체크한다.")
    @CsvSource({"BAKERY,true", "BOTTLE,true", "HANDMADE,false"})
    @ParameterizedTest(name = "{index}. ''{0}'' => {1}")
    void containsStockType_TrueFalse_csvSource(ProductType productType, boolean expected) {
        // when
        boolean result = ProductType.containsStockType(productType);
        // then
        assertThat(result).isEqualTo(expected);
    }


}