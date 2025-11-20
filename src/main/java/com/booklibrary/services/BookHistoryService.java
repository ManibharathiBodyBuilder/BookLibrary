package com.booklibrary.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.booklibrary.entity.BookHistory;
import com.booklibrary.entity.UserEntity;
import com.booklibrary.repository.BookHistoryRepo;

@Service
public class BookHistoryService {

    @Autowired
    private BookHistoryRepo historyRepo;
    
    String EYE_ICON = "<i class=\"fa-solid fa-eye\" style=\"font-size:16px; color:red;\"></i>";

    String SAVE_ICON = "<i class=\"fa-solid fa-bookmark\" style=\"font-size:16px; color:#ff9800;\"></i>";

    String DOWNLOAD_ICON = "<i class=\"fa-solid fa-download\" style=\"font-size:16px; color:green;\"></i>";
    
    @Autowired
    private AuthService authService;

    /*public void downloadBook(Integer bookId) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> opt = authService.getLoggedUser();
        if (!opt.isPresent()) {
            throw new RuntimeException("User not logged in");
        }
        UserEntity user = opt.get();


        // 📌 Save history
        BookHistory h = new BookHistory();
        h.setUserEmail(email);
        h.setBookId(bookId);
        h.setAction("DOWNLOAD | " + DOWNLOAD_ICON);
        h.setUserId(user.getId());   // ⭐ Very Important
        h.setUserName(user.getFullName());
        historyRepo.save(h);
    }



    public void readBook(Integer bookId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(">> BookHistoryService.readBook() called. user=" + email + ", bookId=" + bookId);
        Optional<UserEntity> opt = authService.getLoggedUser();
        if (!opt.isPresent()) {
            throw new RuntimeException("User not logged in");
        }
        UserEntity user = opt.get();
        BookHistory h = new BookHistory();
        h.setUserEmail(email);
        h.setBookId(bookId);
        h.setAction("READ |"+" "+EYE_ICON);
        h.setUserId(user.getId());
        h.setUserName(user.getFullName());

        BookHistory saved = historyRepo.save(h);
        System.out.println(">> Saved history id=" + saved.getId());
    }
    
    
    public void addToMyBook(Integer bookId) {
        String email = SecurityContextHolder.getContext()
                         .getAuthentication().getName();
        Optional<UserEntity> opt = authService.getLoggedUser();
        if (!opt.isPresent()) {
            throw new RuntimeException("User not logged in");
        }
        UserEntity user = opt.get();


        BookHistory h = new BookHistory();
        h.setUserEmail(email);
        h.setBookId(bookId);
        h.setAction("ADDED_TO_MYBOOK |"+" "+SAVE_ICON);
        h.setUserId(user.getId());
        h.setUserName(user.getFullName());

        historyRepo.save(h);
    }*/
    public void downloadBook(Integer bookId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        BookHistory h = new BookHistory();
        h.setUserEmail(username);      
        h.setUserName(username);       
        h.setBookId(bookId);
        h.setAction("DOWNLOAD | " + DOWNLOAD_ICON);

        historyRepo.save(h);
    }

    public void readBook(Integer bookId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        BookHistory h = new BookHistory();
        h.setUserEmail(username);
        h.setUserName(username);
        h.setBookId(bookId);
        h.setAction("READ | " + EYE_ICON);

        historyRepo.save(h);
    }

    public void addToMyBook(Integer bookId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        BookHistory h = new BookHistory();
        h.setUserEmail(username);
        h.setUserName(username);
        h.setBookId(bookId);
        h.setAction("ADDED_TO_MYBOOK | " + SAVE_ICON);

        historyRepo.save(h);
    }


}
