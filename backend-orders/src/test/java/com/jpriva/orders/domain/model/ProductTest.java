package com.jpriva.orders.domain.model;

import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.ProductErrorCodes;
import com.jpriva.orders.domain.model.vo.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateProductSuccessfully() {
        UUID companyId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        String name = "Test Product";
        String sku = "TEST-SKU-001";
        String description = "Test Description";

        Product product = Product.create(companyId, categoryId, name, sku, description);

        assertNotNull(product);
        assertNotNull(product.getId());
        assertEquals(companyId, product.getCompanyId());
        assertEquals(categoryId, product.getCategoryId());
        assertEquals(name, product.getName());
        assertEquals(sku, product.getSku());
        assertEquals(description, product.getDescription());
        assertNotNull(product.getInventory());
        assertEquals(0, product.getInventory().getQuantity());
        assertTrue(product.getPrices().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductWithNullCompanyId() {
        DomainException exception = assertThrows(DomainException.class, () -> Product.builder()
                .id(UUID.randomUUID())
                .name("Name")
                .sku("SKU")
                .build()
        );

        assertEquals(ProductErrorCodes.PRODUCT_COMPANY_ID_NULL.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductWithNullName() {
        DomainException exception = assertThrows(DomainException.class, () -> Product.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                "SKU",
                "Desc"
        ));

        assertEquals(ProductErrorCodes.PRODUCT_NAME_NULL.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductWithNullSku() {
        DomainException exception = assertThrows(DomainException.class, () -> Product.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Name",
                null,
                "Desc"
        ));

        assertEquals(ProductErrorCodes.PRODUCT_SKU_NULL.getCode(), exception.getCode());
    }

    @Test
    void shouldAddAndUpdatePrice() {
        Product product = createValidProduct();
        Money usdPrice = Money.fromString("USD", "100.00");
        Money eurPrice = Money.fromString("EUR", "90.00");

        product.changePrice(usdPrice);
        assertTrue(product.getPrices().containsKey("USD"));
        assertEquals(new BigDecimal("100.00"), product.getPrices().get("USD").getPrice().amount());

        product.changePrice(eurPrice);
        assertTrue(product.getPrices().containsKey("EUR"));

        Money newUsdPrice = Money.fromString("USD", "120.00");
        product.changePrice(newUsdPrice);
        assertEquals(new BigDecimal("120.00"), product.getPrices().get("USD").getPrice().amount());
    }

    @Test
    void shouldManageInventoryStock() {
        Product product = createValidProduct();
        assertEquals(0, product.getInventory().getQuantity());

        product.increaseStock(10);
        assertEquals(10, product.getInventory().getQuantity());

        product.increaseStock(5);
        assertEquals(15, product.getInventory().getQuantity());

        product.decreaseStock(3);
        assertEquals(12, product.getInventory().getQuantity());
    }

    @Test
    void shouldThrowExceptionWhenIncreasingStockWithNegativeAmount() {
        Product product = createValidProduct();

        DomainException exception = assertThrows(DomainException.class, () -> product.increaseStock(-5));
        assertEquals(ProductErrorCodes.INVENTORY_AMOUNT_NEGATIVE.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenDecreasingStockInsufficiently() {
        Product product = createValidProduct();
        product.increaseStock(5);

        DomainException exception = assertThrows(DomainException.class, () -> product.decreaseStock(10));
        assertEquals(ProductErrorCodes.INVENTORY_QUANTITY_NEGATIVE.getCode(), exception.getCode());
    }
    
    @Test
    void shouldCreateProductPriceSuccessfully() {
        UUID productId = UUID.randomUUID();
        Money price = Money.fromString("USD", "50.00");
        
        ProductPrice productPrice = ProductPrice.create(productId, price);
        
        assertNotNull(productPrice);
        assertEquals(productId, productPrice.getProductId());
        assertEquals(price, productPrice.getPrice());
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductPriceWithNullPrice() {
        DomainException exception = assertThrows(DomainException.class, () -> ProductPrice.create(
                UUID.randomUUID(),
                null
        ));
        assertEquals(ProductErrorCodes.PRODUCT_PRICE_NULL.getCode(), exception.getCode());
    }

    @Test
    void shouldCreateInventorySuccessfully() {
        UUID productId = UUID.randomUUID();
        
        Inventory inventory = Inventory.create(productId, 10);
        
        assertNotNull(inventory);
        assertEquals(productId, inventory.getProductId());
        assertEquals(10, inventory.getQuantity());
    }

    @Test
    void shouldThrowExceptionWhenCreatingInventoryWithNegativeQuantity() {
        DomainException exception = assertThrows(DomainException.class, () -> Inventory.create(
                UUID.randomUUID(),
                -1
        ));
        assertEquals(ProductErrorCodes.INVENTORY_QUANTITY_NEGATIVE.getCode(), exception.getCode());
    }

    private Product createValidProduct() {
        return Product.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Test Product",
                "SKU-123",
                "Description"
        );
    }
}
