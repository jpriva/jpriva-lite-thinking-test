package com.jpriva.orders.domain.model;

import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.MoneyErrorCodes;
import com.jpriva.orders.domain.exceptions.OrderErrorCodes;
import com.jpriva.orders.domain.model.vo.Currency;
import com.jpriva.orders.domain.model.vo.Money;
import com.jpriva.orders.domain.model.vo.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Order {

    private final UUID id;
    private final UUID companyId;
    private final UUID clientId;
    private final String clientName;
    private String address;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Money totalAmount;
    private List<OrderItem> items;

    @Builder
    public Order (UUID id, UUID companyId, UUID clientId, String clientName, String address, LocalDateTime orderDate, OrderStatus status, Money totalAmount, List<OrderItem> items){
        if (id == null){
            throw new DomainException(OrderErrorCodes.ORDER_ID_NULL);
        }
        if (companyId == null){
            throw new DomainException(OrderErrorCodes.ORDER_COMPANY_ID_NULL);
        }
        if (clientId == null){
            throw new DomainException(OrderErrorCodes.ORDER_CLIENT_ID_NULL);
        }
        if (clientName == null || clientName.isBlank()){
            throw new DomainException(OrderErrorCodes.ORDER_CLIENT_NAME_NULL);
        }
        if (items==null) {
            items = new ArrayList<>();
        }

        this.id = id;
        this.companyId = companyId;
        this.clientId = clientId;
        this.clientName = clientName.trim();
        changeAddress(address);
        changeOrderDate(orderDate);
        changeStatus(status);
        changeTotalAmount(totalAmount);
        this.items = items;
    }

    public static Order create(UUID companyId, UUID clientId, String clientName, String address, Currency currency){
        return Order.builder()
                .id(UUID.randomUUID())
                .companyId(companyId)
                .clientId(clientId)
                .clientName(clientName)
                .address(address)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(Money.zero(currency))
                .build();
    }

    public void changeCurrency(Currency currency){
        if (currency == null){
            throw new DomainException(MoneyErrorCodes.MONEY_ERROR_CURRENCY);
        }
        this.totalAmount = Money.zero(currency);
        this.items = new ArrayList<>();
    }

    public void changeAddress(String address) {
        if (address == null || address.isBlank()){
            throw new DomainException(OrderErrorCodes.ORDER_ADDRESS_NULL);
        }
        this.address = address.trim();
    }

    public void changeOrderDate(LocalDateTime orderDate) {
        if (orderDate == null){
            throw new DomainException(OrderErrorCodes.ORDER_DATE_NULL);
        }
        this.orderDate = orderDate;
    }

    public void changeStatus(OrderStatus status) {
        if (status == null){
            throw new DomainException(OrderErrorCodes.ORDER_STATUS_NULL);
        }
        this.status = status;
    }

    public void changeTotalAmount(Money totalAmount) {
        if (totalAmount == null){
            throw new DomainException(OrderErrorCodes.ORDER_TOTAL_AMOUNT_NULL);
        }
        this.totalAmount = totalAmount;
    }

    public void addItem(OrderItem item){
        if (item == null){
            throw new DomainException(OrderErrorCodes.ORDER_NO_ITEM_ADDED);
        }
        if (!item.getUnitPrice().currency().equals(this.totalAmount.currency())){
            throw new DomainException(OrderErrorCodes.ORDER_ITEM_CURRENCY_MISMATCH);
        }

        OrderItem itemDuplication = this.items.stream().filter(i -> i.getProductId().equals(item.getProductId())).findFirst().orElse(null);
        if (itemDuplication != null) {
            throw new DomainException(OrderErrorCodes.ORDER_ITEM_ALREADY_ADDED);
        }
        this.items.add(item);
        calculateTotal();
    }

    public void removeItem(UUID itemId) {
        if (itemId == null) {
            throw new DomainException(OrderErrorCodes.ORDER_ITEM_ID_NULL);
        }
        OrderItem item = this.items.stream().filter(i -> i.getId().equals(itemId)).findFirst().orElse(null);
        if (item == null){
            return;
        }
        this.items.remove(item);
        calculateTotal();
    }

    public void changeItemPrice(UUID itemId, Money price){
        if (itemId==null){
            throw new DomainException(OrderErrorCodes.ORDER_ITEM_ID_NULL);
        }
        if (price==null){
            throw new DomainException(OrderErrorCodes.ORDER_PRODUCT_PRICE_NULL);
        }
        if (!price.currency().equals(this.totalAmount.currency())){
            throw new DomainException(OrderErrorCodes.ORDER_ITEM_CURRENCY_MISMATCH);
        }

        OrderItem item = this.items.stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new DomainException(OrderErrorCodes.ORDER_ITEM_NOT_FOUND));

        item.changeUnitPrice(price);
        calculateTotal();
    }

    private void calculateTotal() {
        if (items == null || items.isEmpty()) {
            this.totalAmount = Money.zero(this.totalAmount.currency());
            return;
        }

        Money total = Money.zero(this.totalAmount.currency());
        for (OrderItem item : items) {
            total = total.add(item.getUnitPrice().multiply(item.getQuantity()));
        }
        this.totalAmount = total;
    }
}