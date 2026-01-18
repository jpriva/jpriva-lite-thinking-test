package com.jpriva.orders.application.usecase;

import com.jpriva.orders.application.dto.ProductDto;
import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.ProductErrorCodes;
import com.jpriva.orders.domain.model.Company;
import com.jpriva.orders.domain.model.Product;
import com.jpriva.orders.domain.ports.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ManageProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ManageProductUseCase manageProductUseCase;

    private ProductDto.CreateRequest request;

    @BeforeEach
    void setUp() {
        Company testCompany = Company.builder()
                .id(UUID.randomUUID())
                .name("TestCo")
                .taxId("T1")
                .address("Comp Address")
                .phone("123")
                .build();

        request = new ProductDto.CreateRequest(
                testCompany.getId(),
                UUID.randomUUID(), // categoryId
                "New Product",
                "SKU-NEW",
                "Description for new product"
        );
    }

    @Test
    void createProduct_shouldCreateAndSaveProduct() {
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductDto.Response result = manageProductUseCase.createProduct(request);

        verify(productRepository).save(any(Product.class));
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(request.name());
        assertThat(result.sku()).isEqualTo(request.sku());
    }

    @Test
    void getProduct_shouldReturnProduct_whenFound() {
        Product product = request.toDomain(); // Create a product from the request
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        ProductDto.Response result = manageProductUseCase.getProduct(product.getId());

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(product.getId());
        assertThat(result.name()).isEqualTo(product.getName());
    }

    @Test
    void getProduct_shouldThrowException_whenNotFound() {
        UUID nonExistentProductId = UUID.randomUUID();
        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        DomainException ex = catchThrowableOfType(
                () -> manageProductUseCase.getProduct(nonExistentProductId),
                DomainException.class
        );
        assertThat(ex).isNotNull();
        assertThat(ex.getCode()).isEqualTo(ProductErrorCodes.PRODUCT_ID_NULL.getCode());
    }

    @Test
    void updatePrice_shouldUpdateProductPrice() {
        Product product = request.toDomain();
        ProductDto.UpdatePriceRequest updateRequest = new ProductDto.UpdatePriceRequest(BigDecimal.valueOf(15.00), "USD");

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductDto.Response result = manageProductUseCase.updatePrice(product.getId(), updateRequest);

        verify(productRepository).save(any(Product.class));
        assertThat(result).isNotNull();
        assertThat(result.prices()).hasEntrySatisfying("USD", price ->
                assertThat(price).isEqualByComparingTo(BigDecimal.valueOf(15.00)));
    }

    @Test
    void updatePrice_shouldThrowException_whenProductNotFound() {
        UUID nonExistentProductId = UUID.randomUUID();
        ProductDto.UpdatePriceRequest updateRequest = new ProductDto.UpdatePriceRequest(BigDecimal.valueOf(15.00), "USD");
        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        DomainException ex = catchThrowableOfType(
                () -> manageProductUseCase.updatePrice(nonExistentProductId, updateRequest),
                DomainException.class
        );
        assertThat(ex).isNotNull();
        assertThat(ex.getCode()).isEqualTo(ProductErrorCodes.PRODUCT_ID_NULL.getCode());
    }

    @Test
    void increaseStock_shouldIncreaseProductStock() {
        Product product = request.toDomain();
        int initialStock = product.getInventory().getQuantity();
        int amountToIncrease = 10;

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductDto.Response result = manageProductUseCase.increaseStock(product.getId(), amountToIncrease);

        verify(productRepository).save(any(Product.class));
        assertThat(result).isNotNull();
        assertThat(result.stockQuantity()).isEqualTo(initialStock + amountToIncrease);
    }

    @Test
    void increaseStock_shouldThrowException_whenProductNotFound() {
        UUID nonExistentProductId = UUID.randomUUID();
        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        DomainException ex = catchThrowableOfType(
                () -> manageProductUseCase.increaseStock(nonExistentProductId, 10),
                DomainException.class
        );
        assertThat(ex).isNotNull();
        assertThat(ex.getCode()).isEqualTo(ProductErrorCodes.PRODUCT_ID_NULL.getCode());
    }

    @Test
    void increaseStock_shouldThrowException_whenAmountIsZeroOrNegative() {
        Product product = request.toDomain();
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        DomainException ex = catchThrowableOfType(
                () -> manageProductUseCase.increaseStock(product.getId(), 0),
                DomainException.class
        );
        assertThat(ex).isNotNull();
        assertThat(ex.getCode()).isEqualTo(ProductErrorCodes.INVENTORY_AMOUNT_NEGATIVE.getCode());

        ex = catchThrowableOfType(
                () -> manageProductUseCase.increaseStock(product.getId(), -5),
                DomainException.class
        );
        assertThat(ex).isNotNull();
        assertThat(ex.getCode()).isEqualTo(ProductErrorCodes.INVENTORY_AMOUNT_NEGATIVE.getCode());
    }
}
