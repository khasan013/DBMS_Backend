package com.campuscrate.repository;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.campuscrate.dto.NotificationResponse;
@Repository public class NotificationRepository {
  private final JdbcTemplate jdbc; public NotificationRepository(JdbcTemplate jdbc){this.jdbc=jdbc;}
  public void admin(String title,String message){jdbc.update("INSERT INTO notification (recipient_type,recipient_id,title,message) VALUES ('ADMIN',NULL,?,?)",title,message);}
  public void user(Long userId,String title,String message){jdbc.update("INSERT INTO notification (recipient_type,recipient_id,title,message) VALUES ('USER',?,?,?)",userId,title,message);}
  public List<NotificationResponse> userList(Long userId){return list("recipient_type='USER' AND recipient_id=?",userId);}
  public List<NotificationResponse> adminList(){return list("recipient_type='ADMIN' AND title NOT LIKE 'New food order %'",new Object[]{});}
  public void markUserRead(Long userId){jdbc.update("UPDATE notification SET is_read=TRUE WHERE recipient_type='USER' AND recipient_id=? AND is_read=FALSE",userId);}
  public void markAdminRead(){jdbc.update("UPDATE notification SET is_read=TRUE WHERE recipient_type='ADMIN' AND is_read=FALSE");}
  private List<NotificationResponse> list(String where,Object... args){return jdbc.query("SELECT notification_id,title,message,is_read,created_at FROM notification WHERE "+where+" ORDER BY notification_id DESC LIMIT 100",(rs,n)->new NotificationResponse(rs.getLong(1),rs.getString(2),rs.getString(3),rs.getBoolean(4),rs.getTimestamp(5).toLocalDateTime()),args);}
}
