package sample.cafekiosk.spring.docs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import sample.cafekiosk.spring.RestDocsSupport;
import sample.cafekiosk.spring.api.controller.product.ProductController;
import sample.cafekiosk.spring.api.service.product.ProductService;
import sample.cafekiosk.spring.api.service.product.dto.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.dto.ProductCreateServiceRequest;
import sample.cafekiosk.spring.api.service.product.dto.ProductResponse;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

class ProductControllerDocsTest extends RestDocsSupport {
    private final ProductService productService = Mockito.mock(ProductService.class);
    @Override
    protected Object initController() {
        return new ProductController(productService);
    }

    @DisplayName("신규 상품을 등록하는 API")
    @Test
    void createProduct() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest(HANDMADE, ProductSellingStatus.SELLING, "아메리카노", 4000);
        given(productService.createProduct(any(ProductCreateServiceRequest.class)))
                .willReturn(ProductResponse.builder()
                        .id(1L)
                        .productNumber("001")
                        .sellingStatus(ProductSellingStatus.SELLING)
                        .name("아메리카노")
                        .price(4000)
                        .type(HANDMADE)
                        .build()
                );
        mockMvc.perform(
                post("/api/v1/products/new")
                    .content(objectMapper.writeValueAsString(request))  //  직렬화 위해 ObjectMapper 이용.contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("product-create",
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("type").type(JsonFieldType.STRING)   // Enum은 String으로.
                            .description("상품 타입"),
                        PayloadDocumentation.fieldWithPath("sellingStatus").type(JsonFieldType.STRING)
                            .description("상품 판매상태"),
                        PayloadDocumentation.fieldWithPath("name").type(JsonFieldType.STRING)
                            .description("상품 이름"),
                        PayloadDocumentation.fieldWithPath("price").type(JsonFieldType.NUMBER)
                            .description("상품 가격")
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
                        PayloadDocumentation.fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                            .description("상품 ID"),
                        PayloadDocumentation.fieldWithPath("data.productNumber").type(JsonFieldType.STRING)
                            .description("상품 번호"),
                        PayloadDocumentation.fieldWithPath("data.type").type(JsonFieldType.STRING)
                            .description("상품 상태"),
                        PayloadDocumentation.fieldWithPath("data.sellingStatus").type(JsonFieldType.STRING)
                            .description("상품 판매상태"),
                        PayloadDocumentation.fieldWithPath("data.name").type(JsonFieldType.STRING)
                            .description("상품 이름"),
                        PayloadDocumentation.fieldWithPath("data.price").type(JsonFieldType.NUMBER)
                            .description("상품 가격")
                    )
                ));
    }
}
