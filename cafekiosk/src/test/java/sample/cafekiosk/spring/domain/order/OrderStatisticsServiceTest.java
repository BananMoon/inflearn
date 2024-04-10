package sample.cafekiosk.spring.domain.order;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sample.cafekiosk.spring.domain.history.MailSendHistory;
import sample.cafekiosk.spring.domain.history.MailSendHistoryRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderStatisticsServiceTest {
    @Autowired
    private OrderStatisticsService orderStatisticsService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    JPAQueryFactory jpaQueryFactory;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MailSendHistoryRepository mailSendHistoryRepository;

    @DisplayName("결제완료된 주문들을 조회하여 매출 통계 매일을 전송한다.")
    @Test
    void sendOrderStatisticsMail() {
        // given
        LocalDateTime now = LocalDateTime.of(2024, 4, 1, 0, 0, 0);
        Product product1 = createProduct("아메리카노", "001", ProductType.HANDMADE, 3000);
        Product product2 = createProduct("카페모카", "002", ProductType.HANDMADE, 5000);
        Product product3 = createProduct("소떡소떡", "003", ProductType.BAKERY, 2000);
        List<Product> products = List.of(product1, product2, product3);
        productRepository.saveAll(products);
        int totalPrice = products.stream().mapToInt(product -> product.getPrice()).sum() * 2;

        Order order1 = createPaymentCompletedOrder(products, LocalDateTime.of(2024,3,31,23,59, 59));
        Order order2 = createPaymentCompletedOrder(products, now);
        Order order3 = createPaymentCompletedOrder(products, LocalDateTime.of(2024,4,1,23,59, 59));
        Order order4 = createPaymentCompletedOrder(products, LocalDateTime.of(2024,4,2,0,0, 0));
        // when
        boolean result = orderStatisticsService.sendOrderStatisticsMail(now.toLocalDate(), "test@test.com");

        // then
        assertThat(result).isTrue();

        List<MailSendHistory> histories = mailSendHistoryRepository.findAll();
        assertThat(histories).hasSize(1)
                .extracting("content")
                .contains(String.format("총 매출 합계는 %s원입니다.", totalPrice));
    }

    private Order createPaymentCompletedOrder(List<Product> products, LocalDateTime orderedDateTime) {
        return orderRepository.save(Order.builder()
                .products(products)
                .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                .orderedDateTime(orderedDateTime)
                .build());
    }

    private Product createProduct(String name, String productNumber, ProductType type, int price) {
        return Product.builder()
                .name(name)
                .sellingStatus(ProductSellingStatus.SELLING)
                .productNumber(productNumber)
                .type(type)
                .price(price)
                .build();
    }
}