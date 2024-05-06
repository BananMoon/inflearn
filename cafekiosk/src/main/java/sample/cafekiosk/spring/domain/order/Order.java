package sample.cafekiosk.spring.domain.order;

import jakarta.persistence.*;
import lombok.*;
import sample.cafekiosk.spring.domain.BaseEntity;
import sample.cafekiosk.spring.domain.orderproduct.OrderProduct;
import sample.cafekiosk.spring.domain.product.Product;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "orders")
@Entity
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private int totalPrice;

    private LocalDateTime orderedDateTime;

    // 다대다 연관관계를 풀어주는 중간 매핑.
    // mappedBy : OrderProduct 내 필드명으로 지정하여 매핑.
    @OneToMany(mappedBy = "order", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts;

    @Builder
    private Order(OrderStatus orderStatus, List<Product> products, LocalDateTime orderedDateTime) {
        this.status = orderStatus;
        this.totalPrice = calculateTotalPrice(products);
        this.orderedDateTime = orderedDateTime;
        this.orderProducts = products.stream()
                .map(product -> new OrderProduct(this, product))
                .toList();
    }

    public static Order from(List<Product> products, LocalDateTime registeredDateTime) {
        return Order.builder()
                .orderStatus(OrderStatus.INIT)
                .products(products)
                .orderedDateTime(registeredDateTime)
                .build();
    }

    private static int calculateTotalPrice(List<Product> products) {
        return products.stream().mapToInt(Product::getPrice).sum();
    }
}
