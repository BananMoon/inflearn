package sample.cafekiosk.spring.api.service.product.dto;

import lombok.*;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ProductResponse {
    private Long id;

    private String productNumber;   // 001, 002..

    private ProductType type;

    private ProductSellingStatus sellingStatus;

    private String name;

    private int price;

    public static ProductResponse of(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productNumber(product.getProductNumber())
                .type(product.getType())
                .sellingStatus(product.getSellingStatus())
                .name(product.getName())
                .price(product.getPrice())
                .build();
    }
}
