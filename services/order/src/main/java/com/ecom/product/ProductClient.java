package com.ecom.product;

import com.ecom.exception.BusinessException;
import com.ecom.order.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Класс ProductClient реализует логику взаимодействия с внешним сервисом
 * для выполнения операции покупки продуктов.
 * Через RestTemplate, отправляется POST-запрос с данными о покупке,
 * полученный ответ обрабатывается и возвращается тело ответа.
 */
@Service
@RequiredArgsConstructor
public class ProductClient {
    //@Value("${application.config.product-url}")
    @Value("http://localhost:8222/api/v1/products")
    private String productUrl;
    private final RestTemplate restTemplate;

    public List<PurchaseResponse> purchaseProducts(List<PurchaseRequest> requestBody) {
         HttpHeaders headers = new HttpHeaders();
         // Устанавливаем тело запроса в формате JSON.
         headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

         // Создаем сущность запроса с телом и заголовком
         HttpEntity<List<PurchaseRequest>> requestEntity = new HttpEntity<>(requestBody, headers);

         //Определяем тип ответа на запрос
         ParameterizedTypeReference<List<PurchaseResponse>> responseType =
                 new ParameterizedTypeReference<List<PurchaseResponse>>() {};
//        ResponseEntity<OrderResponse> responseEntity = restTemplate.exchange(
//                productUrl, HttpMethod.GET, null, OrderResponse.class
//        );
//        return responseEntity.getBody();
        // Создание сущности ответа
        // Посылаем POST - запрос на URL : productUrl + "/purchase" с телом и заголовком, указанном в requestEntity,
        // ожидая ответ response Type
         ResponseEntity<List<PurchaseResponse>> responseEntity = restTemplate.exchange(
                productUrl + "/purchase", HttpMethod.POST, requestEntity, responseType);

         if(responseEntity.getStatusCode().isError()) {
             throw new BusinessException("Error during product purchase: " + responseEntity.getStatusCode());
         }
        return responseEntity.getBody();
    }
}
