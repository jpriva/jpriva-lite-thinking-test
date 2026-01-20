package com.jpriva.orders;

import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.Network;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.mssqlserver.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.sqs.SqsClient;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    private final Network network = Network.newNetwork();

    @Bean
    @ServiceConnection(name = "mssql-server")
    @SuppressWarnings("resource")
    MSSQLServerContainer sqlServerContainer() {
        return new MSSQLServerContainer(DockerImageName.parse("mcr.microsoft.com/mssql/server:2022-latest"))
                .acceptLicense()
                .withPassword("StrongPass123!");
    }

    @Bean
    @SuppressWarnings("resource")
    LocalStackContainer localStackContainer() {
        return new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
                .withNetwork(network)
                .withNetworkAliases("localstack")
                .withServices("s3","sqs");
    }

    @Bean
    @Primary
    public S3Client testS3Client(LocalStackContainer localStack) {
        String endpoint = "http://" + localStack.getHost() + ":" + localStack.getMappedPort(4566);

        return S3Client.builder()
                .endpointOverride(java.net.URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())
                ))
                .region(Region.of(localStack.getRegion()))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .chunkedEncodingEnabled(false)
                        .build())
                .build();
    }

    @Bean
    @Primary
    public SqsClient testSqsClient(LocalStackContainer localStack) {
        String endpoint = "http://" + localStack.getHost() + ":" + localStack.getMappedPort(4566);

        return SqsClient.builder()
                .endpointOverride(java.net.URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())
                ))
                .region(Region.of(localStack.getRegion()))
                .build();
    }

    @Bean
    public FlywayMigrationStrategy cleanMigrateStrategy() {
        return flyway -> {
            flyway.clean();
            flyway.migrate();
        };
    }

}
