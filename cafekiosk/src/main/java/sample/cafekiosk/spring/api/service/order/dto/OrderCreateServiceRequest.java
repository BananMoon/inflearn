package sample.cafekiosk.spring.api.service.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Service Layer 위한 Request DTO.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderCreateServiceRequest {
    private List<String> productNumbers;

}
