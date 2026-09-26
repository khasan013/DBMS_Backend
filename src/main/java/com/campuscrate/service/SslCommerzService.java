package com.campuscrate.service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.campuscrate.exception.InvalidRequestException;

@Service
public class SslCommerzService {
    private final String storeId, storePassword, callbackBase;
    private final boolean live;
    private final ObjectMapper json;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
    public SslCommerzService(@Value("${sslcommerz.store-id}") String storeId, @Value("${sslcommerz.store-password}") String storePassword,
            @Value("${sslcommerz.live:false}") boolean live, @Value("${sslcommerz.callback-base-url}") String callbackBase, ObjectMapper json) {
        this.storeId=storeId; this.storePassword=storePassword; this.live=live; this.callbackBase=callbackBase == null ? "" : callbackBase.replaceAll("/$", ""); this.json=json;
    }
    public String createSession(String transactionId, BigDecimal total, String customerName, String email, String phone) {
        if (storeId.isBlank() || storePassword.isBlank() || callbackBase.isBlank()) throw new InvalidRequestException("Online payment is not configured yet.");
        String host=live ? "https://securepay.sslcommerz.com" : "https://sandbox.sslcommerz.com";
        Map<String,String> form=new LinkedHashMap<>(); form.put("store_id",storeId); form.put("store_passwd",storePassword); form.put("total_amount",total.toPlainString()); form.put("currency","BDT"); form.put("tran_id",transactionId);
        form.put("success_url",callbackBase+"/api/food/payments/sslcommerz/success"); form.put("fail_url",callbackBase+"/api/food/payments/sslcommerz/fail"); form.put("cancel_url",callbackBase+"/api/food/payments/sslcommerz/cancel");
        form.put("cus_name",customerName); form.put("cus_email",email); form.put("cus_add1","Campus"); form.put("cus_city","Dhaka"); form.put("cus_country","Bangladesh"); form.put("cus_phone",phone); form.put("shipping_method","NO"); form.put("num_of_item","1"); form.put("product_name","Campus food order"); form.put("product_category","Food"); form.put("product_profile","general");
        try { var request=HttpRequest.newBuilder(URI.create(host+"/gwprocess/v4/api.php")).timeout(Duration.ofSeconds(25)).header("Content-Type","application/x-www-form-urlencoded").POST(HttpRequest.BodyPublishers.ofString(encode(form))).build(); var response=http.send(request,HttpResponse.BodyHandlers.ofString()); var body=json.readTree(response.body()); String url=body.path("GatewayPageURL").asText(); if (response.statusCode()/100!=2 || url.isBlank()) throw new InvalidRequestException(sessionError(body.path("failedreason").asText(), body.path("status").asText())); return url; }
        catch (InvalidRequestException exception) { throw exception; }
        catch (Exception exception) { throw new InvalidRequestException("Could not connect to SSLCommerz."); }
    }
    private String sessionError(String reason, String status) {
        String detail = reason == null || reason.isBlank() ? status : reason;
        if (detail == null || detail.isBlank()) return "SSLCommerz rejected the payment session. Check the live-store credentials and activation status.";
        // Gateway messages can be shown to the buyer, but never include a response body or credentials.
        String safeDetail = detail.replaceAll("[\\r\\n]", " ");
        return "SSLCommerz rejected the payment session: " + safeDetail.substring(0, Math.min(safeDetail.length(), 180));
    }
    public boolean validate(String validationId) {
        if (validationId == null || validationId.isBlank()) return false;
        String host=live ? "https://securepay.sslcommerz.com" : "https://sandbox.sslcommerz.com";
        try { String url=host+"/validator/api/validationserverAPI.php?val_id="+URLEncoder.encode(validationId,StandardCharsets.UTF_8)+"&store_id="+URLEncoder.encode(storeId,StandardCharsets.UTF_8)+"&store_passwd="+URLEncoder.encode(storePassword,StandardCharsets.UTF_8)+"&v=1&format=json"; var response=http.send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(25)).GET().build(),HttpResponse.BodyHandlers.ofString()); String status=json.readTree(response.body()).path("status").asText(); return response.statusCode()==200 && ("VALID".equals(status)||"VALIDATED".equals(status)); } catch (Exception exception) { return false; }
    }
    private String encode(Map<String,String> values) { return values.entrySet().stream().map(e -> URLEncoder.encode(e.getKey(),StandardCharsets.UTF_8)+"="+URLEncoder.encode(e.getValue()==null?"":e.getValue(),StandardCharsets.UTF_8)).collect(java.util.stream.Collectors.joining("&")); }
}
