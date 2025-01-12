package kr.hhplus.be.server.interfaces.api.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.interfaces.api.common.MultiResoponseDto;
import kr.hhplus.be.server.interfaces.api.common.SingleResponseDto;
import kr.hhplus.be.server.interfaces.api.product.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "상품 전체 조회", description = "상품 전체 조회을합니다.")
    //상품 전체 조회
    @GetMapping("/all")
    public ResponseEntity listProducts(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
        List<Product> products = productService.getProduct(page, size);
        return new ResponseEntity<>(new MultiResoponseDto<>(products), HttpStatus.OK);
    }

    @Operation(summary = "상위 상품 조회", description = "상위 상품을 조회합니다.")
    //상위 상품 조회
    @GetMapping("/top")
    public ResponseEntity topProducts( @RequestParam(defaultValue = "3") int days,
                                       @RequestParam(defaultValue = "5") int size){
        List<Product> topProducts = productService.getTopProducts(days, size);
        return new ResponseEntity<>(new MultiResoponseDto<>(topProducts), HttpStatus.OK);
    }
}
