package com.jpriva.orders.application.usecase;

import com.jpriva.orders.application.dto.OrderDto;
import com.jpriva.orders.domain.exceptions.*;
import com.jpriva.orders.domain.model.*;
import com.jpriva.orders.domain.model.vo.Currency;
import com.jpriva.orders.domain.model.vo.OrderStatus;
import com.jpriva.orders.domain.model.vo.Role;
import com.jpriva.orders.domain.ports.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManageOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public Page<OrderDto.Response> getUserOrders(Pageable pageable, String email, String taxId){
        User user = userRepository.findByEmail(email).orElseThrow(()->new DomainException(UserErrorCodes.USER_NOT_FOUND));
        Company company = companyRepository.findByTaxId(taxId).orElseThrow(()->new DomainException(CompanyErrorCodes.COMPANY_NOT_FOUND));
        if (user.getRole() == Role.ADMIN){
            return orderRepository.findByCompanyId(pageable,company.getId()).map(OrderDto.Response::fromDomain);
        }
        Optional<Client> client = clientRepository.findByUserId(user.getId());

        return client.map(value ->
                orderRepository.findByClientIdAndCompanyId(value.getId(), company.getId(), pageable)
                        .map(OrderDto.Response::fromDomain)
        ).orElseGet(() -> Page.empty(pageable));

    }

    @Transactional
    public OrderDto.Response createOrderByUser(OrderDto.CreateByUser request) {
        Currency currency = Currency.valueOf(request.currencyCode());

        Company company = companyRepository.findById(request.companyId())
                .orElseThrow(()->new DomainException(CompanyErrorCodes.COMPANY_NOT_FOUND));
        User user = userRepository.findById(request.userId())
                .orElseThrow(()->new DomainException(UserErrorCodes.USER_NOT_FOUND));

        Optional<Client> clientOpt = clientRepository.findByCompanyIdAndUserId(company.getId(),user.getId());
        Client client;
        if (clientOpt.isEmpty()){
            client = Client.create(company.getId(), user.getId(), user.getFullName(), user.getEmail(), user.getPhone(), user.getAddress());
            clientRepository.save(client);
        } else {
            client = clientOpt.get();
        }

        Order order = Order.create(company.getId(), client.getId(), client.getName(), client.getAddress(), currency);

        Order savedOrder = orderRepository.save(order);
        return OrderDto.Response.fromDomain(savedOrder);
    }
    @Transactional
    public OrderDto.Response createOrderByAdmin(OrderDto.CreateByAdmin request) {
        Currency currency = Currency.valueOf(request.currencyCode());

        Company company = companyRepository.findById(request.companyId())
                .orElseThrow(()->new DomainException(CompanyErrorCodes.COMPANY_NOT_FOUND));

        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(()->new DomainException(ClientErrorCodes.CLIENT_NOT_FOUND));

        Order order = Order.create(company.getId(), client.getId(), client.getName(), client.getAddress(), currency);

        Order savedOrder = orderRepository.save(order);
        return OrderDto.Response.fromDomain(savedOrder);
    }

    @Transactional
    public OrderDto.Response addItem(UUID orderId, OrderDto.AddItemRequest request, String email) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException(OrderErrorCodes.ORDER_NOT_FOUND));
        getOrderAccess(email, order);

        if (order.getStatus() != OrderStatus.PENDING){
            throw new DomainException(OrderErrorCodes.ORDER_STATUS_NOT_PENDING);
        }

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new DomainException(ProductErrorCodes.PRODUCT_NOT_FOUND));

        int quantity = request.quantity() == null ? 1 : request.quantity();

        if (product.getInventory().getQuantity() < quantity) {
            throw new DomainException(ProductErrorCodes.INVENTORY_NOT_ENOUGH);
        }

        ProductPrice productPrice = product.getProductPrice(order.getTotalAmount().currency());

        Optional<OrderItem> itemOpt = order.getItems().stream()
                .filter(i -> i.getProductId().equals(request.productId()))
                .findFirst();
        OrderItem item;
        if (itemOpt.isPresent()){
            item = itemOpt.get();
            item.changeQuantity(quantity);
            order.removeItem(item.getId());
        } else {
            item = OrderItem.create(order.getId(),product, quantity, productPrice);
        }

        order.addItem(item);

        Order savedOrder = orderRepository.save(order);
        return OrderDto.Response.fromDomain(savedOrder);
    }

    @Transactional
    public OrderDto.Response removeItem(UUID orderId, UUID itemId, String email) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException(OrderErrorCodes.ORDER_NOT_FOUND));
        getOrderAccess(email, order);

        order.removeItem(itemId);

        Order savedOrder = orderRepository.save(order);
        return OrderDto.Response.fromDomain(savedOrder);
    }

    private void getOrderAccess(String email, Order order){
        User user = userRepository.findByEmail(email).orElseThrow(()->new DomainException(UserErrorCodes.USER_NOT_FOUND));
        if (user.getRole() == Role.EXTERNAL){
            Client client = clientRepository.findByUserId(user.getId()).orElseThrow(()->new DomainException(ClientErrorCodes.CLIENT_NOT_FOUND));
            if (!order.getClientId().equals(client.getId())){
                throw new DomainException(OrderErrorCodes.ORDER_NOT_ALLOWED);
            }
        }
    }

    @Transactional(readOnly = true)
    public OrderDto.Response getOrder(UUID id, String email) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DomainException(OrderErrorCodes.ORDER_NOT_FOUND));
        getOrderAccess(email, order);
        order.changeStatus(OrderStatus.CONFIRMED);
        order = orderRepository.save(order);
        return OrderDto.Response.fromDomain(order);
    }

    @Transactional
    public OrderDto.Response confirmOrder(UUID id, String email) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DomainException(OrderErrorCodes.ORDER_NOT_FOUND));
        getOrderAccess(email, order);
        if (order.getStatus() != OrderStatus.PENDING){
            throw new DomainException(OrderErrorCodes.ORDER_STATUS_NOT_PENDING);
        }

        if (order.getItems().isEmpty()){
            throw new DomainException(OrderErrorCodes.ORDER_NO_ITEM_ADDED);
        }

        updateStockForOrder(order, StockOperation.DECREASE);

        order.changeStatus(OrderStatus.CONFIRMED);
        order = orderRepository.save(order);
        return OrderDto.Response.fromDomain(order);
    }

    @Transactional
    public OrderDto.Response cancelOrder(UUID orderId, String email){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException(OrderErrorCodes.ORDER_NOT_FOUND));
        getOrderAccess(email, order);
        if (order.getStatus() == OrderStatus.CANCELLED){
            return OrderDto.Response.fromDomain(order);
        }
        if (order.getStatus() == OrderStatus.SHIPPED){
            throw new DomainException(OrderErrorCodes.ORDER_ALREADY_SHIPPED);
        }
        if (order.getStatus() == OrderStatus.DELIVERED){
            throw new DomainException(OrderErrorCodes.ORDER_ALREADY_DELIVERED);
        }
        if (order.getStatus() == OrderStatus.PENDING){
            order.changeStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
        if (order.getStatus() == OrderStatus.CONFIRMED){
            returnStock(order);
            order.changeStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
        return OrderDto.Response.fromDomain(order);
    }

    private void returnStock(Order order){
        if (order == null){
            throw new DomainException(OrderErrorCodes.ORDER_NOT_FOUND);
        }
        if (order.getStatus() != OrderStatus.CONFIRMED){
            throw new DomainException(OrderErrorCodes.ORDER_STATUS_NOT_CONFIRMED);
        }
        updateStockForOrder(order, StockOperation.INCREASE);
    }

    private void updateStockForOrder(Order order, StockOperation operation) {
        Map<UUID, OrderItem> mapItems = order.getItems().stream().collect(Collectors.toMap(OrderItem::getProductId, Function.identity()));
        Set<UUID> productIds = mapItems.keySet();
        Map<UUID, Product> products = productRepository.findByIds(productIds);

        for (UUID productId:productIds){
            Product product = products.get(productId);
            OrderItem item = mapItems.get(productId);
            
            if (item == null){
                throw new DomainException(OrderErrorCodes.ORDER_ITEM_NOT_FOUND);
            }
            if (product == null) {
                throw new DomainException(ProductErrorCodes.PRODUCT_NOT_FOUND);
            }

            if (operation == StockOperation.DECREASE) {
                if (product.getInventory().getQuantity() < item.getQuantity()) {
                    throw new DomainException(ProductErrorCodes.INVENTORY_NOT_ENOUGH, ProductErrorCodes.INVENTORY_NOT_ENOUGH.getMessage() + " Product: " + product.getName());
                }
                product.decreaseStock(item.getQuantity());
            } else {
                product.increaseStock(item.getQuantity());
            }
            
            productRepository.save(product);
        }
    }

    private enum StockOperation {
        INCREASE, DECREASE
    }

}
