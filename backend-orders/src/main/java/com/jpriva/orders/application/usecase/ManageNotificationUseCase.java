package com.jpriva.orders.application.usecase;

import com.jpriva.orders.application.dto.InventoryData;
import com.jpriva.orders.domain.ports.notification.NotificationQueuePort;
import com.jpriva.orders.domain.ports.report.FileStoragePort;
import com.jpriva.orders.domain.ports.report.ReportGeneratorPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManageNotificationUseCase {

    public final ManageProductUseCase productUseCase;
    private final ReportGeneratorPort reportGenerator;
    private final FileStoragePort fileStorage;
    private final NotificationQueuePort notificationQueue;

    public void sendInventoryToEmail(String taxId, String email) {
        InventoryData data = productUseCase.fetchInventoryData(taxId);

        byte[] pdfReport = reportGenerator.generateProductReport(data.products());

        String uniqueFileName = String.format("%s_inv_%s.pdf",
                data.sanitizedName(),
                UUID.randomUUID());

        String storedFileName = fileStorage.storeFile(pdfReport, uniqueFileName);

        notificationQueue.sendProductReport(email, storedFileName, data.originalName());
    }
}
