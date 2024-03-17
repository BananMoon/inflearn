package sample.cafekiosk.spring.api.service.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateRequest {

    private List<String> productNumbers;
}
