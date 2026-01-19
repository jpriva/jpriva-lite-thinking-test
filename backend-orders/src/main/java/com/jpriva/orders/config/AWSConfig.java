package com.jpriva.orders.config;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.net.URI;

@Configuration
public class AWSConfig {

    @Value("${aws.region:us-east-1}")
    private String region;

    @Value("${aws.s3.endpoint:http://localhost:4566}")
    private String endpoint;

    @Value("${aws.credentials.access-key:test}")
    private String accessKey;

    @Value("${aws.credentials.secret-key:test}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(getCredentialsProvider())
                .forcePathStyle(true)
                .build();
    }

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    private StaticCredentialsProvider getCredentialsProvider() {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
        );
    }
}