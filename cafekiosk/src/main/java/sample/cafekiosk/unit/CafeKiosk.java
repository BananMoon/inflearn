package sample.cafekiosk.unit;

import lombok.Getter;
import sample.cafekiosk.unit.beverage.Beverage;
import sample.cafekiosk.unit.order.Order;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
@Getter
public class CafeKiosk {
    private static final LocalTime SHOP_OPEN_TIME = LocalTime.of(10, 0);
    private static final LocalTime SHOP_CLOSED_TIME = LocalTime.of(22, 0);

    private final List<Beverage> beverages = new ArrayList<>();

    public void add(Beverage beverage) {
        beverages.add(beverage);
    }
    public void remove(Beverage beverage) {
        beverages.remove(beverage);
    }
    public void clear() {
        beverages.clear();
    }

    public int calculateTotalPrice() {
        int totalPrice = 0;
        for (Beverage beverage : beverages) {
            totalPrice += beverage.getPrice();
        }
        return totalPrice;
    }

    public int calculateTotalPrice_TDD() {
        return beverages.stream().mapToInt(Beverage::getPrice).sum();
    }

    public Order order(LocalDateTime now) {
        // validation check
        if (beverages.isEmpty()) {
            throw new RuntimeException("최소 1개 이상 주문해주세요.");
        }
        LocalTime currentTime = now.toLocalTime();
        if (currentTime.isBefore(SHOP_OPEN_TIME) || currentTime.isAfter(SHOP_CLOSED_TIME)) {
            throw new RuntimeException("가게 운영 시간 (10:00~22:00) 이 아닙니다.");
        }
        return new Order(now, beverages);
    }

    public void add(Beverage beverage, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("음료는 1잔부터 주문할 수 있습니다.");
        }
        for (int i = 0; i < count; i++) {
            this.beverages.add(beverage);
        }
    }
}
