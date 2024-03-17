package sample.cafekiosk.spring.api.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.service.order.dto.OrderCreateRequest;
import sample.cafekiosk.spring.api.service.order.dto.OrderCreateResponse;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.stock.Stock;
import sample.cafekiosk.spring.domain.stock.StockRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 주문 서비스
 */
@Transactional
@RequiredArgsConstructor
@Service
public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;

    // 비즈니스 로직은 TDD로 짜봄
    public OrderCreateResponse createOrder(OrderCreateRequest request, LocalDateTime registeredDateTime) {
        List<String> productNumbers = request.getProductNumbers();
        List<Product> products = findProductsBy(productNumbers);
        // 재고 체크해야하는 아이템 필터링
        List<String> stockProductNumbers = products.stream()
                .filter(product -> ProductType.containsStockType(product.getType()))
                .map(Product::getProductNumber)// 필요한 건 상품 번호
                .collect(Collectors.toList());

        // 재고 엔티티 조회
        List<Stock> stocks = stockRepository.findAllByProductNumberIn(stockProductNumbers);
        Map<String, Stock> productNumberStockMap = stocks.stream()
                .collect(Collectors.toMap(Stock::getProductNumber, s -> s));

        // 주문한 상품의 갯수
        Map<String, Long> orderedProductCountMap = stockProductNumbers.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));

        // 재고 계산
        for(String productNumber : productNumberStockMap.keySet()) {
            // stocks 리스트를 O(n) 순회하며 ProductNumber 동일한 재고 필드 가져오는 것보단, Map으로 바꾼 것으로 O(1) 조회
            int requestedQuantity = orderedProductCountMap.get(productNumber).intValue();
            Stock stock = productNumberStockMap.get(productNumber);

            if (stock.isQuantityLessThan(requestedQuantity)) {
                throw new IllegalArgumentException("재고가 부족한 상품입니다.");
            }
            // 재고 상품 차감 시도
            stock.deductQuantity(requestedQuantity);
        }

        Order savedOrder = orderRepository.save(Order.from(products, registeredDateTime));
        return OrderCreateResponse.of(savedOrder);
    }

    /**
     * product number에 매핑되어 Product가 생성되도록 한다.
     */
    private List<Product> findProductsBy(List<String> productNumbers) {
        List<Product> findProducts = productRepository.findAllByProductNumberIn(productNumbers);
        // List를 Map으로 변환 : productNumbers 원소 (product number)를 기준으로(key) Product를 value로 Mapping한다.
        Map<String, Product> productMap = findProducts.stream()
                .collect(Collectors.toMap(Product::getProductNumber, product -> product));

        // Product Number로 productMap에서 value를 찾아서 List에 추가
        return productNumbers.stream()
                .map(productMap::get)
                .collect(Collectors.toList());
    }
}
