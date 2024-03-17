package sample.cafekiosk.spring.api.service.order.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import sample.cafekiosk.spring.api.service.product.dto.ProductResponse;
import sample.cafekiosk.spring.domain.order.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class OrderCreateResponse {
    private Long orderId;
    private int totalPrice;
    private LocalDateTime registeredDateTime;
    private List<ProductResponse> products;

    public static OrderCreateResponse of(Order order) {
        return OrderCreateResponse.builder()
                .orderId(order.getId())
                .registeredDateTime(order.getOrderedDateTime())
                .totalPrice(order.getTotalPrice())
                .products(order.getOrderProducts().stream()
                        .map(orderProduct -> ProductResponse.of(orderProduct.getProduct()))
                        .collect(Collectors.toList()))
                .build();
    }
}
