package sample.cafekiosk.spring.api.service.product;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sample.cafekiosk.spring.domain.product.ProductRepository;

/**
 * 상품 번호를 순차적으로 생성하는 팩토리 클래스.
 */
@RequiredArgsConstructor
@Component
public class ProductNumberFactory {
    private final ProductRepository productRepository;

    public String createNextProductNumber() {
        // 가장 최근의 productNumber 조회
        String productNumber = productRepository.findLatestProductNumberOrderByIdDesc();
        String initProductNumber = "001";
        if (StringUtils.isEmpty(productNumber)) {
            return initProductNumber;
        }
        Integer nextProductNumberInt = Integer.parseInt(productNumber) + 1;

        return String.format("%03d", nextProductNumberInt);
    }

}
