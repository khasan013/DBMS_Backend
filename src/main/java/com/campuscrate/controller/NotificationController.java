package com.campuscrate.controller;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.campuscrate.dto.NotificationResponse;
import com.campuscrate.repository.NotificationRepository;
import com.campuscrate.security.CurrentUser;
@RestController @RequestMapping("/api/notifications") public class NotificationController {
 private final NotificationRepository notifications; private final CurrentUser current;
 public NotificationController(NotificationRepository notifications,CurrentUser current){this.notifications=notifications;this.current=current;}
 @GetMapping("/me") public List<NotificationResponse> mine(){return notifications.userList(current.currentUserId());}
 @GetMapping("/admin") public List<NotificationResponse> admin(){current.currentAdminId();return notifications.adminList();}
 @PutMapping("/me/read-all") public void markMineRead(){notifications.markUserRead(current.currentUserId());}
 @PutMapping("/admin/read-all") public void markAdminRead(){current.currentAdminId();notifications.markAdminRead();}
}
