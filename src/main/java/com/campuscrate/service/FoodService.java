package com.campuscrate.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.campuscrate.dto.*;
import com.campuscrate.exception.InvalidRequestException;
import com.campuscrate.model.FoodItem;
import com.campuscrate.model.FoodVendor;
import com.campuscrate.model.User;
import com.campuscrate.repository.AdminRepository;
import com.campuscrate.repository.FoodRepository;
import com.campuscrate.repository.UserRepository;

@Service
public class FoodService {
    private final FoodRepository food; private final UserRepository users; private final AdminRepository admins; private final PasswordEncoder encoder; private final SslCommerzService ssl;
    public FoodService(FoodRepository food, UserRepository users, AdminRepository admins, PasswordEncoder encoder, SslCommerzService ssl) { this.food=food;this.users=users;this.admins=admins;this.encoder=encoder;this.ssl=ssl; }
    public List<VendorResponse> publicVendors() { return food.findActiveVendors().stream().map(v -> vendor(v,food.findAvailableItems(v.vendorId()))).toList(); }
    public List<VendorResponse> adminVendors() { return food.findAllVendors().stream().map(v -> vendor(v,food.findVendorItems(v.vendorId()))).toList(); }
    @Transactional public VendorResponse createVendor(VendorCreateRequest r) { if(users.existsByStudentId(r.loginId())||users.existsByEmail(r.email())||admins.findByEmail(r.email()).isPresent()) throw new InvalidRequestException("The vendor login ID or email is already in use."); User user=users.create(new User(null,r.loginId().trim(),r.name().trim(),r.email().trim(),true,false,encoder.encode(r.password()),r.phone().trim(),r.imageUrl())); FoodVendor vendor=food.createVendor(new FoodVendor(null,user.getUserId(),r.name().trim(),r.location().trim(),r.description(),r.phone().trim(),r.imageUrl(),true)); return vendor(vendor,List.of()); }
    public VendorResponse mine(Long userId) { FoodVendor v=food.findVendorByUser(userId).orElseThrow(()->new InvalidRequestException("This account is not a food vendor.")); return vendor(v,food.findVendorItems(v.vendorId())); }
    @Transactional public FoodItemResponse createItem(Long userId, FoodItemRequest r) { FoodVendor v=owner(userId); return item(food.createItem(new FoodItem(null,v.vendorId(),r.name().trim(),r.description(),r.price(),r.imageUrl(),r.available()==null||r.available()))); }
    @Transactional public FoodItemResponse updateItem(Long userId, Long itemId, FoodItemRequest r) { FoodVendor v=owner(userId); FoodItem old=food.findItem(itemId).orElseThrow(()->new InvalidRequestException("Food item not found.")); if(!old.vendorId().equals(v.vendorId())) throw new InvalidRequestException("You can update only your own food items."); FoodItem next=new FoodItem(itemId,v.vendorId(),r.name().trim(),r.description(),r.price(),r.imageUrl(),r.available()==null?old.available():r.available()); food.updateItem(itemId,next);return item(next); }
    public void deleteItem(Long userId,Long itemId) { FoodVendor v=owner(userId); FoodItem i=food.findItem(itemId).orElseThrow(()->new InvalidRequestException("Food item not found.")); if(!i.vendorId().equals(v.vendorId()))throw new InvalidRequestException("You can delete only your own food items."); food.deleteItem(itemId); }
    @Transactional public FoodCheckoutResponse checkout(Long buyerId, FoodOrderRequest r) { if(!"COD".equalsIgnoreCase(r.paymentMethod())&&!"ONLINE".equalsIgnoreCase(r.paymentMethod()))throw new InvalidRequestException("Payment method must be COD or ONLINE."); FoodVendor v=food.findVendor(r.vendorId()).filter(FoodVendor::active).orElseThrow(()->new InvalidRequestException("Food vendor is unavailable.")); BigDecimal total=BigDecimal.ZERO; java.util.List<FoodItem> items=new java.util.ArrayList<>(); for(FoodOrderLineRequest line:r.items()){ FoodItem i=food.findItem(line.foodItemId()).filter(FoodItem::available).orElseThrow(()->new InvalidRequestException("A food item is unavailable.")); if(!i.vendorId().equals(v.vendorId()))throw new InvalidRequestException("All foods must belong to the selected vendor."); items.add(i); total=total.add(i.price().multiply(BigDecimal.valueOf(line.quantity()))); } String method=r.paymentMethod().toUpperCase(); String transaction=method.equals("ONLINE")?"FOOD-"+System.currentTimeMillis()+"-"+buyerId:null; Long orderId=food.createOrder(v.vendorId(),buyerId,total,method,method.equals("COD")?"PENDING":"INITIATED",transaction); for(int x=0;x<items.size();x++)food.addOrderItem(orderId,items.get(x),r.items().get(x).quantity()); if(method.equals("COD"))return new FoodCheckoutResponse(orderId,total,method,"PENDING","PLACED",null); User buyer=users.findById(buyerId).orElseThrow(()->new InvalidRequestException("Buyer not found.")); String gateway=ssl.createSession(transaction,total,buyer.getName(),buyer.getEmail(),buyer.getPhone()); return new FoodCheckoutResponse(orderId,total,method,"INITIATED","PLACED",gateway); }
    public void completeOnlinePayment(String transactionId,String validationId,boolean successful){ food.updatePayment(transactionId,successful&&ssl.validate(validationId)?"PAID":"FAILED"); }
    private FoodVendor owner(Long userId){return food.findVendorByUser(userId).orElseThrow(()->new InvalidRequestException("This account is not a food vendor."));}
    private VendorResponse vendor(FoodVendor v,List<FoodItem> items){return new VendorResponse(v.vendorId(),v.userId(),v.name(),v.location(),v.description(),v.phone(),v.imageUrl(),v.active(),items.stream().map(this::item).toList());}
    private FoodItemResponse item(FoodItem i){return new FoodItemResponse(i.foodItemId(),i.vendorId(),i.name(),i.description(),i.price(),i.imageUrl(),i.available());}
}
