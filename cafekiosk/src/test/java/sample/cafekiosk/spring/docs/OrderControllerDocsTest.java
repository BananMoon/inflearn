package sample.cafekiosk.spring.docs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import sample.cafekiosk.spring.RestDocsSupport;
import sample.cafekiosk.spring.api.controller.order.OrderController;
import sample.cafekiosk.spring.api.service.order.OrderService;
import sample.cafekiosk.spring.api.service.order.dto.OrderCreateRequest;
import sample.cafekiosk.spring.api.service.order.dto.OrderCreateResponse;
import sample.cafekiosk.spring.api.service.order.dto.OrderCreateServiceRequest;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderStatus;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * OrderController의 Rest Docs Test Code
 */
class OrderControllerDocsTest extends RestDocsSupport {
    private final OrderService orderService = Mockito.mock(OrderService .class);

    @Override
    protected Object initController() {
        return new OrderController(orderService);
    }

    @DisplayName("신규 주문 생성하는 API")
    @Test
    void createOrder() throws Exception {
        List<String> productNumbers = List.of("001", "002");
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(productNumbers);
        given(orderService.createOrder(any(OrderCreateServiceRequest.class), any(LocalDateTime.class))).willReturn(
                OrderCreateResponse.of(
                        Order.builder()
                                .id(1L)
                                .orderedDateTime(LocalDateTime.now())
                                .orderStatus(OrderStatus.COMPLETED)
                                .products(List.of(
                                        Product.builder()
                                                .id(1L)
                                                .name("카페모카")
                                                .price(4000)
                                                .sellingStatus(ProductSellingStatus.SELLING)
                                                .type(ProductType.HANDMADE)
                                                .productNumber("001")
                                                .build(),
                                        Product.builder()
                                                .id(2L)
                                                .name("토피넛 라떼")
                                                .price(4500)
                                                .sellingStatus(ProductSellingStatus.SELLING)
                                                .type(ProductType.HANDMADE)
                                                .productNumber("002").build())
                                ).build()
                ));
        mockMvc.perform(
                post("/api/v1/orders")
                        .content(objectMapper.writeValueAsString(orderCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("order-create",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("productNumbers").type(JsonFieldType.ARRAY)
                                        .description("상품 번호 리스트")
                        ),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("응답 코드"),
                                PayloadDocumentation.fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("응답 상태"),
                                PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),
                                PayloadDocumentation.fieldWithPath("data.orderId").type(JsonFieldType.NUMBER)
                                        .description("주문 번호"),
                                PayloadDocumentation.fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER)
                                        .description("주문 가격"),
                                PayloadDocumentation.fieldWithPath("data.registeredDateTime").type(JsonFieldType.ARRAY)
                                        .description("주문 일시"),
                                PayloadDocumentation.fieldWithPath("data.products").type(JsonFieldType.ARRAY)
                                        .description("주문 상품"),
                                PayloadDocumentation.fieldWithPath("data.products[].id").type(JsonFieldType.NUMBER)
                                        .description("주문 상품의 ID"),
                                PayloadDocumentation.fieldWithPath("data.products[].productNumber").type(JsonFieldType.STRING)
                                        .description("주문 상품의 주문 번호"),
                                PayloadDocumentation.fieldWithPath("data.products[].type").type(JsonFieldType.STRING)
                                        .description("주문 상품의 타입"),
                                PayloadDocumentation.fieldWithPath("data.products[].sellingStatus").type(JsonFieldType.STRING)
                                        .description("주문 상품의 판매 상태"),
                                PayloadDocumentation.fieldWithPath("data.products[].name").type(JsonFieldType.STRING)
                                        .description("주문 상품 명"),
                                PayloadDocumentation.fieldWithPath("data.products[].price").type(JsonFieldType.NUMBER)
                                        .description("주문 상품 가격")
                        )
                ));
    }
}
