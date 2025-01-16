package kr.hhplus.be.server.domain.product;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.infrastructure.order.OrderRepository;
import kr.hhplus.be.server.infrastructure.product.ProductRepository;
import kr.hhplus.be.server.interfaces.exception.ErroMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        // 상품 생성
        testProduct = Product.builder()
                .name("Test Product")
                .price(1000)
                .stock(10)
                .build();
        productRepository.save(testProduct);
    }

    @DisplayName("상품 전체 조회 테스트")
    @Test
    void getProductTest() {
        // When
        List<Product> products = productService.getProduct(0, 5);

        // Then
        Assertions.assertNotNull(products);
        Assertions.assertFalse(products.isEmpty());
        Assertions.assertEquals("Test Product", products.get(0).getName());
    }


    @DisplayName("재고 감소 및 주문 생성 테스트")
    @Test
    void createOrderAndReduceStockTest() {
        // Given
        int orderQuantity = 5;

        // Simulate creating an order
        Order order = Order.builder()
                .user(null)
                .coupon(null)
                .totalPrice(testProduct.getPrice() * orderQuantity)
                .discountPrice(0)
                .status(OrderStatus.PENDING)
                .build();

        // When
        productService.validateAndReduceStock(testProduct.getId(), orderQuantity);
        orderRepository.save(order);

        // Then
        Product updatedProduct = productService.getProductById(testProduct.getId());
        Assertions.assertEquals(5, updatedProduct.getStock());

        Order savedOrder = orderRepository.findById(order.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(ErroMessages.ORDER_NOT_FOUND));
        Assertions.assertEquals(orderQuantity * testProduct.getPrice(), savedOrder.getTotalPrice());
    }
}
