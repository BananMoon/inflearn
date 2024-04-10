package sample.cafekiosk.spring.api.service.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateRequest {
    @NotNull(message = "상품 타입은 필수입니다.")
    private ProductType type;

    @NotNull(message = "상품 판매상태는 필수입니다.")
    private ProductSellingStatus sellingStatus;
    @NotBlank(message = "상품 이름은 필수입니다.")
    private String name;
    @Positive(message = "상품 가격은 양수이어야 합니다.")
    private int price;

    public ProductCreateServiceRequest toServiceRequest() {
        return new ProductCreateServiceRequest(type, sellingStatus, name, price);
    }
}
