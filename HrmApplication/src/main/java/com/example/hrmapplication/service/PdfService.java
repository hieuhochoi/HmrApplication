package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.Salary;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PdfService {

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.HELVETICA, 10, Font.NORMAL);

    public byte[] generatePayslipPdf(Salary salary) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Tiêu đề
        Paragraph title = new Paragraph("PHIẾU LƯƠNG", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Thông tin nhân viên và tháng/năm
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(15);

        addCell(infoTable, "Nhân viên:", salary.getEmployee().getFullName(), HEADER_FONT, NORMAL_FONT);
        addCell(infoTable, "Tháng/Năm:", salary.getMonth() + "/" + salary.getYear(), HEADER_FONT, NORMAL_FONT);
        
        if (salary.getEmployee().getCurrentDepartment() != null) {
            addCell(infoTable, "Phòng ban:", salary.getEmployee().getCurrentDepartment().getDepartmentName(), HEADER_FONT, NORMAL_FONT);
        }
        addCell(infoTable, "Trạng thái:", getStatusText(salary.getStatus()), HEADER_FONT, NORMAL_FONT);

        document.add(infoTable);

        // Các khoản thu nhập
        Paragraph incomeTitle = new Paragraph("CÁC KHOẢN THU NHẬP", HEADER_FONT);
        incomeTitle.setSpacingBefore(10);
        incomeTitle.setSpacingAfter(10);
        document.add(incomeTitle);

        PdfPTable incomeTable = createTable();
        addTableRow(incomeTable, "Lương cơ bản", formatCurrency(salary.getBaseSalary()));
        addTableRow(incomeTable, "Phụ cấp", formatCurrency(salary.getAllowance()));
        addTableRow(incomeTable, "Thưởng", formatCurrency(salary.getBonus()));
        addTableRow(incomeTable, "Tăng ca", formatCurrency(salary.getOvertime()));
        document.add(incomeTable);

        // Các khoản khấu trừ
        Paragraph deductionTitle = new Paragraph("CÁC KHOẢN KHẤU TRỪ", HEADER_FONT);
        deductionTitle.setSpacingBefore(10);
        deductionTitle.setSpacingAfter(10);
        document.add(deductionTitle);

        PdfPTable deductionTable = createTable();
        addTableRow(deductionTable, "Tạm ứng", formatCurrency(salary.getAdvance()));
        addTableRow(deductionTable, "Khấu trừ khác", formatCurrency(salary.getDeduction()));
        document.add(deductionTable);

        // Tổng lương
        Paragraph totalTitle = new Paragraph("TỔNG LƯƠNG THỰC NHẬN", new Font(Font.HELVETICA, 14, Font.BOLD));
        totalTitle.setSpacingBefore(15);
        totalTitle.setSpacingAfter(10);
        document.add(totalTitle);

        PdfPTable totalTable = createTable();
        PdfPCell totalCell = new PdfPCell(new Phrase(formatCurrency(salary.getTotalSalary()), 
                new Font(Font.HELVETICA, 14, Font.BOLD)));
        totalCell.setColspan(2);
        totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalCell.setPadding(10);
        totalTable.addCell(totalCell);
        document.add(totalTable);

        // Ngày thanh toán
        if (salary.getPaymentDate() != null) {
            Paragraph paymentDate = new Paragraph(
                    "Ngày thanh toán: " + salary.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    NORMAL_FONT);
            paymentDate.setSpacingBefore(15);
            paymentDate.setAlignment(Element.ALIGN_RIGHT);
            document.add(paymentDate);
        }

        document.close();
        return outputStream.toByteArray();
    }

    private PdfPTable createTable() {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{70, 30});
        return table;
    }

    private void addTableRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, NORMAL_FONT));
        labelCell.setPadding(8);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setPadding(8);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }

    private void addCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    private String formatCurrency(Double amount) {
        if (amount == null) {
            amount = 0.0;
        }
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + " VNĐ";
    }

    private String getStatusText(String status) {
        if (status == null) {
            return "N/A";
        }
        return switch (status) {
            case "PAID" -> "Đã thanh toán";
            case "PENDING" -> "Chờ thanh toán";
            case "CANCELLED" -> "Đã hủy";
            default -> status;
        };
    }
}

