package sample.cafekiosk.spring.api.service.product;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.service.product.dto.ProductCreateServiceRequest;
import sample.cafekiosk.spring.api.service.product.dto.ProductResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(ProductCreateServiceRequest request) {
        String nextProductNumber = createNextProductNumber();

        Product product = request.toEntity(nextProductNumber);
        Product savedProduct = productRepository.save(product);
        return ProductResponse.of(savedProduct);
    }
    private String createNextProductNumber() {
        // 가장 최근의 productNumber 조회
        String productNumber = productRepository.findLatestProductNumberOrderByIdDesc();
        String initProductNumber = "001";
        if (StringUtils.isEmpty(productNumber)) {
            return initProductNumber;
        }
        Integer nextProductNumberInt = Integer.parseInt(productNumber) + 1;

        return String.format("%03d", nextProductNumberInt);
    }

    public List<ProductResponse> getSellingProducts() {
        List<Product> sellingproducts = productRepository.findAllBySellingStatusIn(ProductSellingStatus.forDisplay());
        return sellingproducts.stream().map(ProductResponse::of).toList();
    }
}
