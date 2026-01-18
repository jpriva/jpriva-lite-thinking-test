package com.jpriva.orders.domain.model;

import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.OrderErrorCodes;
import com.jpriva.orders.domain.model.vo.Currency;
import com.jpriva.orders.domain.model.vo.Money;
import com.jpriva.orders.domain.model.vo.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrderSuccessfully() {
        UUID id = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        Money totalAmount = Money.zero(Currency.USD);

        Order order = new Order(
                id,
                companyId,
                clientId,
                "John Doe",
                "123 Main St",
                LocalDateTime.now(),
                OrderStatus.PENDING,
                totalAmount,
                new ArrayList<>()
        );

        assertNotNull(order);
        assertEquals(id, order.getId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithNullId() {
        DomainException exception = assertThrows(DomainException.class, () -> new Order(
                null,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Client",
                "Address",
                LocalDateTime.now(),
                OrderStatus.PENDING,
                Money.zero(Currency.USD),
                new ArrayList<>()
        ));

        assertEquals(OrderErrorCodes.ORDER_ID_NULL.getCode(), exception.getCode());
    }

    @Test
    void shouldAddItemAndRecalculateTotal() {
        Order order = createValidOrder();
        Product product = createValidProduct();
        ProductPrice price = product.getPrices().get(Currency.USD.getCode());
        int quantity = 2;

        OrderItem item = OrderItem.create(order.getId(), product, quantity, price);
        order.addItem(item);

        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("200.00"), order.getTotalAmount().amount());
    }

    @Test
    void shouldThrowExceptionWhenAddingItemWithDifferentCurrency() {
        // GIVEN an order in USD
        Order order = createValidOrder();

        // AND a product with a price in EUR
        Product productInEur = Product.create(UUID.randomUUID(), UUID.randomUUID(), "Product EUR", "SKU-EUR", "Desc");
        Money eurPrice = Money.fromString("EUR", "50");
        productInEur.changePrice(eurPrice);
        ProductPrice eurProductPrice = productInEur.getProductPrice(Currency.EUR);

        // AND an order item based on that product
        OrderItem itemInEur = OrderItem.create(order.getId(), productInEur, 1, eurProductPrice);

        // WHEN adding the EUR item to the USD order
        DomainException exception = assertThrows(DomainException.class, () -> order.addItem(itemInEur));

        // THEN an exception for currency mismatch should be thrown
        assertEquals(OrderErrorCodes.ORDER_ITEM_CURRENCY_MISMATCH.getCode(), exception.getCode());
    }

    @Test
    void shouldRemoveItemAndRecalculateTotal() {
        Order order = createValidOrder();
        Product product = createValidProduct();
        ProductPrice price = product.getPrices().get(Currency.USD.getCode());
        
        OrderItem item1 = OrderItem.create(order.getId(), product, 2, price);
        
        Product product2 = Product.create(UUID.randomUUID(), UUID.randomUUID(), "Prod 2", "SKU2", "Desc");
        product2.changePrice(Money.fromString("USD", "50"));
        ProductPrice price2 = product2.getPrices().get(Currency.USD.getCode());
        OrderItem item2Real = OrderItem.create(order.getId(), product2, 1, price2);

        order.addItem(item1);
        order.addItem(item2Real);
        
        assertEquals(new BigDecimal("250.00"), order.getTotalAmount().amount());

        order.removeItem(item1.getId());

        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("50.00"), order.getTotalAmount().amount());
    }

    @Test
    void shouldChangeItemPriceAndRecalculateTotal() {
        Order order = createValidOrder();
        Product product = createValidProduct();
        ProductPrice price = product.getPrices().get(Currency.USD.getCode());
        OrderItem item = OrderItem.create(order.getId(), product, 1, price);
        order.addItem(item);

        Money newPrice = Money.fromString("USD", "150.00");
        order.changeItemPrice(item.getId(), newPrice);

        assertEquals(new BigDecimal("150.00"), order.getTotalAmount().amount());
        assertEquals(new BigDecimal("150.00"), order.getItems().getFirst().getUnitPrice().amount());
    }

    private Order createValidOrder() {
        return new Order(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Client Name",
                "Address",
                LocalDateTime.now(),
                OrderStatus.PENDING,
                Money.zero(Currency.USD),
                new ArrayList<>()
        );
    }

    private Product createValidProduct() {
        Product product = Product.create(UUID.randomUUID(), UUID.randomUUID(), "Product 1", "SKU1", "Description");
        product.changePrice(Money.fromString("USD", "100.00"));
        return product;
    }
}
