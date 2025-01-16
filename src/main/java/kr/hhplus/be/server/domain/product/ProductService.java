package kr.hhplus.be.server.domain.product;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.interfaces.exception.ErroMessages;
import kr.hhplus.be.server.infrastructure.order.OrderRepository;
import kr.hhplus.be.server.infrastructure.product.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final OrderRepository orderRepositroy;

    public ProductService(ProductRepository productRepository, OrderRepository orderRepositroy) {
        this.productRepository = productRepository;
        this.orderRepositroy = orderRepositroy;
    }

    // 제품 조회
    public List<Product> getProduct(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.getContent();
    }

    // 인기 상품 조회
    public List<Product> getTopProducts(int days, int size){
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Long> topProductIds = orderRepositroy.findTopProductIdsBySales(startDate, PageRequest.of(0, size));
        return productRepository.findAllById(topProductIds);
    }

    // 재고 검증 및 감소
    public void validateAndReduceStock(Long productId, int quantity) {
        Product product = getProductById(productId);

        product.decreaseStock(quantity);
        productRepository.save(product);
    }

    // 제품 정보 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(ErroMessages.PRODUCT_NOT_FOUND));
    }
}
