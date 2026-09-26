package com.campuscrate.repository;

import java.math.BigDecimal;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.campuscrate.model.FoodItem;
import com.campuscrate.model.FoodVendor;

@Repository
public class FoodRepository {
    private final JdbcTemplate jdbc;
    public FoodRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    private static final String VENDOR = "vendor_id, user_id, name, location, description, phone, image_url, active";
    private static final String ITEM = "food_item_id, vendor_id, name, description, price, image_url, available";

    public List<FoodVendor> findActiveVendors() { return jdbc.query("SELECT " + VENDOR + " FROM food_vendor WHERE active = TRUE ORDER BY name", (rs, n) -> vendor(rs)); }
    public List<FoodVendor> findAllVendors() { return jdbc.query("SELECT " + VENDOR + " FROM food_vendor ORDER BY vendor_id DESC", (rs, n) -> vendor(rs)); }
    public Optional<FoodVendor> findVendor(Long id) { return oneVendor("SELECT " + VENDOR + " FROM food_vendor WHERE vendor_id = ?", id); }
    public Optional<FoodVendor> findVendorByUser(Long userId) { return oneVendor("SELECT " + VENDOR + " FROM food_vendor WHERE user_id = ?", userId); }
    private Optional<FoodVendor> oneVendor(String sql, Object id) { return jdbc.query(sql, (rs, n) -> vendor(rs), id).stream().findFirst(); }
    private FoodVendor vendor(java.sql.ResultSet rs) throws java.sql.SQLException { return new FoodVendor(rs.getLong("vendor_id"), rs.getLong("user_id"), rs.getString("name"), rs.getString("location"), rs.getString("description"), rs.getString("phone"), rs.getString("image_url"), rs.getBoolean("active")); }

    public FoodVendor createVendor(FoodVendor value) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(c -> { var ps = c.prepareStatement("INSERT INTO food_vendor (user_id,name,location,description,phone,image_url,active) VALUES (?,?,?,?,?,?,TRUE)", Statement.RETURN_GENERATED_KEYS); ps.setLong(1,value.userId()); ps.setString(2,value.name()); ps.setString(3,value.location()); ps.setString(4,value.description()); ps.setString(5,value.phone()); ps.setString(6,value.imageUrl()); return ps; }, keys);
        return new FoodVendor(keys.getKey().longValue(), value.userId(), value.name(), value.location(), value.description(), value.phone(), value.imageUrl(), true);
    }
    public List<FoodItem> findAvailableItems(Long vendorId) { return items("SELECT " + ITEM + " FROM food_item WHERE vendor_id = ? AND available = TRUE ORDER BY food_item_id DESC", vendorId); }
    public List<FoodItem> findVendorItems(Long vendorId) { return items("SELECT " + ITEM + " FROM food_item WHERE vendor_id = ? ORDER BY food_item_id DESC", vendorId); }
    private List<FoodItem> items(String sql, Long vendorId) { return jdbc.query(sql, (rs,n) -> item(rs), vendorId); }
    public Optional<FoodItem> findItem(Long id) { return jdbc.query("SELECT " + ITEM + " FROM food_item WHERE food_item_id = ?", (rs,n) -> item(rs), id).stream().findFirst(); }
    private FoodItem item(java.sql.ResultSet rs) throws java.sql.SQLException { return new FoodItem(rs.getLong("food_item_id"),rs.getLong("vendor_id"),rs.getString("name"),rs.getString("description"),rs.getBigDecimal("price"),rs.getString("image_url"),rs.getBoolean("available")); }
    public FoodItem createItem(FoodItem value) { KeyHolder keys = new GeneratedKeyHolder(); jdbc.update(c -> { var ps=c.prepareStatement("INSERT INTO food_item (vendor_id,name,description,price,image_url,available) VALUES (?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS); ps.setLong(1,value.vendorId()); ps.setString(2,value.name()); ps.setString(3,value.description()); ps.setBigDecimal(4,value.price()); ps.setString(5,value.imageUrl()); ps.setBoolean(6,value.available()); return ps; },keys); return new FoodItem(keys.getKey().longValue(),value.vendorId(),value.name(),value.description(),value.price(),value.imageUrl(),value.available()); }
    public boolean updateItem(Long id, FoodItem value) { return jdbc.update("UPDATE food_item SET name=?, description=?, price=?, image_url=?, available=? WHERE food_item_id=?",value.name(),value.description(),value.price(),value.imageUrl(),value.available(),id)>0; }
    public boolean deleteItem(Long id) { return jdbc.update("DELETE FROM food_item WHERE food_item_id=?",id)>0; }

    public Long createOrder(Long vendorId, Long buyerId, BigDecimal total, String method, String paymentStatus, String deliveryLocation, String transactionId) { KeyHolder keys=new GeneratedKeyHolder(); jdbc.update(c -> {var ps=c.prepareStatement("INSERT INTO food_order (vendor_id,buyer_id,total_amount,payment_method,payment_status,order_status,delivery_location,transaction_id) VALUES (?,?,?,?,?,'PLACED',?,?)",Statement.RETURN_GENERATED_KEYS);ps.setLong(1,vendorId);ps.setLong(2,buyerId);ps.setBigDecimal(3,total);ps.setString(4,method);ps.setString(5,paymentStatus);ps.setString(6,deliveryLocation);ps.setString(7,transactionId);return ps;},keys);return keys.getKey().longValue(); }
    public void addOrderItem(Long orderId, FoodItem item, int quantity) { jdbc.update("INSERT INTO food_order_item (food_order_id,food_item_id,quantity,unit_price) VALUES (?,?,?,?)",orderId,item.foodItemId(),quantity,item.price()); }
    public void updatePayment(String transactionId, String paymentStatus) { jdbc.update("UPDATE food_order SET payment_status=?, order_status=CASE WHEN ?='PAID' THEN 'CONFIRMED' ELSE order_status END WHERE transaction_id=?",paymentStatus,paymentStatus,transactionId); }
    public List<com.campuscrate.dto.VendorOrderResponse> findVendorOrders(Long vendorId) {
        String sql = "SELECT o.food_order_id, u.name buyer_name, u.phone buyer_phone, o.delivery_location, o.total_amount, o.payment_method, o.payment_status, o.order_status, o.created_at FROM food_order o JOIN `USER` u ON u.user_id=o.buyer_id WHERE o.vendor_id=? ORDER BY o.food_order_id DESC";
        return jdbc.query(sql, (rs, n) -> new com.campuscrate.dto.VendorOrderResponse(rs.getLong("food_order_id"), rs.getString("buyer_name"), rs.getString("buyer_phone"), rs.getString("delivery_location"), rs.getBigDecimal("total_amount"), rs.getString("payment_method"), rs.getString("payment_status"), rs.getString("order_status"), rs.getTimestamp("created_at").toLocalDateTime(), findOrderItems(rs.getLong("food_order_id"))), vendorId);
    }
    private List<com.campuscrate.dto.VendorOrderItemResponse> findOrderItems(Long orderId) { return jdbc.query("SELECT f.name, oi.quantity, oi.unit_price FROM food_order_item oi JOIN food_item f ON f.food_item_id=oi.food_item_id WHERE oi.food_order_id=?", (rs,n) -> new com.campuscrate.dto.VendorOrderItemResponse(rs.getString("name"),rs.getInt("quantity"),rs.getBigDecimal("unit_price")), orderId); }
}
