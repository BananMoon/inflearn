package sample.cafekiosk.spring.domain.stock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockTest {
    @Test
    @DisplayName("[True] 재고 수량이 제공된 수량보다 작은지 확인한다.")
    void isQuantityLessThan_true() {
        // given
        Stock stock = Stock.create("001", 1);
        int quantity = 2;
        // when
        boolean result = stock.isQuantityLessThan(quantity);
        // then
        assertThat(result).isTrue();
    }
    @Test
    @DisplayName("[False] 재고 수량이 제공된 수량보다 작은지 확인한다.")
    void isQuantityLessThan_false() {
        // given
        Stock stock = Stock.create("001", 1);
        int quantity = 1;
        // when
        boolean result = stock.isQuantityLessThan(quantity);
        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("재고 수량을 제공된 수량만큼 감소시킨다.")
    void deductQuantity() {
        // given
        Stock stock = Stock.create("001", 5);
        int quantity = 5;
        // when
        stock.deductQuantity(quantity);
        // then
        assertThat(stock.getQuantity()).isZero();
    }

    @Test
    @DisplayName("재고보다 많은 수량으로 차감 시도하는 경우 예외가 발생한다.")
    void deductQuantity_moreQuantity_exception() {
        // given
        Stock stock = Stock.create("001", 5);
        int quantity = 6;
        // when // then
        assertThatThrownBy(() -> stock.deductQuantity(quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("차감할 재고 수량이 없습니다.");
    }

    // Dynamic Test
    @DisplayName("재고 차감 시나리오")
    @TestFactory
    Collection<DynamicTest> stockDeductionDynamicTest() {
        // given
        Stock stock = Stock.create("001", 1);
        return List.of(
                DynamicTest.dynamicTest("재고를 주어진 개수만큼 차감할 수 있다.", () -> {
                    // given
                    int quantity = 1;
                    // when
                    stock.deductQuantity(quantity);
                    // then
                    assertThat(stock.getQuantity()).isZero();
                }),
                DynamicTest.dynamicTest("재고보다 많은 수량으로 차감 시도하는 경우 예외가 발생한다.", () -> {
                    // given
                    int quantity = 1;
                    // when // then
                    assertThatThrownBy(() -> stock.deductQuantity(quantity))
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage("차감할 재고 수량이 없습니다.");
                })
        );
    }
}