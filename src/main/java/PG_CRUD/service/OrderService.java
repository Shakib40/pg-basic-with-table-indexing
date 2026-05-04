package PG_CRUD.service;

import PG_CRUD.entity.ActivityLog;
import PG_CRUD.entity.Order;
import PG_CRUD.repository.ActivityLogRepository;
import PG_CRUD.repository.OrderRepository;
import PG_CRUD.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ActivityLogRepository activityLogRepository;
    
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
    
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    public Optional<Order> getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }
    
    public List<Order> getOrdersByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return orderRepository.findByUserId(userId);
    }
    
    public Page<Order> getOrdersByUserId(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return orderRepository.findByUserId(userId, pageable);
    }
    
    public Order createOrder(Order order, HttpServletRequest request) {
        if (!userRepository.existsById(order.getUserId())) {
            throw new RuntimeException("User not found with id: " + order.getUserId());
        }
        
        if (order.getOrderNumber() == null || order.getOrderNumber().isEmpty()) {
            order.setOrderNumber(generateOrderNumber());
        }
        
        if (order.getTotalAmount() == null) {
            order.setTotalAmount(order.getUnitPrice().multiply(BigDecimal.valueOf(order.getQuantity())));
        }
        
        Order savedOrder = orderRepository.save(order);
        
        logActivity(savedOrder.getUserId(), null, ActivityLog.Action.CREATE, 
                   ActivityLog.EntityType.ORDER, savedOrder.getId(), 
                   "Order created: " + savedOrder.getOrderNumber(), request);
        
        return savedOrder;
    }
    
    public Order updateOrder(Long id, Order orderDetails, HttpServletRequest request) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        if (orderDetails.getUserId() != null && !orderDetails.getUserId().equals(order.getUserId())) {
            if (!userRepository.existsById(orderDetails.getUserId())) {
                throw new RuntimeException("User not found with id: " + orderDetails.getUserId());
            }
            order.setUserId(orderDetails.getUserId());
        }
        
        if (orderDetails.getProductName() != null) {
            order.setProductName(orderDetails.getProductName());
        }
        
        if (orderDetails.getQuantity() != null) {
            order.setQuantity(orderDetails.getQuantity());
        }
        
        if (orderDetails.getUnitPrice() != null) {
            order.setUnitPrice(orderDetails.getUnitPrice());
        }
        
        if (orderDetails.getQuantity() != null && orderDetails.getUnitPrice() != null) {
            order.setTotalAmount(orderDetails.getUnitPrice().multiply(BigDecimal.valueOf(orderDetails.getQuantity())));
        }
        
        if (orderDetails.getStatus() != null) {
            Order.OrderStatus oldStatus = order.getStatus();
            Order.OrderStatus newStatus = orderDetails.getStatus();
            order.setStatus(newStatus);
            
            if (newStatus == Order.OrderStatus.SHIPPED && oldStatus != Order.OrderStatus.SHIPPED) {
                order.setShippedAt(LocalDateTime.now());
            }
            
            if (newStatus == Order.OrderStatus.DELIVERED && oldStatus != Order.OrderStatus.DELIVERED) {
                order.setDeliveredAt(LocalDateTime.now());
            }
        }
        
        if (orderDetails.getShippingAddress() != null) {
            order.setShippingAddress(orderDetails.getShippingAddress());
        }
        
        if (orderDetails.getBillingAddress() != null) {
            order.setBillingAddress(orderDetails.getBillingAddress());
        }
        
        if (orderDetails.getNotes() != null) {
            order.setNotes(orderDetails.getNotes());
        }
        
        Order updatedOrder = orderRepository.save(order);
        
        logActivity(updatedOrder.getUserId(), null, ActivityLog.Action.UPDATE, 
                   ActivityLog.EntityType.ORDER, updatedOrder.getId(), 
                   "Order updated: " + updatedOrder.getOrderNumber(), request);
        
        return updatedOrder;
    }
    
    public void deleteOrder(Long id, HttpServletRequest request) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        String orderNumber = order.getOrderNumber();
        Long userId = order.getUserId();
        
        orderRepository.deleteById(id);
        
        logActivity(userId, null, ActivityLog.Action.DELETE, 
                   ActivityLog.EntityType.ORDER, id, 
                   "Order deleted: " + orderNumber, request);
    }
    
    public Page<Order> searchOrders(String searchTerm, Pageable pageable) {
        return orderRepository.searchOrders(searchTerm, pageable);
    }
    
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    public Page<Order> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }
    
    public List<Order> getOrdersByUserIdAndStatus(Long userId, Order.OrderStatus status) {
        return orderRepository.findByUserIdAndStatus(userId, status);
    }
    
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByCreatedAtBetween(startDate, endDate);
    }
    
    public List<Order> getOrdersByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return orderRepository.findByTotalAmountBetween(minAmount, maxAmount);
    }
    
    public long getOrderCount() {
        return orderRepository.count();
    }
    
    public long getOrderCountByStatus(Order.OrderStatus status) {
        return orderRepository.countByStatus(status);
    }
    
    public BigDecimal getTotalRevenueByStatus(Order.OrderStatus status) {
        return orderRepository.sumTotalAmountByStatus(status);
    }
    
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }
    
    private void logActivity(Long userId, String username, ActivityLog.Action action, 
                           ActivityLog.EntityType entityType, Long entityId, String description, 
                           HttpServletRequest request) {
        ActivityLog log = new ActivityLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        log.setIpAddress(getClientIpAddress(request));
        log.setUserAgent(request.getHeader("User-Agent"));
        
        activityLogRepository.save(log);
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
