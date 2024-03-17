package sample.cafekiosk.spring.api.controller.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sample.cafekiosk.spring.api.service.product.ProductService;
import sample.cafekiosk.spring.api.service.product.dto.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.dto.ProductResponse;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping
@RestController
public class ProductController {
    private final ProductService productService;

    @PostMapping("/api/v1/products/new")
    public void createProduct(@Valid ProductCreateRequest request) {
        productService.createProduct(request);
    }

    @GetMapping("/api/v1/products/selling")
    public List<ProductResponse> getSellingProducts() {
        return productService.getSellingProducts();
    }
}
