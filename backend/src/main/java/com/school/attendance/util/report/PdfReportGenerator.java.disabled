package com.school.attendance.util.report;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.school.attendance.entity.School;
import com.school.attendance.service.AttendanceService.AttendanceSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class PdfReportGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Generate PDF attendance report
     */
    public byte[] generateAttendanceReport(List<AttendanceSummary> summaries, School school, 
                                         LocalDate startDate, LocalDate endDate, String reportType) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.A4.rotate()); // Landscape for better table fit
            
            // Set margins
            document.setMargins(36, 36, 36, 36);
            
            // Create fonts
            PdfFont titleFont = PdfFontFactory.createFont();
            PdfFont headerFont = PdfFontFactory.createFont();
            PdfFont normalFont = PdfFontFactory.createFont();
            
            // Add school header
            addSchoolHeader(document, school, startDate, endDate, reportType, titleFont, normalFont);
            
            // Add attendance table
            addAttendanceTable(document, summaries, headerFont, normalFont);
            
            // Add summary
            addSummary(document, summaries, titleFont, normalFont);
            
            // Add footer
            addFooter(document, normalFont);
            
            document.close();
            
            log.info("Generated PDF report with {} records", summaries.size());
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            log.error("Error generating PDF report: {}", e.getMessage());
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    private void addSchoolHeader(Document document, School school, LocalDate startDate, LocalDate endDate, 
                               String reportType, PdfFont titleFont, PdfFont normalFont) {
        
        // School name
        Paragraph schoolName = new Paragraph(school.getName())
                .setFont(titleFont)
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(schoolName);
        
        // School address
        if (school.getAddress() != null) {
            Paragraph address = new Paragraph(school.getAddress())
                    .setFont(normalFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5);
            document.add(address);
        }
        
        // Contact information
        if (school.getContactEmail() != null || school.getContactPhone() != null) {
            String contact = "";
            if (school.getContactEmail() != null) {
                contact += "Email: " + school.getContactEmail();
            }
            if (school.getContactPhone() != null) {
                if (!contact.isEmpty()) contact += " | ";
                contact += "Phone: " + school.getContactPhone();
            }
            
            Paragraph contactInfo = new Paragraph(contact)
                    .setFont(normalFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15);
            document.add(contactInfo);
        }
        
        // Report title
        Paragraph title = new Paragraph("ATTENDANCE REPORT - " + reportType.toUpperCase())
                .setFont(titleFont)
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(title);
        
        // Date range
        Paragraph dateRange = new Paragraph("Period: " + startDate.format(DATE_FORMATTER) + " to " + endDate.format(DATE_FORMATTER))
                .setFont(normalFont)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(dateRange);
        
        // Generated date
        Paragraph generatedDate = new Paragraph("Generated on: " + LocalDate.now().format(DATE_FORMATTER))
                .setFont(normalFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(generatedDate);
    }

    private void addAttendanceTable(Document document, List<AttendanceSummary> summaries, 
                                  PdfFont headerFont, PdfFont normalFont) {
        
        // Create table with appropriate column widths
        float[] columnWidths = {1, 2, 1.5f, 4, 1.5f, 2, 1, 2, 2, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(20);
        
        // Add headers
        String[] headers = {
            "Sr.", "GR No.", "Roll No.", "Student Name", "Gender", "DOB", 
            "Age", "Mobile", "Standard", "Total", "Present", 
            "Absent", "Half", "Sick", "Holiday", "Attendance %"
        };
        
        for (String header : headers) {
            Cell headerCell = new Cell()
                    .add(new Paragraph(header)
                            .setFont(headerFont)
                            .setFontSize(8)
                            .setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(5);
            table.addHeaderCell(headerCell);
        }
        
        // Add data rows
        int serialNo = 1;
        for (AttendanceSummary summary : summaries) {
            // Serial Number
            table.addCell(createDataCell(String.valueOf(serialNo++), normalFont));
            
            // GR No
            table.addCell(createDataCell(summary.getGrNo() != null ? summary.getGrNo() : "", normalFont));
            
            // Roll No
            table.addCell(createDataCell(summary.getRollNo() != null ? summary.getRollNo() : "", normalFont));
            
            // Student Name
            table.addCell(createDataCell(summary.getStudentName(), normalFont));
            
            // Gender
            table.addCell(createDataCell(summary.getGender(), normalFont));
            
            // Date of Birth
            table.addCell(createDataCell(summary.getDateOfBirth().format(DATE_FORMATTER), normalFont));
            
            // Age
            table.addCell(createDataCell(String.valueOf(summary.getAge()), normalFont));
            
            // Mobile Number
            table.addCell(createDataCell(summary.getMobileNumber() != null ? summary.getMobileNumber() : "", normalFont));
            
            // Standard
            table.addCell(createDataCell(summary.getStandard() != null ? summary.getStandard() : "", normalFont));
            
            // Total Days
            table.addCell(createDataCell(String.valueOf(summary.getTotalDays()), normalFont));
            
            // Present Days
            table.addCell(createDataCell(String.valueOf(summary.getPresentDays()), normalFont));
            
            // Absent Days
            table.addCell(createDataCell(String.valueOf(summary.getAbsentDays()), normalFont));
            
            // Half Days
            table.addCell(createDataCell(String.valueOf(summary.getHalfDays()), normalFont));
            
            // Sick Leave
            table.addCell(createDataCell(String.valueOf(summary.getSickLeaveDays()), normalFont));
            
            // Holidays
            table.addCell(createDataCell(String.valueOf(summary.getHolidayDays()), normalFont));
            
            // Attendance Percentage
            table.addCell(createDataCell(String.format("%.2f%%", summary.getAttendancePercentage()), normalFont));
        }
        
        document.add(table);
    }

    private Cell createDataCell(String content, PdfFont font) {
        return new Cell()
                .add(new Paragraph(content)
                        .setFont(font)
                        .setFontSize(7))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(3)
                .setBorder(Border.NO_BORDER);
    }

    private void addSummary(Document document, List<AttendanceSummary> summaries, 
                          PdfFont titleFont, PdfFont normalFont) {
        
        // Summary title
        Paragraph summaryTitle = new Paragraph("SUMMARY")
                .setFont(titleFont)
                .setFontSize(14)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(summaryTitle);
        
        // Calculate totals
        long totalStudents = summaries.size();
        long totalPresentDays = summaries.stream().mapToLong(AttendanceSummary::getPresentDays).sum();
        long totalAbsentDays = summaries.stream().mapToLong(AttendanceSummary::getAbsentDays).sum();
        long totalDays = summaries.stream().mapToLong(AttendanceSummary::getTotalDays).sum();
        double overallAttendance = totalDays > 0 ? (totalPresentDays * 100.0 / totalDays) : 0.0;
        
        // Create summary table
        Table summaryTable = new Table(2)
                .setWidth(UnitValue.createPercentValue(50))
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setMarginBottom(20);
        
        // Add summary data
        addSummaryRow(summaryTable, "Total Students:", String.valueOf(totalStudents), normalFont);
        addSummaryRow(summaryTable, "Total Present Days:", String.valueOf(totalPresentDays), normalFont);
        addSummaryRow(summaryTable, "Total Absent Days:", String.valueOf(totalAbsentDays), normalFont);
        addSummaryRow(summaryTable, "Overall Attendance:", String.format("%.2f%%", overallAttendance), normalFont);
        
        document.add(summaryTable);
    }

    private void addSummaryRow(Table table, String label, String value, PdfFont font) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label)
                        .setFont(font)
                        .setFontSize(10)
                        .setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(5);
        
        Cell valueCell = new Cell()
                .add(new Paragraph(value)
                        .setFont(font)
                        .setFontSize(10))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addFooter(Document document, PdfFont normalFont) {
        Paragraph footer = new Paragraph("Report generated by School Attendance Management System")
                .setFont(normalFont)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20);
        document.add(footer);
    }
}
