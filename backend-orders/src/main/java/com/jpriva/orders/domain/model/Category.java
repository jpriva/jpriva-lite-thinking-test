package com.jpriva.orders.domain.model;

import com.jpriva.orders.domain.exceptions.CategoryErrorCodes;
import com.jpriva.orders.domain.exceptions.DomainException;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Category {
    private final UUID id;
    private final UUID companyId;
    private String name;
    private String description;

    @Builder
    public Category(UUID id, UUID companyId, String name, String description) {
        if (id == null) {
            throw new DomainException(CategoryErrorCodes.CATEGORY_ID_NULL);
        }
        if (companyId == null) {
            throw new DomainException(CategoryErrorCodes.CATEGORY_COMPANY_ID_NULL);
        }
        
        this.id = id;
        this.companyId = companyId;
        changeName(name);
        changeDescription(description);
    }

    public static Category create(UUID companyId, String name, String description) {
        return Category.builder()
                .id(UUID.randomUUID())
                .companyId(companyId)
                .name(name)
                .description(description)
                .build();
    }

    public static Category fromPersistence(UUID id, UUID companyId, String name, String description) {
        try {
            return Category.builder()
                    .id(id)
                    .companyId(companyId)
                    .name(name)
                    .description(description)
                    .build();
        } catch (DomainException e) {
            throw new DomainException(CategoryErrorCodes.CATEGORY_PERSISTENCE_ERROR, e);
        }
    }

    public void changeName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException(CategoryErrorCodes.CATEGORY_NAME_NULL);
        }
        this.name = name.trim();
    }

    public void changeDescription(String description) {
        if (description == null || description.isBlank()) {
            this.description = null;
            return;
        }
        this.description = description.trim();
    }
}
