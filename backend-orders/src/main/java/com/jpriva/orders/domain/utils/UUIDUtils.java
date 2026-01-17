package com.jpriva.orders.domain.utils;

import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.UUIDErrorCodes;

import java.util.UUID;

public class UUIDUtils {
    private UUIDUtils() {
    }

    public static String uuidToString(UUID id) {
        return id.toString();
    }

    public static UUID stringToUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new DomainException(UUIDErrorCodes.INVALID_UUID_FORMAT, "Invalid UUID format (" + id + ")", e);
        }
    }


    public static boolean isValid(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            throw new DomainException(UUIDErrorCodes.INVALID_UUID_FORMAT, "Invalid UUID format (" + uuid + ")", e);
        }
    }
}
