package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.common.ErroMessages;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.order.OrderRepository;
import kr.hhplus.be.server.infrastructure.product.ProductRepository;
import kr.hhplus.be.server.infrastructure.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public OrderService(ProductRepository productRepository, UserRepository userRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Long userId, List<OrderDetailRequest> orderDetails){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErroMessages.USER_NOT_FOUND));

        List<OrderDetail> details = new ArrayList<>();
        int totalPrice = 0;

        for (OrderDetailRequest detailRequest : orderDetails) {
            Product product = productRepository.findById(detailRequest.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(ErroMessages.PRODUCT_NOT_FOUND));

            product.decreaseStock(detailRequest.getQuantity());
            productRepository.save(product);

            OrderDetail detail = OrderDetail.builder()
                    .product(product)
                    .quantity(detailRequest.getQuantity())
                    .price(product.getPrice() * detailRequest.getQuantity())
                    .build();

            details.add(detail);
            totalPrice += detail.getPrice();
        }

        Order order = Order.builder()
                .user(user)
                //.orderDetails(details)
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)
                .build();

        details.forEach(detail -> detail.setOrder(order));
        return orderRepository.save(order);
    }
}
