package sample.cafekiosk.spring.api.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import sample.cafekiosk.spring.api.service.order.OrderService;
import sample.cafekiosk.spring.api.service.order.dto.OrderCreateResponse;
import sample.cafekiosk.spring.api.service.order.dto.OrderCreateServiceRequest;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.product.Product;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private OrderService orderService;

    @DisplayName("주문을 생성한다.")
    @Test
    void createOrder() throws Exception {
        // given
        OrderCreateServiceRequest givenRequest = new OrderCreateServiceRequest(List.of("001"));
        OrderCreateResponse response = OrderCreateResponse.of(Order.from(List.of(Product.builder().build()), LocalDateTime.now()));
        when(orderService.createOrder(givenRequest, LocalDateTime.now())).thenReturn(response);

        // when // then
        mockMvc.perform(post("/api/v1/orders")
                        .content(objectMapper.writeValueAsString(givenRequest))  //  직렬화 위해 ObjectMapper 이용
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.message").value("success"));
    }
    @DisplayName("주문을 생성할 때 상품 번호 리스트는 필수 값이다.")
    @Test
    void createOrder_noProductNumbers() throws Exception {
        // given
        OrderCreateServiceRequest createRequest = new OrderCreateServiceRequest(null);
        OrderCreateResponse response = OrderCreateResponse.of(Order.from(List.of(Product.builder().build()), LocalDateTime.now()));
        when(orderService.createOrder(createRequest, LocalDateTime.now())).thenReturn(response);

        // when // then
        mockMvc.perform(post("/api/v1/orders")
                        .content(objectMapper.writeValueAsString(createRequest))  //  직렬화 위해 ObjectMapper 이용
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("상품 번호 리스트는 필수입니다./"));
    }
}