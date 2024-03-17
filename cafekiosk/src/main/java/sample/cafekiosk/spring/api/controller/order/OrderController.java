package sample.cafekiosk.spring.api.controller.order;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sample.cafekiosk.spring.api.service.order.OrderService;
import sample.cafekiosk.spring.api.service.order.dto.OrderCreateRequest;
import sample.cafekiosk.spring.api.service.order.dto.OrderCreateResponse;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@RestController
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public OrderCreateResponse createOne(@RequestBody OrderCreateRequest createRequest) {
        LocalDateTime registeredDateTime = LocalDateTime.now();
        return orderService.createOrder(createRequest, registeredDateTime);
    }
}
