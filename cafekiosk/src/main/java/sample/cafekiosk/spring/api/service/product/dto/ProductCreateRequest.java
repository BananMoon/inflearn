package sample.cafekiosk.spring.api.service.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateRequest {
    @NotNull
    private ProductType type;
    @NotNull
    private ProductSellingStatus sellingStatus;
    @NotBlank
    private String name;
    @PositiveOrZero   // 0, 양수만 가능.
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
