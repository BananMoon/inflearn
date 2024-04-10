package sample.cafekiosk.spring.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.service.mail.MailService;

import java.time.LocalDate;
import java.util.List;

/**
 * 하루의 총 매출 통계를 내어 메일 발송 요청하는 Service.
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderStatisticsService {
    private final OrderRepository orderRepository;
    private final MailService mailService;

    public boolean sendOrderStatisticsMail(LocalDate date, String email) {
        // 오늘 하루 기준, 결제 완료된 주문들 조회
        List<Order> orders = orderRepository.findPaymentCompletedOrdersOnToday(
                date.atStartOfDay(), date.plusDays(1).atStartOfDay(),
                OrderStatus.PAYMENT_COMPLETED
        );

        // 총 매출 합계 계산
        int totalAmount = orders.stream()
                .mapToInt(Order::getTotalPrice)
                .sum();

        // 메일 발송
        boolean sendResult = mailService.sendMail("no-reply@cafekiosk.com", email,
                String.format("[매출 통계] %s", date),
                String.format("총 매출 합계는 %s원입니다.", totalAmount));
        if (!sendResult) {
            throw new IllegalStateException("매출 통계 메일 전송에 실패했습니다.");
        }
        return true;
    }

}
