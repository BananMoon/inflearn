package sample.cafekiosk.spring.domain.stock;

import jakarta.persistence.*;
import lombok.*;
import sample.cafekiosk.spring.domain.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "stock")
public class Stock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productNumber;

    private int quantity;

    public static Stock create(String productNumber, int quantity) {
        return Stock.builder()
                .productNumber(productNumber)
                .quantity(quantity)
                .build();
    }

    public boolean isQuantityLessThan(int quantity) {
        return this.quantity < quantity;
    }

    public void deductQuantity(int quantity) {
        if (this.quantity < quantity) {
            throw new IllegalArgumentException("차감할 재고 수량이 없습니다.");
        }
        this.quantity -= quantity;
    }
}
