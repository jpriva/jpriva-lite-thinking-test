package com.jpriva.orders.domain.ports.notification;

public interface NotificationQueuePort {
    void sendProductReport(String email, String pdfUrl, String companyName);
}
