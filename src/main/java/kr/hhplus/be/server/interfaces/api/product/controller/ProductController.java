package kr.hhplus.be.server.interfaces.api.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.interfaces.api.common.MultiResoponseDto;
import kr.hhplus.be.server.interfaces.api.common.SingleResponseDto;
import kr.hhplus.be.server.interfaces.api.product.dto.ProductResponse;
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

    @Operation(summary = "상품 전체 조회", description = "상품 전체 조회을합니다.")
    //상품 전체 조회
    @GetMapping("/all")
    public ResponseEntity listProducts(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize){
        ProductResponse productResponse1= ProductResponse.builder()
                .productId(1)
                .name("장갑")
                .stock(3)
                .price(230000)
                .build();
        ProductResponse productResponse2= ProductResponse.builder()
                .productId(2)
                .name("이불")
                .stock(5)
                .price(320000)
                .build();
        List<ProductResponse> productResponses = new ArrayList<>();
        productResponses.add(productResponse1);
        productResponses.add(productResponse2);


        return new ResponseEntity<>(new MultiResoponseDto<>(productResponses), HttpStatus.OK);
    }

    @Operation(summary = "상위 상품 조회", description = "상위 상품을 조회합니다.")
    //상위 상품 조회
    @GetMapping("/top")
    public ResponseEntity topProducts(){
        ProductResponse productResponse1= ProductResponse.builder()
                .productId(1)
                .name("장갑")
                .stock(3)
                .price(230000)
                .build();
        ProductResponse productResponse2= ProductResponse.builder()
                .productId(2)
                .name("이불")
                .stock(5)
                .price(320000)
                .build();
        List<ProductResponse> productResponses = new ArrayList<>();
        productResponses.add(productResponse1);
        productResponses.add(productResponse2);

        return new ResponseEntity<>(new MultiResoponseDto<>(productResponses), HttpStatus.OK);
    }
}
