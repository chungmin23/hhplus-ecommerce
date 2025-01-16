package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.infrastructure.order.OrderRepository;
import kr.hhplus.be.server.infrastructure.product.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepositroy;
    
    @DisplayName("전체 조회는 성공")
    @Test
    public void testGetProduct_Success() {
        // Given
        int page = 0;
        int size = 10;

        Product product1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .price(1000)
                .stock(20)
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .name("Product 2")
                .price(2000)
                .stock(10)
                .build();

        List<Product> products = List.of(product1, product2);
        Page<Product> productPage = new PageImpl<>(products);

        Mockito.when(productRepository.findAll(PageRequest.of(page, size)))
                .thenReturn(productPage);

        // When
        List<Product> result = productService.getProduct(page, size);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Product 1", result.get(0).getName());
        Assertions.assertEquals("Product 2", result.get(1).getName());

        Mockito.verify(productRepository).findAll(PageRequest.of(page, size));
    }
    





}