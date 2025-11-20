package com.booklibrary.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booklibrary.entity.BookHistory;
import com.booklibrary.entity.UserEntity;
import com.booklibrary.repository.BookHistoryRepo;
import com.booklibrary.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
public class ExcelDownloadController {

    @Autowired
    private BookHistoryRepo historyRepo;

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/history/download")
    public void downloadHistory(HttpServletResponse response) throws IOException {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepo.findByEmail(username).orElse(null);

        boolean isAdmin = user != null && "ADMIN".equals(user.getRole());

        List<BookHistory> history = isAdmin 
                ? historyRepo.findAll()
                : historyRepo.findByUserEmailOrderByActionTimeDesc(username);

        response.reset();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"history.xlsx\"");

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("History");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("User");
        header.createCell(1).setCellValue("Book ID");
        header.createCell(2).setCellValue("Action");
        header.createCell(3).setCellValue("When");

        int rowIdx = 1;

        for (BookHistory h : history) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(h.getUserName());
            row.createCell(1).setCellValue(h.getBookId());
            row.createCell(2).setCellValue(h.getAction().replaceAll("<[^>]*>", ""));
            row.createCell(3).setCellValue(h.getActionTime().toString());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
