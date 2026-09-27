package com.campuscrate.dto;
import java.time.LocalDateTime;
public record NotificationResponse(Long notificationId, String title, String message, boolean read, LocalDateTime createdAt) { }
