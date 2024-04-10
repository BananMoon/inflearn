package sample.cafekiosk.spring.api.controller.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sample.cafekiosk.spring.ApiResponse;
import sample.cafekiosk.spring.api.service.product.ProductService;
import sample.cafekiosk.spring.api.service.product.dto.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.dto.ProductResponse;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping
@RestController
public class ProductController {
    private final ProductService productService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/api/v1/products/new")
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {   // Post 메서드, Request Body
        return ApiResponse.ok(productService.createProduct(request.toServiceRequest()));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/v1/products/selling")
    public ApiResponse<List<ProductResponse>> getSellingProducts() {
        return ApiResponse.ok(productService.getSellingProducts());
    }
}
