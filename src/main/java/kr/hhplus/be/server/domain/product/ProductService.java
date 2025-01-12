package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.infrastructure.order.OrderRepository;
import kr.hhplus.be.server.infrastructure.product.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public List<Product> getProduct(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.getContent();
    }

    public List<Product> getTopProducts(int days, int size){
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Long> topProductIds = orderRepositroy.findTopProductIdsBySales(startDate, PageRequest.of(0, size));
        return productRepository.findAllById(topProductIds);
    }
}
