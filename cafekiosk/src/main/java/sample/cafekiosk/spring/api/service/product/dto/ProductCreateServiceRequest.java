package sample.cafekiosk.spring.api.service.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

/**
 * Product Service Layer에서 사용할 Request Dto
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateServiceRequest {
    private ProductType type;
    private ProductSellingStatus sellingStatus;
    private String name;
    private int price;

    public Product toEntity(String productNumber) {
        return Product.builder()
                .productNumber(productNumber)
                .type(type)
                .name(name)
                .price(price)
                .sellingStatus(sellingStatus)
                .build();
    }
}
