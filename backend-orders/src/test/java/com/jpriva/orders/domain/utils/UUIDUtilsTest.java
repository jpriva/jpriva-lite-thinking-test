package com.jpriva.orders.domain.utils;

import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.UUIDErrorCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UUIDUtilsTest {

    @Test
    void shouldConvertUUIDToString() {
        UUID uuid = UUID.randomUUID();
        String result = UUIDUtils.uuidToString(uuid);
        assertEquals(uuid.toString(), result);
    }

    @Test
    void shouldConvertStringToUUID() {
        String uuidStr = "550e8400-e29b-41d4-a716-446655440000";
        UUID result = UUIDUtils.stringToUUID(uuidStr);
        assertEquals(uuidStr, result.toString());
    }

    @Test
    void shouldThrowExceptionWhenConvertingInvalidStringToUUID() {
        String invalidUuid = "invalid-uuid";
        DomainException exception = assertThrows(DomainException.class, () -> UUIDUtils.stringToUUID(invalidUuid));
        assertEquals(UUIDErrorCodes.INVALID_UUID_FORMAT.getCode(), exception.getCode());
        assertTrue(exception.getMessage().contains(invalidUuid));
    }

    @Test
    void shouldReturnTrueWhenUUIDIsValid() {
        String validUuid = UUID.randomUUID().toString();
        assertTrue(UUIDUtils.isValid(validUuid));
    }

    @Test
    void shouldThrowExceptionWhenUUIDIsInvalid() {
        // Note: The current implementation throws DomainException instead of returning false
        String invalidUuid = "not-a-uuid";
        DomainException exception = assertThrows(DomainException.class, () -> UUIDUtils.isValid(invalidUuid));
        assertEquals(UUIDErrorCodes.INVALID_UUID_FORMAT.getCode(), exception.getCode());
    }
}
