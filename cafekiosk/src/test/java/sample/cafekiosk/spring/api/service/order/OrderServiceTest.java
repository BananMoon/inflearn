package sample.cafekiosk.spring.api.service.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.service.order.dto.OrderCreateResponse;
import sample.cafekiosk.spring.api.service.order.dto.OrderCreateServiceRequest;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.stock.Stock;
import sample.cafekiosk.spring.domain.stock.StockRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional    // TODO 이슈 있음.
//@DataJpaTest  // JPA 관련 빈들만 조회하여 Service 클래스를 조회하지 못함.
class OrderServiceTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StockRepository stockRepository;
    @Test
    @DisplayName("주문번호 리스트를 받아 주문을 생성한다.")
    void createOrder() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct("001", ProductType.BAKERY, 3000, "소시지 빵");
        Product product2 = createProduct("002", ProductType.HANDMADE, 5000, "바닐라 라떼");
        Product product3 = createProduct("003", ProductType.BAKERY, 2000, "크로아상");
        productRepository.saveAll(List.of(product1, product2, product3));

        OrderCreateServiceRequest request = new OrderCreateServiceRequest(List.of("001", "002"));
        // when
        OrderCreateResponse response = orderService.createOrder(request, registeredDateTime);

        // then
        assertThat(response.getOrderId()).isNotNull();
        assertThat(response)
                .extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, 8000);
        assertThat(response.getProducts()).hasSize(2)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(         // 순서 상관없이 contains 비교
                        tuple("001", 3000),
                        tuple("002", 5000)
                );
    }

    @DisplayName("중복되는 상품번호 리스트로 주문을 생성할 수 있다.")
    @Test
    void createOrderWithDuplicateProductNumbers() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct("001", ProductType.BAKERY, 3000, "소시지 빵");
        Product product2 = createProduct("002", ProductType.HANDMADE, 5000, "바닐라 라떼");
        Product product3 = createProduct("003", ProductType.BAKERY, 2000, "크로아상");
        productRepository.saveAll(List.of(product1, product2, product3));

        OrderCreateServiceRequest request = new OrderCreateServiceRequest(List.of("001", "001"));

        // when
        OrderCreateResponse response = orderService.createOrder(request, registeredDateTime);

        // then
        assertThat(response.getOrderId()).isNotNull();
        assertThat(response)
                .extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, 6000);
        assertThat(response.getProducts()).hasSize(2)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(         // 순서 상관없이 contains 비교
                        tuple("001", 3000),
                        tuple("001", 3000)
                );
    }

    @Test
    @DisplayName("재고 관리 상품이 담긴 주문번호 리스트를 받아 주문을 생성한다.")
    void createOrderWithStock() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct("001", ProductType.BAKERY, 3000, "소시지 빵");
        Product product2 = createProduct("002", ProductType.BOTTLE, 5000, "오렌지 주스");
        Product product3 = createProduct("003", ProductType.HANDMADE, 7000, "와플");
        productRepository.saveAll(List.of(product1, product2, product3));

        Stock stockBakery = Stock.create("001", 2);
        Stock stockBottle = Stock.create("002", 2);
        stockRepository.saveAll(List.of(stockBakery, stockBottle));

        OrderCreateServiceRequest request = new OrderCreateServiceRequest(List.of("001", "001", "002", "003"));
        // when
        OrderCreateResponse response = orderService.createOrder(request, registeredDateTime);

        // then
        assertThat(response.getOrderId()).isNotNull();
        assertThat(response)
                .extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, 18000);
        assertThat(response.getProducts()).hasSize(4)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(         // 순서 상관없이 contains 비교
                        tuple("001", 3000),
                        tuple("001", 3000),
                        tuple("002", 5000),
                        tuple("003", 7000)
                );
        // 재고 감소 체크
        List<Stock> stocks = stockRepository.findAll();
        assertThat(stocks).hasSize(2)
                .extracting("productNumber", "quantity")
                .contains(
                        tuple("001", 0),
                        tuple("002", 1)
                );
    }

    @DisplayName("제공된 수량보다 재고가 부족한 재고 관리 상품이 주문번호 리스트에 포함되어 있으면 예외가 발생한다.")
    @Test
    void createOrderWithStock_failed() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct("001", ProductType.BAKERY, 3000, "소시지 빵");
        Product product2 = createProduct("002", ProductType.BOTTLE, 5000, "오렌지 주스");
        Product product3 = createProduct("003", ProductType.HANDMADE, 7000, "와플");
        productRepository.saveAll(List.of(product1, product2, product3));

        Stock stockBakery = Stock.create("001", 1);
        Stock stockBottle = Stock.create("002", 1);
        stockRepository.saveAll(List.of(stockBakery, stockBottle));

        OrderCreateServiceRequest request = new OrderCreateServiceRequest(List.of("001", "001", "002", "003"));
        // when // then
        assertThatThrownBy(() -> orderService.createOrder(request, registeredDateTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족한 상품입니다.");
    }


    // 도우미 메서드
    private Product createProduct(String productNumber, ProductType type, int price, String name) {
        return Product.builder()
                .productNumber(productNumber)
                .type(type)
                .price(price)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name(name)
                .build();
    }
}