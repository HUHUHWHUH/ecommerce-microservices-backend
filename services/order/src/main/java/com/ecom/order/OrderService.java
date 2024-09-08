package com.ecom.order;

import com.ecom.exception.BusinessException;
import com.ecom.kafka.OrderConfirmation;
import com.ecom.kafka.OrderProducer;
import com.ecom.order.customer.CustomerClient;
import com.ecom.orderline.OrderLineRequest;
import com.ecom.orderline.OrderLineService;
import com.ecom.payment.PaymentClient;
import com.ecom.payment.PaymentRequest;
import com.ecom.product.ProductClient;
import com.ecom.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderRepository orderRepostory;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;

    public Integer createOrder(OrderRequest orderRequest) {
        // Валидация покупателя
        var customer = this.customerClient.findCustomerById(orderRequest.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: Customer with provided id not found "));

        // Покупка товара
        var PurchasedProducts = this.productClient.purchaseProducts(orderRequest.products());
        // Создание заказа
        var order = orderRepostory.save(mapper.toOrder(orderRequest));

        for(PurchaseRequest purchaseRequest: orderRequest.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        var paymentRequest = new PaymentRequest(
                orderRequest.amount(),
                orderRequest.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        orderRequest.reference(),
                        orderRequest.amount(),
                        orderRequest.paymentMethod(),
                        customer,
                        PurchasedProducts
                )
        );
        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return orderRepostory
                .findAll()
                .stream()
                .map(mapper::toOrderResponse).toList();
    }

    public OrderResponse findById(Integer orderId) {
        return orderRepostory
                .findById(orderId)
                .map(mapper::toOrderResponse)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Order with id:: %d not found ", orderId)));
    }
}
