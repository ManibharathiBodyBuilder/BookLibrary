package com.booklibrary.razorpayservice;


import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


@Service
public class RazorpayService {

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;

    public Order createOrder(long amountPaise, String receipt) throws Exception {
        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountPaise);     // in paise (e.g., 50000 = ₹500)
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receipt);
        orderRequest.put("payment_capture", 1);      // auto-capture
        System.out.println("🔑 Razorpay keyId: " + keyId);
        System.out.println("🔒 Razorpay keySecret: " + keySecret);

        // ✅ Correct usage — "Orders" is a method, not a field
        return client.orders.create(orderRequest);
    }

    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + '|' + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(keySecret.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] digest = mac.doFinal(payload.getBytes());
            String expected = bytesToHex(digest);
            // Razorpay sends signature in hex; compare case-insensitively
            return expected.equalsIgnoreCase(signature);
        } catch (Exception e) {
            return false;
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) hex.append(String.format("%02x", b));
        return hex.toString();
    }

    public String getKeyId() { return keyId; }
}

