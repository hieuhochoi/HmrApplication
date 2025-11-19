package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.Salary;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class ExcelService {

    public byte[] generateSalaryReportExcel(List<Salary> salaries, Integer month, Integer year) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bảng lương");

        // Tạo style cho header
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // Tạo style cho cell
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.cloneStyleFrom(cellStyle);
        DataFormat format = workbook.createDataFormat();
        currencyStyle.setDataFormat(format.getFormat("#,##0"));

        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BẢNG LƯƠNG " + (month != null && year != null ? "THÁNG " + month + "/" + year : ""));
        titleCell.setCellStyle(headerStyle);
        
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCell.setCellStyle(titleStyle);
        
        // Merge cells cho tiêu đề
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 10));

        // Header row
        int rowNum = 2;
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {
            "STT", "Họ tên", "Phòng ban", "Chức vụ", "Lương cơ bản", 
            "Phụ cấp", "Thưởng", "Tăng ca", "Tạm ứng", "Khấu trừ", "Tổng lương"
        };
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data rows
        int stt = 1;
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        
        for (Salary salary : salaries) {
            Row row = sheet.createRow(rowNum++);
            
            // STT
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(stt++);
            cell0.setCellStyle(cellStyle);
            
            // Họ tên
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(salary.getEmployee().getFullName());
            cell1.setCellStyle(cellStyle);
            
            // Phòng ban
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(salary.getEmployee().getCurrentDepartment() != null ? 
                    salary.getEmployee().getCurrentDepartment().getDepartmentName() : "");
            cell2.setCellStyle(cellStyle);
            
            // Chức vụ
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(salary.getEmployee().getCurrentPosition() != null ? 
                    salary.getEmployee().getCurrentPosition().getPositionName() : "");
            cell3.setCellStyle(cellStyle);
            
            // Lương cơ bản
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(salary.getBaseSalary() != null ? salary.getBaseSalary() : 0);
            cell4.setCellStyle(currencyStyle);
            
            // Phụ cấp
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(salary.getAllowance() != null ? salary.getAllowance() : 0);
            cell5.setCellStyle(currencyStyle);
            
            // Thưởng
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(salary.getBonus() != null ? salary.getBonus() : 0);
            cell6.setCellStyle(currencyStyle);
            
            // Tăng ca
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(salary.getOvertime() != null ? salary.getOvertime() : 0);
            cell7.setCellStyle(currencyStyle);
            
            // Tạm ứng
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(salary.getAdvance() != null ? salary.getAdvance() : 0);
            cell8.setCellStyle(currencyStyle);
            
            // Khấu trừ
            Cell cell9 = row.createCell(9);
            cell9.setCellValue(salary.getDeduction() != null ? salary.getDeduction() : 0);
            cell9.setCellStyle(currencyStyle);
            
            // Tổng lương
            Cell cell10 = row.createCell(10);
            cell10.setCellValue(salary.getTotalSalary() != null ? salary.getTotalSalary() : 0);
            cell10.setCellStyle(currencyStyle);
        }

        // Tổng cộng
        Row totalRow = sheet.createRow(rowNum);
        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("TỔNG CỘNG");
        totalLabelCell.setCellStyle(headerStyle);
        
        // Merge cells cho tổng cộng
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum, rowNum, 0, 3));
        
        // Tính tổng
        double totalBaseSalary = salaries.stream().mapToDouble(s -> s.getBaseSalary() != null ? s.getBaseSalary() : 0).sum();
        double totalAllowance = salaries.stream().mapToDouble(s -> s.getAllowance() != null ? s.getAllowance() : 0).sum();
        double totalBonus = salaries.stream().mapToDouble(s -> s.getBonus() != null ? s.getBonus() : 0).sum();
        double totalOvertime = salaries.stream().mapToDouble(s -> s.getOvertime() != null ? s.getOvertime() : 0).sum();
        double totalAdvance = salaries.stream().mapToDouble(s -> s.getAdvance() != null ? s.getAdvance() : 0).sum();
        double totalDeduction = salaries.stream().mapToDouble(s -> s.getDeduction() != null ? s.getDeduction() : 0).sum();
        double totalSalary = salaries.stream().mapToDouble(s -> s.getTotalSalary() != null ? s.getTotalSalary() : 0).sum();
        
        Cell totalCell4 = totalRow.createCell(4);
        totalCell4.setCellValue(totalBaseSalary);
        totalCell4.setCellStyle(currencyStyle);
        
        Cell totalCell5 = totalRow.createCell(5);
        totalCell5.setCellValue(totalAllowance);
        totalCell5.setCellStyle(currencyStyle);
        
        Cell totalCell6 = totalRow.createCell(6);
        totalCell6.setCellValue(totalBonus);
        totalCell6.setCellStyle(currencyStyle);
        
        Cell totalCell7 = totalRow.createCell(7);
        totalCell7.setCellValue(totalOvertime);
        totalCell7.setCellStyle(currencyStyle);
        
        Cell totalCell8 = totalRow.createCell(8);
        totalCell8.setCellValue(totalAdvance);
        totalCell8.setCellStyle(currencyStyle);
        
        Cell totalCell9 = totalRow.createCell(9);
        totalCell9.setCellValue(totalDeduction);
        totalCell9.setCellStyle(currencyStyle);
        
        Cell totalCell10 = totalRow.createCell(10);
        totalCell10.setCellValue(totalSalary);
        totalCell10.setCellStyle(currencyStyle);

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return outputStream.toByteArray();
    }
}

