package com.jpriva.orders.infrastructure.storage;

import com.jpriva.orders.domain.ports.report.FileStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class S3StorageAdapter implements FileStoragePort {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    public String storeFile(byte[] content, String fileName) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType("application/pdf")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content));

        return fileName;
    }
}
