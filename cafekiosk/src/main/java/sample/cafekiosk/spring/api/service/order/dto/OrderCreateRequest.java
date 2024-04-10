package sample.cafekiosk.spring.api.service.order.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateRequest {
    @NotEmpty(message = "상품 번호 리스트는 필수입니다.")
    private List<String> productNumbers;

    public OrderCreateServiceRequest toServiceRequest() {
        return new OrderCreateServiceRequest(productNumbers);
    }
}
