package com.jpriva.orders.infrastructure.aws;

import com.jpriva.orders.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class AwsInfrastructureTest {

    @Autowired
    private S3Client s3Client;

    @Autowired
    private SqsClient sqsClient;

    @Value("${aws.s3.bucket:test-bucket}")
    private String bucketName;

    @Value("${aws.sqs.queue-name:test-queue}")
    private String queueName;

    private static String queueUrl;

    @BeforeAll
    static void setup(@Autowired S3Client s3,
                      @Autowired SqsClient sqs,
                      @Value("${aws.s3.bucket:test-bucket}") String bucket,
                      @Value("${aws.sqs.queue-name:test-queue}") String queue) {

        try {
            s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        } catch (Exception e) {
            System.out.println("Bucket already exists or error creating bucket: " + e.getMessage());
        }

        try {
            queueUrl = sqs.createQueue(CreateQueueRequest.builder().queueName(queue).build()).queueUrl();
        } catch (Exception e) {
            queueUrl = sqs.getQueueUrl(b -> b.queueName(queue)).queueUrl();
        }
    }

    @Test
    void shouldUploadAndDownloadFromS3() {
        String key = "test_file_" + UUID.randomUUID() + ".txt";
        String content = "Test content";

        s3Client.putObject(
                PutObjectRequest.builder().bucket(bucketName).key(key).build(),
                RequestBody.fromString(content)
        );

        String downloadedContent = s3Client.getObject(
                GetObjectRequest.builder().bucket(bucketName).key(key).build(),
                ResponseTransformer.toBytes()
        ).asUtf8String();

        assertThat(downloadedContent).isEqualTo(content);
    }

    @Test
    void shouldSendAndReceiveSqsMessage() {
        String messageBody = "Order created whit ID: " + UUID.randomUUID();

        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build());

        var response = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(1)
                .waitTimeSeconds(2)
                .build());

        assertThat(response.messages()).isNotEmpty();
        assertThat(response.messages().getFirst().body()).isEqualTo(messageBody);
    }
}