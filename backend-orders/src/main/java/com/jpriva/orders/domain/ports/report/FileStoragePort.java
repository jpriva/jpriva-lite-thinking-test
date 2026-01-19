package com.jpriva.orders.domain.ports.report;

public interface FileStoragePort {
    String storeFile(byte[] content, String fileName);
}
