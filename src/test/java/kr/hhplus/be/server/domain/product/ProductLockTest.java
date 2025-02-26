package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.infrastructure.product.ProductRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static junit.framework.TestCase.assertEquals;

@SpringBootTest
public class ProductLockTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;


    @DisplayName("재고 감소 동시성 테스트")
    @Test
    public void testPessimisticLockWithFindById() throws InterruptedException {

    // Given
    Long productId = 1L;

    Product product = Product.builder()
            .stock(100)
            .name("노트북")
            .build();
    productRepository.save(product);

    productRepository.save(product);

    // When
        int numberOfThreads = 5; // 동시에 접근하는 스레드 수
        int reductionAmount = 10; // 각 스레드가 차감할 재고

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for(
        int i = 0;
        i<numberOfThreads;i++)

        {
            executorService.execute(() -> {
                try {
                    productService.validateAndReduceStock(productId, reductionAmount);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

            latch.await();
            executorService.shutdown();

        // Then
        Product updatedStock = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        int expectedStock = 100 - (numberOfThreads * reductionAmount);

        assertEquals(expectedStock, updatedStock.getStock()); // 최종 재고 검증
    }
}
