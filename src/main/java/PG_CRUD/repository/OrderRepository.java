package PG_CRUD.repository;

import PG_CRUD.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

       Optional<Order> findByOrderNumber(String orderNumber);

       List<Order> findByUserId(Long userId);

       Page<Order> findByUserId(Long userId, Pageable pageable);

       List<Order> findByStatus(Order.OrderStatus status);

       Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

       List<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status);

       @Query("SELECT o FROM Order o WHERE " +
                     "LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(o.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(o.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
       Page<Order> searchOrders(@Param("searchTerm") String searchTerm, Pageable pageable);

       @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
       List<Order> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       @Query("SELECT o FROM Order o WHERE o.totalAmount BETWEEN :minAmount AND :maxAmount")
       List<Order> findByTotalAmountBetween(@Param("minAmount") BigDecimal minAmount,
                     @Param("maxAmount") BigDecimal maxAmount);

       @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.createdAt BETWEEN :startDate AND :endDate")
       List<Order> findByUserIdAndCreatedAtBetween(@Param("userId") Long userId,
                     @Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
       long countByStatus(@Param("status") Order.OrderStatus status);

       @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status")
       BigDecimal sumTotalAmountByStatus(@Param("status") Order.OrderStatus status);

       @Query("SELECT o FROM Order o WHERE o.shippedAt BETWEEN :startDate AND :endDate")
       List<Order> findByShippedAtBetween(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);
}
