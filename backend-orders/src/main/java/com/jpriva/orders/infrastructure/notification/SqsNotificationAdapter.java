package com.jpriva.orders.infrastructure.notification;

import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.NotificationErrorCodes;
import com.jpriva.orders.domain.ports.notification.NotificationQueuePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SqsNotificationAdapter implements NotificationQueuePort {
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;
    @Override
    public void sendProductReport(String email, String fileName, String companyName) {
        try {
            Map<String, String> payload = Map.of(
                    "email", email,
                    "fileName", fileName,
                    "companyName", companyName
            );

            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(objectMapper.writeValueAsString(payload))
                    .build();

            sqsClient.sendMessage(sendMsgRequest);
        } catch (Exception e) {
            throw new DomainException(NotificationErrorCodes.NOTIFICATION_ERROR_SERIALIZATION, e);
        }
    }
}
