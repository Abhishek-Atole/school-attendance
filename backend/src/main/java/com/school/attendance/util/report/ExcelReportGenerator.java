package com.school.attendance.util.report;

import com.school.attendance.entity.School;
import com.school.attendance.service.AttendanceService.AttendanceSummary;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class ExcelReportGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Generate Excel attendance report
     */
    public byte[] generateAttendanceReport(List<AttendanceSummary> summaries, School school, 
                                         LocalDate startDate, LocalDate endDate, String reportType) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Attendance Report");
            
            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle percentageStyle = createPercentageStyle(workbook);

            int rowNum = 0;

            // Add school branding header
            rowNum = addSchoolHeader(sheet, school, startDate, endDate, reportType, titleStyle, rowNum);

            // Add table headers
            rowNum = addTableHeaders(sheet, headerStyle, rowNum);

            // Add data rows
            rowNum = addDataRows(sheet, summaries, dataStyle, percentageStyle, rowNum);

            // Add footer
            addFooter(sheet, summaries, titleStyle, dataStyle, rowNum);

            // Auto-size columns
            for (int i = 0; i < 16; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            
            log.info("Generated Excel report with {} records", summaries.size());
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            log.error("Error generating Excel report: {}", e.getMessage());
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createPercentageStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        return style;
    }

    private int addSchoolHeader(Sheet sheet, School school, LocalDate startDate, LocalDate endDate, 
                              String reportType, CellStyle titleStyle, int rowNum) {
        
        // School name
        Row row = sheet.createRow(rowNum++);
        Cell cell = row.createCell(0);
        cell.setCellValue(school.getName());
        cell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 15));

        // School address
        if (school.getAddress() != null) {
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(school.getAddress());
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 15));
        }

        // Contact information
        if (school.getContactEmail() != null || school.getContactPhone() != null) {
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            String contact = "";
            if (school.getContactEmail() != null) {
                contact += "Email: " + school.getContactEmail();
            }
            if (school.getContactPhone() != null) {
                if (!contact.isEmpty()) contact += " | ";
                contact += "Phone: " + school.getContactPhone();
            }
            cell.setCellValue(contact);
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 15));
        }

        // Empty row
        sheet.createRow(rowNum++);

        // Report title
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("ATTENDANCE REPORT - " + reportType.toUpperCase());
        cell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 15));

        // Date range
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("Period: " + startDate.format(DATE_FORMATTER) + " to " + endDate.format(DATE_FORMATTER));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 15));

        // Generated date
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("Generated on: " + LocalDate.now().format(DATE_FORMATTER));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 15));

        // Empty row
        sheet.createRow(rowNum++);

        return rowNum;
    }

    private int addTableHeaders(Sheet sheet, CellStyle headerStyle, int rowNum) {
        Row headerRow = sheet.createRow(rowNum++);
        
        String[] headers = {
            "Sr. No.", "GR No.", "Roll No.", "Student Name", "Gender", "Date of Birth", 
            "Age", "Mobile Number", "Standard", "Total Days", "Present Days", 
            "Absent Days", "Half Days", "Sick Leave", "Holidays", "Attendance %"
        };
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        return rowNum;
    }

    private int addDataRows(Sheet sheet, List<AttendanceSummary> summaries, 
                          CellStyle dataStyle, CellStyle percentageStyle, int rowNum) {
        int serialNo = 1;
        
        for (AttendanceSummary summary : summaries) {
            Row row = sheet.createRow(rowNum++);
            
            // Serial Number
            Cell cell = row.createCell(0);
            cell.setCellValue(serialNo++);
            cell.setCellStyle(dataStyle);
            
            // GR No
            cell = row.createCell(1);
            cell.setCellValue(summary.getGrNo() != null ? summary.getGrNo() : "");
            cell.setCellStyle(dataStyle);
            
            // Roll No
            cell = row.createCell(2);
            cell.setCellValue(summary.getRollNo() != null ? summary.getRollNo() : "");
            cell.setCellStyle(dataStyle);
            
            // Student Name
            cell = row.createCell(3);
            cell.setCellValue(summary.getStudentName());
            cell.setCellStyle(dataStyle);
            
            // Gender
            cell = row.createCell(4);
            cell.setCellValue(summary.getGender());
            cell.setCellStyle(dataStyle);
            
            // Date of Birth
            cell = row.createCell(5);
            cell.setCellValue(summary.getDateOfBirth().format(DATE_FORMATTER));
            cell.setCellStyle(dataStyle);
            
            // Age
            cell = row.createCell(6);
            cell.setCellValue(summary.getAge());
            cell.setCellStyle(dataStyle);
            
            // Mobile Number
            cell = row.createCell(7);
            cell.setCellValue(summary.getMobileNumber() != null ? summary.getMobileNumber() : "");
            cell.setCellStyle(dataStyle);
            
            // Standard
            cell = row.createCell(8);
            cell.setCellValue(summary.getStandard() != null ? summary.getStandard() : "");
            cell.setCellStyle(dataStyle);
            
            // Total Days
            cell = row.createCell(9);
            cell.setCellValue(summary.getTotalDays());
            cell.setCellStyle(dataStyle);
            
            // Present Days
            cell = row.createCell(10);
            cell.setCellValue(summary.getPresentDays());
            cell.setCellStyle(dataStyle);
            
            // Absent Days
            cell = row.createCell(11);
            cell.setCellValue(summary.getAbsentDays());
            cell.setCellStyle(dataStyle);
            
            // Half Days
            cell = row.createCell(12);
            cell.setCellValue(summary.getHalfDays());
            cell.setCellStyle(dataStyle);
            
            // Sick Leave
            cell = row.createCell(13);
            cell.setCellValue(summary.getSickLeaveDays());
            cell.setCellStyle(dataStyle);
            
            // Holidays
            cell = row.createCell(14);
            cell.setCellValue(summary.getHolidayDays());
            cell.setCellStyle(dataStyle);
            
            // Attendance Percentage
            cell = row.createCell(15);
            cell.setCellValue(summary.getAttendancePercentage() / 100.0); // Convert to decimal for percentage format
            cell.setCellStyle(percentageStyle);
        }
        
        return rowNum;
    }

    private void addFooter(Sheet sheet, List<AttendanceSummary> summaries, 
                         CellStyle titleStyle, CellStyle dataStyle, int rowNum) {
        // Empty row
        sheet.createRow(rowNum++);
        
        // Summary title
        Row row = sheet.createRow(rowNum++);
        Cell cell = row.createCell(0);
        cell.setCellValue("SUMMARY");
        cell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 15));
        
        // Calculate totals
        long totalStudents = summaries.size();
        long totalPresentDays = summaries.stream().mapToLong(AttendanceSummary::getPresentDays).sum();
        long totalAbsentDays = summaries.stream().mapToLong(AttendanceSummary::getAbsentDays).sum();
        long totalDays = summaries.stream().mapToLong(AttendanceSummary::getTotalDays).sum();
        double overallAttendance = totalDays > 0 ? (totalPresentDays * 100.0 / totalDays) : 0.0;
        
        // Total Students
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("Total Students:");
        cell.setCellStyle(dataStyle);
        cell = row.createCell(1);
        cell.setCellValue(totalStudents);
        cell.setCellStyle(dataStyle);
        
        // Total Present Days
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("Total Present Days:");
        cell.setCellStyle(dataStyle);
        cell = row.createCell(1);
        cell.setCellValue(totalPresentDays);
        cell.setCellStyle(dataStyle);
        
        // Total Absent Days
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("Total Absent Days:");
        cell.setCellStyle(dataStyle);
        cell = row.createCell(1);
        cell.setCellValue(totalAbsentDays);
        cell.setCellStyle(dataStyle);
        
        // Overall Attendance
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("Overall Attendance:");
        cell.setCellStyle(dataStyle);
        cell = row.createCell(1);
        cell.setCellValue(String.format("%.2f%%", overallAttendance));
        cell.setCellStyle(dataStyle);
        
        // Footer note
        rowNum += 2;
        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue("Report generated by School Attendance Management System");
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 15));
    }
}
