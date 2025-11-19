package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.Salary;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final PdfService pdfService;

    @Value("${spring.mail.from:no-reply@hrm.local}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender, PdfService pdfService) {
        this.mailSender = mailSender;
        this.pdfService = pdfService;
    }

    public void sendPayslip(@NonNull Salary salary) throws MessagingException, com.lowagie.text.DocumentException {
        if (salary.getEmployee() == null || salary.getEmployee().getEmail() == null) {
            throw new MessagingException("Không có email nhân viên để gửi phiếu lương");
        }
        byte[] pdfBytes = pdfService.generatePayslipPdf(salary);
        String subject = String.format("Phiếu lương %02d/%d - %s",
                salary.getMonth(), salary.getYear(), salary.getEmployee().getFullName());
        String body = "Kính gửi Anh/Chị " + salary.getEmployee().getFullName()
                + ",\n\nĐính kèm là phiếu lương của Anh/Chị. Vui lòng kiểm tra.\n\nTrân trọng.";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromAddress);
        helper.setTo(salary.getEmployee().getEmail());
        helper.setSubject(subject);
        helper.setText(body.replace("\n", "<br/>"), true);
        String fileName = String.format("payslip_%s_%02d_%d.pdf",
                salary.getEmployee().getFullName().replaceAll("\\s+", "_"),
                salary.getMonth(), salary.getYear());
        helper.addAttachment(fileName, new ByteArrayResource(pdfBytes));

        mailSender.send(message);
    }
}


