package com.jpriva.orders.domain.model;

import com.jpriva.orders.domain.exceptions.CompanyErrorCodes;
import com.jpriva.orders.domain.exceptions.DomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompanyTest {

    @Test
    void shouldCreateCompanySuccessfully() {
        String name = "Test Corp";
        String taxId = "123-456-789";
        String address = "Avenue 123";
        String phone = "555-0000";

        Company company = Company.create(name, taxId, address, phone);

        assertNotNull(company);
        assertNotNull(company.getId());
        assertEquals(name, company.getName());
        assertEquals(taxId, company.getTaxId());
        assertEquals(address, company.getAddress());
        assertEquals(phone, company.getPhone());
    }

    @Test
    void shouldThrowExceptionWhenCreatingCompanyWithNullName() {
        DomainException exception = assertThrows(DomainException.class, () -> Company.create(
                null,
                "TAX-1",
                "Addr",
                "Phone"
        ));
        assertEquals(CompanyErrorCodes.COMPANY_NAME_NULL.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenCreatingCompanyWithNullTaxId() {
        DomainException exception = assertThrows(DomainException.class, () -> Company.create(
                "Name",
                null,
                "Addr",
                "Phone"
        ));
        assertEquals(CompanyErrorCodes.COMPANY_TAX_ID_NULL.getCode(), exception.getCode());
    }

    @Test
    void shouldUpdateCompanyDetails() {
        Company company = Company.create("Old Name", "TAX-OLD", "Old Addr", "Old Phone");

        company.changeName("New Name");
        company.changeTaxId("TAX-NEW");
        company.changeAddress("New Addr");
        company.changePhone("New Phone");

        assertEquals("New Name", company.getName());
        assertEquals("TAX-NEW", company.getTaxId());
        assertEquals("New Addr", company.getAddress());
        assertEquals("New Phone", company.getPhone());
    }
}
