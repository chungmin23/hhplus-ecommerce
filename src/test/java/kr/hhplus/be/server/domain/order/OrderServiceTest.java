package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.common.ErroMessages;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.order.OrderRepository;
import kr.hhplus.be.server.infrastructure.product.ProductRepository;
import kr.hhplus.be.server.infrastructure.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    private final Long VALID_USER_ID = 1L;

    // 상품이 존재하지 않는 경우
    @DisplayName("상품이 존재하지 않는 경우")
    @Test
    public void createOrder_productNotFound(){
        // Given
        List<OrderDetailRequest> orderDetails = List.of(
                new OrderDetailRequest(1L, 2)
        );

        User user = User.builder()
                .id(VALID_USER_ID)
                .name("Test User")
                .build();

        Mockito.when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(VALID_USER_ID, orderDetails)
        );

        Assertions.assertEquals(ErroMessages.PRODUCT_NOT_FOUND, exception.getMessage());
        Mockito.verify(userRepository).findById(VALID_USER_ID);
        Mockito.verify(productRepository).findById(1L);
        Mockito.verifyNoInteractions(orderRepository);
    }


    // 재고가 부족한 경우
    @DisplayName("재고가 부족한 경우")
    @Test
    public void testCreateOrder_InsufficientStock() {
        // Given
        List<OrderDetailRequest> orderDetails = List.of(
                new OrderDetailRequest(1L, 5)
        );

        User user = User.builder()
                .id(VALID_USER_ID)
                .name("Test User")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Product 1")
                .price(1000)
                .stock(3) // 재고 부족
                .build();

        Mockito.when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // When & Then
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(VALID_USER_ID, orderDetails)
        );

        Assertions.assertEquals("Insufficient stock for product: Product 1", exception.getMessage());
        Mockito.verify(userRepository).findById(VALID_USER_ID);
        Mockito.verify(productRepository).findById(1L);
        Mockito.verifyNoInteractions(orderRepository);
    }
    

}