package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.common.ErroMessages;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.order.OrderRepository;
import kr.hhplus.be.server.infrastructure.payment.PaymentRepository;
import kr.hhplus.be.server.infrastructure.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService; // 테스트 대상 서비스

    @Mock
    private PaymentRepository paymentRepository; // Mock Repository

    @Mock
    private OrderRepository orderRepository; // Mock Repository

    @Mock
    private UserRepository userRepository; // Mock Repository

    private final Long VALID_ORDER_ID = 1L;
    private final Long VALID_USER_ID = 100L;

    // 주문이 존재하지 않은경우 예외 발생
    @Test
    public void testProcessPayment_OrderNotFound() {
        // Given
        Mockito.when(orderRepository.findById(VALID_ORDER_ID)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.processPayment(VALID_ORDER_ID, VALID_USER_ID)
        );

        Assertions.assertEquals(ErroMessages.ORDER_NOT_FOUND, exception.getMessage());
        Mockito.verify(orderRepository).findById(VALID_ORDER_ID);
        Mockito.verifyNoInteractions(userRepository, paymentRepository);
    }


    // 사용자의 잔액이 부족할때 예외 발생
    @Test
    public void testProcessPayment_InsufficientBalance() {
        // Given
        Order order = Order.builder()
                .orderId(VALID_ORDER_ID)
                .totalPrice(10000)
                .status(OrderStatus.PENDING)
                .build();

        User user = User.builder()
                .id(VALID_USER_ID)
                .balance(5000) // 부족한 잔액
                .build();

        Mockito.when(orderRepository.findById(VALID_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.processPayment(VALID_ORDER_ID, VALID_USER_ID)
        );

        assertEquals(ErroMessages.INSUFFICIENT_BALANCE, exception.getMessage());
        Mockito.verify(orderRepository).findById(VALID_ORDER_ID);
        Mockito.verify(userRepository).findById(VALID_USER_ID);
        Mockito.verifyNoInteractions(paymentRepository);
    }
}