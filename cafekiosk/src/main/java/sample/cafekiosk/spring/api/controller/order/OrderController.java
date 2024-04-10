package sample.cafekiosk.spring.api.controller.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sample.cafekiosk.spring.ApiResponse;
import sample.cafekiosk.spring.api.service.order.OrderService;
import sample.cafekiosk.spring.api.service.order.dto.OrderCreateRequest;
import sample.cafekiosk.spring.api.service.order.dto.OrderCreateResponse;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@RestController
public class OrderController {
    private final OrderService orderService;
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public ApiResponse<OrderCreateResponse> createOrder(@Valid @RequestBody OrderCreateRequest createRequest) {
        LocalDateTime registeredDateTime = LocalDateTime.now();
        return ApiResponse.ok(orderService.createOrder(createRequest.toServiceRequest(), registeredDateTime));
    }
}
