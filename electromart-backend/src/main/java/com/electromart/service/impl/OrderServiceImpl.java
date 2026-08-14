package com.electromart.service.impl;

import com.electromart.dto.request.CheckoutRequest;
import com.electromart.dto.request.OrderStatusUpdateRequest;
import com.electromart.dto.response.OrderResponse;
import com.electromart.dto.response.PagedResponse;
import com.electromart.entity.*;
import com.electromart.enums.OrderStatus;
import com.electromart.enums.PaymentMethod;
import com.electromart.enums.PaymentStatus;
import com.electromart.enums.RoleName;
import com.electromart.exception.ForbiddenException;
import com.electromart.exception.InsufficientStockException;
import com.electromart.exception.InvalidOrderStateException;
import com.electromart.exception.ResourceNotFoundException;
import com.electromart.mapper.OrderMapper;
import com.electromart.repository.AddressRepository;
import com.electromart.repository.CartRepository;
import com.electromart.repository.OrderRepository;
import com.electromart.repository.PaymentRepository;
import com.electromart.repository.ProductRepository;
import com.electromart.service.OrderService;
import com.electromart.util.PriceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;

    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("999.00");
    private static final BigDecimal STANDARD_SHIPPING_FEE = new BigDecimal("49.00");

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(OrderStatus.class);
    static {
        ALLOWED_TRANSITIONS.put(OrderStatus.PENDING, EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.PROCESSING, EnumSet.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.SHIPPED, EnumSet.of(OrderStatus.OUT_FOR_DELIVERY));
        ALLOWED_TRANSITIONS.put(OrderStatus.OUT_FOR_DELIVERY, EnumSet.of(OrderStatus.DELIVERED));
        ALLOWED_TRANSITIONS.put(OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class));
        ALLOWED_TRANSITIONS.put(OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));
    }
    private static final Set<OrderStatus> USER_CANCELLABLE = EnumSet.of(OrderStatus.PENDING, OrderStatus.CONFIRMED);

    @Override
    @Transactional
    public OrderResponse checkout(User user, CheckoutRequest request) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for this account"));
        if (cart.getItems().isEmpty()) {
            throw new InvalidOrderStateException("Your cart is empty — add items before checking out");
        }

        Address address = addressRepository.findByIdAndUserId(request.getAddressId(), user.getId())
                .orElseThrow(() -> ResourceNotFoundException.of("Address", request.getAddressId()));

        // Re-validate stock NOW, not whatever was checked when items were added to
        // the cart — availability can have changed since then.
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (!product.isActive() || product.getStockQuantity() < item.getQuantity()) {
                throw new InsufficientStockException(
                        "\"" + product.getName() + "\" no longer has enough stock (requested "
                                + item.getQuantity() + ", available " + product.getStockQuantity() + ")");
            }
        }

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(user)
                .status(OrderStatus.PENDING)
                .shippingFullName(address.getFullName())
                .shippingPhone(address.getPhone())
                .shippingAddressLine(address.getAddressLine())
                .shippingCity(address.getCity())
                .shippingState(address.getState())
                .shippingPostalCode(address.getPostalCode())
                .shippingCountry(address.getCountry())
                .placedAt(LocalDateTime.now())
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discountTotal = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            BigDecimal unitPrice = PriceUtils.discountedPrice(product);
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            BigDecimal lineDiscount = product.getPrice().subtract(unitPrice)
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    // Snapshot fields — frozen here, independent of future edits
                    // to the live product (Order must store historical data).
                    .productNameSnapshot(product.getName())
                    .productBrandSnapshot(product.getBrand())
                    .productImageSnapshot(product.getImageUrl())
                    .unitPrice(unitPrice)
                    .quantity(cartItem.getQuantity())
                    .lineTotal(lineTotal)
                    .build();
            order.getItems().add(orderItem);

            subtotal = subtotal.add(lineTotal);
            discountTotal = discountTotal.add(lineDiscount);

            // Deduct stock now — the whole checkout is one @Transactional method,
            // so if anything downstream fails, this rolls back along with it.
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        BigDecimal shippingFee = subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0 ? BigDecimal.ZERO : STANDARD_SHIPPING_FEE;

        order.setSubtotal(subtotal);
        order.setDiscountTotal(discountTotal);
        order.setShippingFee(shippingFee);
        order.setTotalAmount(subtotal.add(shippingFee));

        order = orderRepository.save(order);

        Payment payment = createPayment(order, request.getPaymentMethod());

        // Clear the cart — checkout succeeded, nothing left to check out again.
        cart.getItems().clear();

        return OrderMapper.toResponse(order, payment);
    }

    private Payment createPayment(Order order, PaymentMethod method) {
        boolean instantlySettled = method == PaymentMethod.UPI || method == PaymentMethod.CARD;

        Payment payment = Payment.builder()
                .order(order)
                .method(method)
                .status(instantlySettled ? PaymentStatus.SUCCESS : PaymentStatus.PENDING)
                .amount(order.getTotalAmount())
                .transactionId(instantlySettled ? generateTransactionId() : null)
                .paidAt(instantlySettled ? LocalDateTime.now() : null)
                .build();

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getMyOrders(User user, Pageable pageable) {
        Page<Order> page = orderRepository.findByUserId(user.getId(), pageable);
        return PagedResponse.from(page.map(order -> OrderMapper.toResponse(order, paymentFor(order).orElse(null))));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderDetail(Long id, User requester) {
        Order order = orderRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Order", id));
        assertCanView(order, requester);
        return OrderMapper.toResponse(order, paymentFor(order).orElse(null));
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long id, User user) {
        Order order = orderRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> ResourceNotFoundException.of("Order", id));

        if (!USER_CANCELLABLE.contains(order.getStatus())) {
            throw new InvalidOrderStateException(
                    "This order can no longer be cancelled (current status: " + order.getStatus() + ")");
        }

        restoreStock(order);
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        orderRepository.save(order);

        return OrderMapper.toResponse(order, paymentFor(order).orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> adminGetAll(OrderStatus status, Pageable pageable) {
        Page<Order> page = (status != null)
                ? orderRepository.findByStatus(status, pageable)
                : orderRepository.findAll(pageable);
        return PagedResponse.from(page.map(order -> OrderMapper.toResponse(order, paymentFor(order).orElse(null))));
    }

    @Override
    @Transactional
    public OrderResponse adminUpdateStatus(Long id, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Order", id));

        OrderStatus target = request.getStatus();
        Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(order.getStatus(), Set.of());
        if (!allowed.contains(target)) {
            throw new InvalidOrderStateException(
                    "Cannot move an order from " + order.getStatus() + " to " + target);
        }

        if (target == OrderStatus.CANCELLED) {
            restoreStock(order);
            order.setCancelledAt(LocalDateTime.now());
        }
        if (target == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
            // COD payments settle on delivery — mark it paid now, with a
            // generated reference the same as any other "successful" dummy payment.
            paymentFor(order).ifPresent(payment -> {
                if (payment.getStatus() == PaymentStatus.PENDING) {
                    payment.setStatus(PaymentStatus.SUCCESS);
                    payment.setTransactionId(generateTransactionId());
                    payment.setPaidAt(LocalDateTime.now());
                    paymentRepository.save(payment);
                }
            });
        }

        order.setStatus(target);
        orderRepository.save(order);

        return OrderMapper.toResponse(order, paymentFor(order).orElse(null));
    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() != null) {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }
    }

    private void assertCanView(Order order, User requester) {
        boolean isOwner = order.getUser().getId().equals(requester.getId());
        boolean isAdmin = requester.getRoles().stream().anyMatch(r -> r.getName() == RoleName.ADMIN);
        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("You can only view your own orders");
        }
    }

    private Optional<Payment> paymentFor(Order order) {
        return paymentRepository.findByOrderId(order.getId());
    }

    private String generateOrderNumber() {
        String candidate;
        do {
            candidate = "ORD-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                    + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (orderRepository.findByOrderNumber(candidate).isPresent());
        return candidate;
    }

    private String generateTransactionId() {
        String candidate;
        do {
            candidate = "DEMO-TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        } while (paymentRepository.existsByTransactionId(candidate));
        return candidate;
    }
}