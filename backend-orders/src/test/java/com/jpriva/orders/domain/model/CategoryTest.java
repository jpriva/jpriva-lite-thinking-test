package com.jpriva.orders.domain.model;

import com.jpriva.orders.domain.exceptions.CategoryErrorCodes;
import com.jpriva.orders.domain.exceptions.DomainException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void shouldCreateCategorySuccessfully() {
        UUID companyId = UUID.randomUUID();
        String name = "Electronics";
        String description = "Electronic devices";

        Category category = Category.create(companyId, name, description);

        assertNotNull(category);
        assertNotNull(category.getId());
        assertEquals(companyId, category.getCompanyId());
        assertEquals(name, category.getName());
        assertEquals(description, category.getDescription());
    }

    @Test
    void shouldThrowExceptionWhenCreatingCategoryWithNullCompanyId() {
        DomainException exception = assertThrows(DomainException.class, () -> Category.builder()
                .id(UUID.randomUUID())
                .name("Name")
                .build()
        );
        assertEquals(CategoryErrorCodes.CATEGORY_COMPANY_ID_NULL.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenCreatingCategoryWithNullName() {
        DomainException exception = assertThrows(DomainException.class, () -> Category.create(
                UUID.randomUUID(),
                null,
                "Desc"
        ));
        assertEquals(CategoryErrorCodes.CATEGORY_NAME_NULL.getCode(), exception.getCode());
    }

    @Test
    void shouldUpdateCategoryDetails() {
        Category category = Category.create(UUID.randomUUID(), "Old Name", "Old Desc");

        category.changeName("New Name");
        category.changeDescription("New Desc");

        assertEquals("New Name", category.getName());
        assertEquals("New Desc", category.getDescription());
    }
}
