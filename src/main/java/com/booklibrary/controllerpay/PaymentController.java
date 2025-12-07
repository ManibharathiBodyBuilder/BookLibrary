package com.booklibrary.controllerpay;

import com.booklibrary.entity.BookEntity;
import com.booklibrary.payment.Payment;
import com.booklibrary.razorpayservice.RazorpayService;
import com.booklibrary.repository.BookRepository;
import com.booklibrary.repository.PaymentRepository;

import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

@Controller
public class PaymentController {

    @Autowired 
    private BookRepository bookRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private RazorpayService razorpayService;

    // choose your price here (in paise). Example: ₹10.00 → 1000
    private static final long PRICE_PAISE = 100; // ₹1


    @GetMapping("/payment/{id}")
    public String paymentPage(@PathVariable("id") Long bookId, Model model) {
        Optional<BookEntity> opt = bookRepository.findById(bookId);
        if (!opt.isPresent()) {
            model.addAttribute("error", "Book not found");
            return "error";
        }
        BookEntity book = opt.get();

        try {
            String receipt = "book-" + bookId + "-" + System.currentTimeMillis();
            System.out.println("🧾 Creating order with amount: " + PRICE_PAISE);
            Order order = razorpayService.createOrder(PRICE_PAISE, receipt);
            System.out.println("✅ Order Created: " + order.toString());

            // Save PENDING payment row
            Payment pay = new Payment();
            pay.setBookId(bookId);
            pay.setOrderId(order.get("id"));
            pay.setAmountPaise(PRICE_PAISE);
            pay.setStatus("PENDING");
            pay.setCreatedAt(LocalDateTime.now());
            paymentRepository.save(pay);

            model.addAttribute("book", book);
            model.addAttribute("keyId", razorpayService.getKeyId());
            model.addAttribute("orderId", order.get("id"));
            model.addAttribute("amountPaise", PRICE_PAISE);
            model.addAttribute("amountRupee", String.format("%.2f", PRICE_PAISE / 100.0));
            model.addAttribute("receipt", receipt);
            
            return "payment_page";

        } catch (Exception e) {
        	e.printStackTrace();
            model.addAttribute("error", "Unable to start payment: " + e.getMessage());
            return "error";
        }
    }

    // Razorpay checkout success posts here
    @PostMapping("/payment/verify")
    public String verifyPayment(
            @RequestParam("razorpay_order_id") String orderId,
            @RequestParam("razorpay_payment_id") String paymentId,
            @RequestParam("razorpay_signature") String signature,
            @RequestParam("bookId") Long bookId,
            Model model
    ) {
        boolean ok = razorpayService.verifySignature(orderId, paymentId, signature);
        Optional<Payment> optPay = paymentRepository.findTopByBookIdAndStatusOrderByIdDesc(bookId, "PENDING");

        if (!optPay.isPresent()) {
            model.addAttribute("error", "No pending payment found");
            return "error";
        }

        Payment pay = optPay.get();
        if (!pay.getOrderId().equals(orderId)) {
            model.addAttribute("error", "Order mismatch");
            return "error";
        }

        if (ok) {
            pay.setPaymentId(paymentId);
            pay.setSignature(signature);
            pay.setStatus("PAID");
            pay.setPaidAt(LocalDateTime.now());
            // generate one-time download token
            String token = UUID.randomUUID().toString();
            pay.setDownloadToken(token);
            paymentRepository.save(pay);

            // redirect to a success page with a secured download link
            return "redirect:/payment/success?bookId=" + bookId + "&token=" + token;

        } else {
            pay.setStatus("FAILED");
            paymentRepository.save(pay);
            model.addAttribute("error", "Signature verification failed");
            return "error";
        }
    }

    @GetMapping("/payment/success")
    public String paymentSuccess(@RequestParam Long bookId, @RequestParam String token, Model model) {
        model.addAttribute("bookId", bookId);
        model.addAttribute("token", token);
        return "payment_success";
    }

    // secure download
    /*@GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable("id") Long bookId, @RequestParam("token") String token) {
        // verify token belongs to a PAID payment
        Optional<Payment> opt = paymentRepository.findByBookIdAndDownloadToken(bookId, token);
        if (!opt.isPresent() || !"PAID".equals(opt.get().getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        BookEntity book = bookRepository.findById(bookId).orElse(null);
        if (book == null) return ResponseEntity.notFound().build();

        // --- adapt this part based on your storage ---
        // If you store bytes: byte[] file = book.getFileData();
        // In your earlier code, looked like string “255044...”, so:
        byte[] file = (book.getBookDocument() != null)
                ? book.getBookDocument()
                : new byte[0];

        String fileName = (book.getFileName() != null) ? book.getFileName() : "book.pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }*/
    
    @GetMapping("/download/{id}")
    public ResponseEntity<?> download(@PathVariable("id") Long bookId, @RequestParam("token") String token) {

        // 1️⃣ Verify token belongs to a PAID payment
        Optional<Payment> opt = paymentRepository.findByBookIdAndDownloadToken(bookId, token);
        if (!opt.isPresent() || !"PAID".equals(opt.get().getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 2️⃣ Fetch book data
        BookEntity book = bookRepository.findById(bookId).orElse(null);
        if (book == null || book.getPdfUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            URL url = new URL(book.getPdfUrl());
            InputStream in = url.openStream();

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int nRead;

            while ((nRead = in.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            byte[] pdfBytes = buffer.toByteArray();
            in.close();

            String fileName = (book.getFileName() != null) ? book.getFileName() : "book.pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error downloading file");
        }

    
}
}
