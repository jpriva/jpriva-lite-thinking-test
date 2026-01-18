package com.jpriva.orders.application.usecase;

import com.jpriva.orders.application.dto.OrderDto;
import com.jpriva.orders.domain.exceptions.*;
import com.jpriva.orders.domain.model.*;
import com.jpriva.orders.domain.model.vo.Currency;
import com.jpriva.orders.domain.model.vo.Money;
import com.jpriva.orders.domain.model.vo.OrderStatus;
import com.jpriva.orders.domain.model.vo.Role;
import com.jpriva.orders.domain.ports.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManageOrderUseCaseTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private CompanyRepository companyRepository;
    @InjectMocks private ManageOrderUseCase manageOrderUseCase;

    private User testUser;
    private User externalUser;
    private Company testCompany;
    private Client testClient;
    private Product testProduct;
    private Order testOrder;
    private OrderDto.CreateRequest createByUserRequest;
    private OrderDto.CreateRequest createByAdminRequest;
    private OrderDto.AddItemRequest addItemRequest;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        UUID externalUserId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        UUID externalClientId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID inventoryId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        testUser = User.builder().id(userId).email("admin@example.com").passwordHash("p").fullName("Admin User").phone("1").address("A").role(Role.ADMIN).createdAt(LocalDateTime.now()).build();
        externalUser = User.builder().id(externalUserId).email("user@example.com").passwordHash("p").fullName("External User").phone("2").address("B").role(Role.EXTERNAL).createdAt(LocalDateTime.now()).build();
        testCompany = Company.builder().id(companyId).name("TestCo").taxId("T1").address("Comp Address").phone("123").createdAt(LocalDateTime.now()).build();
        testClient = Client.builder().id(clientId).companyId(companyId).name(testUser.getFullName()).email("a@b.c").phone("1").address("A").createdAt(LocalDateTime.now()).build();

        Map<String, ProductPrice> prices = new HashMap<>();
        Money productPriceMoney = new Money(Currency.USD, BigDecimal.TEN);
        prices.put("USD", ProductPrice.create(productId, productPriceMoney));

        testProduct = Product.builder().id(productId).companyId(companyId).categoryId(categoryId).name("Test Product").sku("SKU-001").description("Desc").createdAt(LocalDateTime.now()).inventory(Inventory.builder().id(inventoryId).productId(productId).quantity(100).lastUpdated(LocalDateTime.now()).build()).prices(prices).build();
        testOrder = Order.builder().id(orderId).companyId(companyId).clientId(clientId).clientName(testClient.getName()).address(testClient.getAddress()).orderDate(LocalDateTime.now()).status(OrderStatus.PENDING).totalAmount(Money.zero(Currency.USD)).build();

        createByUserRequest = new OrderDto.CreateRequest(testCompany.getTaxId(), testClient.getId(), "USD");
        createByAdminRequest = new OrderDto.CreateRequest(testCompany.getTaxId(), testClient.getId(), "USD");
        addItemRequest = new OrderDto.AddItemRequest(productId, 5);
    }

    @Test
    void createOrderByUser_shouldFail_whenClientDoesNotExist() {
        when(companyRepository.findByTaxId(any(String.class))).thenReturn(Optional.of(testCompany));
        when(clientRepository.findById(any())).thenReturn(Optional.empty());
        DomainException ex = catchThrowableOfType(() -> manageOrderUseCase.createOrder(createByUserRequest), DomainException.class);
        assertThat(ex.getCode()).isEqualTo(ClientErrorCodes.CLIENT_NOT_FOUND.getCode());
    }
    
    @Test
    void createOrderByUser_shouldUseExistingClient_whenClientExists() {
        when(companyRepository.findByTaxId(any(String.class))).thenReturn(Optional.of(testCompany));
        when(clientRepository.findById(any())).thenReturn(Optional.of(testClient));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        OrderDto.Response result = manageOrderUseCase.createOrder(createByUserRequest);
        verify(clientRepository, never()).save(any(Client.class));
        verify(orderRepository, times(1)).save(any(Order.class));
        assertThat(result.clientId()).isEqualTo(testClient.getId());
    }

    @Test
    void createOrderByUser_shouldFail_whenCompanyNotFound() {
        when(companyRepository.findByTaxId(any(String.class))).thenReturn(Optional.empty());
        DomainException ex = catchThrowableOfType(() -> manageOrderUseCase.createOrder(createByUserRequest), DomainException.class);
        assertThat(ex.getCode()).isEqualTo(CompanyErrorCodes.COMPANY_NOT_FOUND.getCode());
    }

    @Test
    void createOrderByUser_shouldFail_whenClientNotFound() {
        when(companyRepository.findByTaxId(any(String.class))).thenReturn(Optional.of(testCompany));
        when(clientRepository.findById(any())).thenReturn(Optional.empty());
        DomainException ex = catchThrowableOfType(() -> manageOrderUseCase.createOrder(createByUserRequest), DomainException.class);
        assertThat(ex.getCode()).isEqualTo(ClientErrorCodes.CLIENT_NOT_FOUND.getCode());
    }

    @Test
    void createOrderByAdmin_shouldCreateOrder() {
        when(companyRepository.findByTaxId(any(String.class))).thenReturn(Optional.of(testCompany));
        when(clientRepository.findById(any())).thenReturn(Optional.of(testClient));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        OrderDto.Response result = manageOrderUseCase.createOrder(createByAdminRequest);
        verify(orderRepository, times(1)).save(any(Order.class));
        assertThat(result).isNotNull();
    }

    @Test
    void createOrderByAdmin_shouldFail_whenClientNotFound() {
        when(companyRepository.findByTaxId(any(String.class))).thenReturn(Optional.of(testCompany));
        when(clientRepository.findById(any())).thenReturn(Optional.empty());
        DomainException ex = catchThrowableOfType(() -> manageOrderUseCase.createOrder(createByAdminRequest), DomainException.class);
        assertThat(ex.getCode()).isEqualTo(ClientErrorCodes.CLIENT_NOT_FOUND.getCode());
    }

    @Test
    void addItem_shouldAddNewItem() {
        when(orderRepository.findById(any())).thenReturn(Optional.of(testOrder));
        when(productRepository.findById(any())).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        OrderDto.Response result = manageOrderUseCase.addItem(testOrder.getId(), addItemRequest, testUser.getEmail());
        assertThat(result.items()).hasSize(1);
    }

    @Test
    void addItem_shouldUpdateQuantity() {
        testOrder.addItem(OrderItem.create(testOrder.getId(), testProduct, 1, testProduct.getProductPrice(Currency.USD)));
        when(orderRepository.findById(any())).thenReturn(Optional.of(testOrder));
        when(productRepository.findById(any())).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        OrderDto.Response result = manageOrderUseCase.addItem(testOrder.getId(), addItemRequest, testUser.getEmail());
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().getFirst().quantity()).isEqualTo(addItemRequest.quantity());
    }
    
    @Test
    void addItem_shouldFail_whenOrderNotFound() {
        when(orderRepository.findById(any())).thenReturn(Optional.empty());
        DomainException ex = catchThrowableOfType(() -> manageOrderUseCase.addItem(testOrder.getId(), addItemRequest, testUser.getEmail()), DomainException.class);
        assertThat(ex.getCode()).isEqualTo(OrderErrorCodes.ORDER_NOT_FOUND.getCode());
    }

    @Test
    void addItem_shouldFail_whenOrderStatusNotPending() {
        testOrder.changeStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(any())).thenReturn(Optional.of(testOrder));
        DomainException ex = catchThrowableOfType(() -> manageOrderUseCase.addItem(testOrder.getId(), addItemRequest, testUser.getEmail()), DomainException.class);
        assertThat(ex.getCode()).isEqualTo(OrderErrorCodes.ORDER_STATUS_NOT_PENDING.getCode());
    }

    @Test
    void addItem_shouldFail_whenProductNotFound() {
        when(orderRepository.findById(any())).thenReturn(Optional.of(testOrder));
        when(productRepository.findById(any())).thenReturn(Optional.empty());
        DomainException ex = catchThrowableOfType(() -> manageOrderUseCase.addItem(testOrder.getId(), addItemRequest, testUser.getEmail()), DomainException.class);
        assertThat(ex.getCode()).isEqualTo(ProductErrorCodes.PRODUCT_NOT_FOUND.getCode());
    }

    @Test
    void addItem_shouldFail_whenInventoryNotEnough() {
        testProduct.decreaseStock(98);
        when(orderRepository.findById(any())).thenReturn(Optional.of(testOrder));
        when(productRepository.findById(any())).thenReturn(Optional.of(testProduct));
        DomainException ex = catchThrowableOfType(() -> manageOrderUseCase.addItem(testOrder.getId(), addItemRequest, testUser.getEmail()), DomainException.class);
        assertThat(ex.getCode()).isEqualTo(ProductErrorCodes.INVENTORY_NOT_ENOUGH.getCode());
    }



    @Test
    void removeItem_shouldRemoveItem() {
        OrderItem item = OrderItem.create(testOrder.getId(), testProduct, 1, testProduct.getProductPrice(Currency.USD));
        testOrder.addItem(item);
        assertThat(testOrder.getItems()).hasSize(1);
        
        when(orderRepository.findById(any())).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrderDto.Response result = manageOrderUseCase.removeItem(testOrder.getId(), item.getId(), testUser.getEmail());
        assertThat(result.items()).isEmpty();
    }

    @Test
    void getOrder_shouldReturnOrder() {
        testOrder.changeStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(any())).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrderDto.Response result = manageOrderUseCase.getOrder(testOrder.getId(), testUser.getEmail());
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(OrderStatus.CONFIRMED.name());
    }

    @Test
    void confirmOrder_shouldConfirmAndDecreaseStock() {
        int initialStock = testProduct.getInventory().getQuantity();
        int quantityOrdered = addItemRequest.quantity();
        testOrder.addItem(OrderItem.create(testOrder.getId(), testProduct, quantityOrdered, testProduct.getProductPrice(Currency.USD)));
        
        when(orderRepository.findById(any())).thenReturn(Optional.of(testOrder));
        when(productRepository.findByIds(anySet())).thenReturn(Map.of(testProduct.getId(), testProduct));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrderDto.Response result = manageOrderUseCase.confirmOrder(testOrder.getId(), testUser.getEmail());
        
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        assertThat(productCaptor.getValue().getInventory().getQuantity()).isEqualTo(initialStock - quantityOrdered);
        assertThat(result.status()).isEqualTo(OrderStatus.CONFIRMED.name());
    }
    
    @Test
    void confirmOrder_shouldFail_whenNoItems() {
        when(orderRepository.findById(any())).thenReturn(Optional.of(testOrder));
        DomainException ex = catchThrowableOfType(() -> manageOrderUseCase.confirmOrder(testOrder.getId(), testUser.getEmail()), DomainException.class);
        assertThat(ex.getCode()).isEqualTo(OrderErrorCodes.ORDER_NO_ITEM_ADDED.getCode());
    }

    @Test
    void cancelOrder_shouldCancelPendingOrder() {
        when(orderRepository.findById(any())).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrderDto.Response result = manageOrderUseCase.cancelOrder(testOrder.getId(), testUser.getEmail());

        verify(productRepository, never()).save(any());
        assertThat(result.status()).isEqualTo(OrderStatus.CANCELLED.name());
    }

    @Test
    void cancelOrder_shouldCancelConfirmedOrderAndRestock() {
        testOrder.changeStatus(OrderStatus.CONFIRMED);
        int initialStock = testProduct.getInventory().getQuantity();
        int quantityOrdered = 5;
        testOrder.addItem(OrderItem.create(testOrder.getId(), testProduct, quantityOrdered, testProduct.getProductPrice(Currency.USD)));

        when(orderRepository.findById(any())).thenReturn(Optional.of(testOrder));
        when(productRepository.findByIds(anySet())).thenReturn(Map.of(testProduct.getId(), testProduct));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrderDto.Response result = manageOrderUseCase.cancelOrder(testOrder.getId(), testUser.getEmail());

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        assertThat(productCaptor.getValue().getInventory().getQuantity()).isEqualTo(initialStock + quantityOrdered);
        assertThat(result.status()).isEqualTo(OrderStatus.CANCELLED.name());
    }

    @Test
    void cancelOrder_shouldFail_ifShipped() {
        testOrder.changeStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById(any())).thenReturn(Optional.of(testOrder));
        DomainException ex = catchThrowableOfType(() -> manageOrderUseCase.cancelOrder(testOrder.getId(), testUser.getEmail()), DomainException.class);
        assertThat(ex.getCode()).isEqualTo(OrderErrorCodes.ORDER_ALREADY_SHIPPED.getCode());
    }
}
