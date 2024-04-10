package sample.cafekiosk.spring.domain.order;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static sample.cafekiosk.spring.domain.order.QOrder.*;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 오늘 일자에 결제완료된 주문 조회
     * @param start
     * @param end
     * @param orderStatus
     * @return
     */
    @Override
    public List<Order> findPaymentCompletedOrdersOnToday(LocalDateTime start, LocalDateTime end, OrderStatus orderStatus) {
        return queryFactory.selectFrom(order)
                .where(order.orderedDateTime.eq(start).or(order.orderedDateTime.after(start))
                        .and(order.orderedDateTime.before(end))
                        .and(order.status.eq(orderStatus)))
                .fetch();
    }
}
