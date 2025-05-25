package com.taskmang.util;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.taskmang.dto.response.TaskResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelExporter {
    public byte[] exportTasks(List<TaskResponse> tasks) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); 
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Tasks");
            
            // Header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Name", "Description", "Category", "Due Date", "Status"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }
            
            // Data rows
            int rowNum = 1;
            for (TaskResponse task : tasks) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(task.getId());
                row.createCell(1).setCellValue(task.getName());
                row.createCell(2).setCellValue(task.getDescription());
                row.createCell(3).setCellValue(task.getCategory());
                row.createCell(4).setCellValue(task.getDueDate().toString());
                row.createCell(5).setCellValue(task.getStatus());
            }
            
            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }
}