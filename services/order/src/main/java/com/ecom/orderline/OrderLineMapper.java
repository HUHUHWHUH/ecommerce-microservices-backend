package com.ecom.orderline;

import com.ecom.order.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderLineMapper {
    public OrderLine toOrderLine(OrderLineRequest request) {
        return OrderLine.builder()
                .id(request.id())
                .productId((request.productId()))
                .quantity(request.quantity())
                .order(Order.builder()
                        .id(request.orderId())
                        .build()
                )


                .build();
    }
}
