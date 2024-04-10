package sample.cafekiosk.spring.domain.order;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepositoryCustom {
    List<Order> findPaymentCompletedOrdersOnToday(LocalDateTime atStartOfDay, LocalDateTime atStartOfDay1, OrderStatus paymentCompleted);

}
