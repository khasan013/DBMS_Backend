package com.campuscrate.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.campuscrate.dto.*;
import com.campuscrate.security.CurrentUser;
import com.campuscrate.service.FoodService;

@RestController
public class FoodController {
    private final FoodService food; private final CurrentUser current; private final String foodUrl;
    public FoodController(FoodService food, CurrentUser current, @Value("${sslcommerz.frontend-food-url}") String foodUrl){this.food=food;this.current=current;this.foodUrl=foodUrl;}
    @GetMapping("/api/food/vendors") public List<VendorResponse> vendors(){return food.publicVendors();}
    @GetMapping("/api/vendors/me") public VendorResponse mine(){return food.mine(current.currentUserId());}
    @PostMapping("/api/vendors/me/items") public ResponseEntity<FoodItemResponse> createItem(@Valid @RequestBody FoodItemRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(food.createItem(current.currentUserId(),r));}
    @PutMapping("/api/vendors/me/items/{itemId}") public FoodItemResponse updateItem(@PathVariable Long itemId,@Valid @RequestBody FoodItemRequest r){return food.updateItem(current.currentUserId(),itemId,r);}
    @DeleteMapping("/api/vendors/me/items/{itemId}") public ResponseEntity<Void> deleteItem(@PathVariable Long itemId){food.deleteItem(current.currentUserId(),itemId);return ResponseEntity.noContent().build();}
    @PostMapping("/api/food/orders") public FoodCheckoutResponse checkout(@Valid @RequestBody FoodOrderRequest r){return food.checkout(current.currentUserId(),r);}
    @PostMapping("/api/food/payments/sslcommerz/success") public ResponseEntity<Void> paymentSuccess(@RequestParam Map<String,String> values){food.completeOnlinePayment(values.get("tran_id"),values.get("val_id"),true);return redirect();}
    @PostMapping("/api/food/payments/sslcommerz/fail") public ResponseEntity<Void> paymentFail(@RequestParam Map<String,String> values){food.completeOnlinePayment(values.get("tran_id"),null,false);return redirect();}
    @PostMapping("/api/food/payments/sslcommerz/cancel") public ResponseEntity<Void> paymentCancel(@RequestParam Map<String,String> values){food.completeOnlinePayment(values.get("tran_id"),null,false);return redirect();}
    private ResponseEntity<Void> redirect(){return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(foodUrl)).build();}
}
