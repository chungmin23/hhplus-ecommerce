package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("""
        SELECT od.product.id 
        FROM OrderDetail od
        WHERE od.regDate >= :startDate
        GROUP BY od.product.id
        ORDER BY SUM(od.quantity) DESC
    """)
    List<Long> findTopProductIdsBySales(@Param("startDate") LocalDateTime startDate, Pageable pageable);

}
