package sample.cafekiosk.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.cafekiosk.unit.beverage.Americano;
import sample.cafekiosk.unit.beverage.Latte;
import sample.cafekiosk.unit.order.Order;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CafeKioskTest {

    @Test
    void addManualTest() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.add(new Americano());

        System.out.println(">>> 담긴 음료 수: " + cafeKiosk.getBeverages().size());
        System.out.println(">>> 담긴 음료: " + cafeKiosk.getBeverages().get(0).getName());
    }

    @Test
    @DisplayName("주문하기")
    void add() {
        // given
        CafeKiosk cafeKiosk = new CafeKiosk();
        // when
        cafeKiosk.add(new Americano());
        // then
        assertThat(cafeKiosk.getBeverages()).hasSize(1);
        assertThat(cafeKiosk.getBeverages()).hasSize(1);

        assertThat(cafeKiosk.calculateTotalPrice()).isEqualTo(4000);
        assertThat(cafeKiosk.getBeverages().get(0).getName()).isEqualTo("아메리카노");
    }

    @Test
    @DisplayName("주문 취소")
    void remove() {
        // given
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();
        cafeKiosk.add(americano);
        cafeKiosk.add(new Latte());
        cafeKiosk.add(new Latte());
        assertThat(cafeKiosk.getBeverages()).hasSize(3);
        // when
        cafeKiosk.remove(americano);
        // then
        assertThat(cafeKiosk.getBeverages()).hasSize(2);
        assertThat(cafeKiosk.getBeverages().get(1).getName()).isEqualTo("라떼");
        assertThat(cafeKiosk.calculateTotalPrice()).isEqualTo(4500 * 2);
    }

    @Test
    @DisplayName("전체 리셋하기")
    void clear() {
        // given
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.add(new Americano());
        cafeKiosk.add(new Latte());
        assertThat(cafeKiosk.getBeverages()).hasSize(2);

        // when
        cafeKiosk.clear();

        // then
        assertThat(cafeKiosk.getBeverages()).isEmpty();
        assertThat(cafeKiosk.calculateTotalPrice()).isZero();
    }

    @Test
    @DisplayName("전체 음료수 값을 계산한다.")
    void calcuateTotalPrice_tdd() {
        // given
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.add(new Americano());
        cafeKiosk.add(new Latte());
        // when
        int totalPrice = cafeKiosk.calculateTotalPrice_TDD();
        // then
        assertThat(totalPrice).isEqualTo(8500);
    }

    @Test
    @DisplayName("[SUCCESS] 음료 수량만큼 주문할 수 있다.")
    void addWithCount_success() {
        // given
        CafeKiosk cafeKiosk = new CafeKiosk();
        // when
        cafeKiosk.add(new Americano(), 3);
        // then
        assertThat(cafeKiosk.getBeverages()).hasSize(3);
        assertThat(cafeKiosk.calculateTotalPrice()).isEqualTo(4000 * 3);
    }

    @Test
    @DisplayName("[FAILED] 음료 수량으로 음수, 0은 불가능하다.")
    void addWithCount_failed() {
        // given
        CafeKiosk cafeKiosk = new CafeKiosk();
        // when & then
        assertThatThrownBy(() -> cafeKiosk.add(new Americano(), -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("음료는 1잔부터 주문할 수 있습니다.");

        assertThatThrownBy(() -> cafeKiosk.add(new Americano(), 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("음료는 1잔부터 주문할 수 있습니다.");
    }

    @Test
    @DisplayName("[SUCCESS] 가게 운영 시간 (10:00~22:00) 내에는 주문을 생성할 수 있다")
    void order_success() {
        // given
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.add(new Latte());
        LocalDateTime now = LocalDateTime.of(2024,2,21,22,0);
        // when
        Order order = cafeKiosk.order(now);
        // then
        assertThat(order.getOrderedBeverages()).hasSize(1);
        assertThat(order.getOrderedBeverages().get(0).getName()).isEqualTo("라떼");
        assertThat(order.getOrderDateTime()).isEqualTo(now);

    }

    @Test
    @DisplayName("[FALIED] 최소 1개 이상 메뉴를 선택하지 않으면 주문할 수 없다")
    void order_nonPicked_failed() {
        // given
        CafeKiosk cafeKiosk = new CafeKiosk();
        LocalDateTime now = LocalDateTime.of(2024,2,21,10,0);

        assertThatThrownBy(() -> cafeKiosk.order(now))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("최소 1개 이상 주문해주세요.");
    }
    @Test
    @DisplayName("[FALIED] 가게 운영 시간 (10:00~22:00) 외에는 주문을 생성할 수 없다")
    void order_nonOpenTime_failed() {
        // given
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.add(new Americano());
        LocalDateTime now = LocalDateTime.of(2024,2,21,8,59);

        assertThatThrownBy(() -> cafeKiosk.order(now))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("가게 운영 시간 (10:00~22:00) 이 아닙니다.");
    }

}