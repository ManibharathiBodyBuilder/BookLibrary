/*package com.booklibrary.controllerpay;

import java.util.HashMap;
import java.util.Map;


import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

	@PostMapping("/create-order")
	public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> data) throws Exception {
	    // Convert amount safely
	    int amount = Integer.parseInt(data.get("amount").toString());

	    RazorpayClient client = new RazorpayClient("YOUR_KEY_ID", "YOUR_SECRET_KEY");

	    JSONObject orderRequest = new JSONObject();
	    orderRequest.put("amount", amount * 100); // Razorpay expects paise
	    orderRequest.put("currency", "INR");
	    orderRequest.put("receipt", "txn_12345");

	    Order order = client.orders.create(orderRequest);

	    Map<String, Object> response = new HashMap<>();
	    response.put("id", order.get("id"));
	    response.put("currency", order.get("currency"));
	    response.put("amount", order.get("amount"));

	    return ResponseEntity.ok(response);
	}
}

*/