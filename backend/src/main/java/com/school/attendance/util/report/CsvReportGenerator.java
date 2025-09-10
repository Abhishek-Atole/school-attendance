package com.school.attendance.util.report;

import com.opencsv.CSVWriter;
import com.school.attendance.entity.School;
import com.school.attendance.service.AttendanceService.AttendanceSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class CsvReportGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Generate CSV attendance report
     */
    public byte[] generateAttendanceReport(List<AttendanceSummary> summaries, School school, 
                                         LocalDate startDate, LocalDate endDate, String reportType) {
        try {
            StringWriter stringWriter = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(stringWriter);

            // Add school branding header
            addSchoolHeader(csvWriter, school, startDate, endDate, reportType);

            // Add table headers
            addTableHeaders(csvWriter);

            // Add data rows
            addDataRows(csvWriter, summaries);

            // Add footer
            addFooter(csvWriter, summaries);

            csvWriter.close();
            
            String csvContent = stringWriter.toString();
            log.info("Generated CSV report with {} records", summaries.size());
            
            return csvContent.getBytes("UTF-8");
            
        } catch (Exception e) {
            log.error("Error generating CSV report: {}", e.getMessage());
            throw new RuntimeException("Failed to generate CSV report", e);
        }
    }

    private void addSchoolHeader(CSVWriter csvWriter, School school, LocalDate startDate, 
                               LocalDate endDate, String reportType) {
        // School name and branding
        csvWriter.writeNext(new String[]{school.getName()});
        
        if (school.getAddress() != null) {
            csvWriter.writeNext(new String[]{school.getAddress()});
        }
        
        if (school.getContactEmail() != null || school.getContactPhone() != null) {
            String contact = "";
            if (school.getContactEmail() != null) {
                contact += "Email: " + school.getContactEmail();
            }
            if (school.getContactPhone() != null) {
                if (!contact.isEmpty()) contact += " | ";
                contact += "Phone: " + school.getContactPhone();
            }
            csvWriter.writeNext(new String[]{contact});
        }
        
        // Report title and date range
        csvWriter.writeNext(new String[]{""});
        csvWriter.writeNext(new String[]{"ATTENDANCE REPORT - " + reportType.toUpperCase()});
        csvWriter.writeNext(new String[]{"Period: " + startDate.format(DATE_FORMATTER) + " to " + endDate.format(DATE_FORMATTER)});
        csvWriter.writeNext(new String[]{"Generated on: " + LocalDate.now().format(DATE_FORMATTER)});
        csvWriter.writeNext(new String[]{""});
    }

    private void addTableHeaders(CSVWriter csvWriter) {
        String[] headers = {
            "Sr. No.",
            "GR No.",
            "Roll No.", 
            "Student Name",
            "Gender",
            "Date of Birth",
            "Age",
            "Mobile Number",
            "Standard",
            "Total Days",
            "Present Days",
            "Absent Days",
            "Half Days",
            "Sick Leave",
            "Holidays",
            "Attendance %"
        };
        csvWriter.writeNext(headers);
    }

    private void addDataRows(CSVWriter csvWriter, List<AttendanceSummary> summaries) {
        int serialNo = 1;
        
        for (AttendanceSummary summary : summaries) {
            String[] row = {
                String.valueOf(serialNo++),
                summary.getGrNo() != null ? summary.getGrNo() : "",
                summary.getRollNo() != null ? summary.getRollNo() : "",
                summary.getStudentName(),
                summary.getGender(),
                summary.getDateOfBirth().format(DATE_FORMATTER),
                String.valueOf(summary.getAge()),
                summary.getMobileNumber() != null ? summary.getMobileNumber() : "",
                summary.getStandard() != null ? summary.getStandard() : "",
                String.valueOf(summary.getTotalDays()),
                String.valueOf(summary.getPresentDays()),
                String.valueOf(summary.getAbsentDays()),
                String.valueOf(summary.getHalfDays()),
                String.valueOf(summary.getSickLeaveDays()),
                String.valueOf(summary.getHolidayDays()),
                String.format("%.2f%%", summary.getAttendancePercentage())
            };
            csvWriter.writeNext(row);
        }
    }

    private void addFooter(CSVWriter csvWriter, List<AttendanceSummary> summaries) {
        csvWriter.writeNext(new String[]{""});
        
        // Calculate totals
        long totalStudents = summaries.size();
        long totalPresentDays = summaries.stream().mapToLong(AttendanceSummary::getPresentDays).sum();
        long totalAbsentDays = summaries.stream().mapToLong(AttendanceSummary::getAbsentDays).sum();
        long totalDays = summaries.stream().mapToLong(AttendanceSummary::getTotalDays).sum();
        
        double overallAttendance = totalDays > 0 ? (totalPresentDays * 100.0 / totalDays) : 0.0;
        
        csvWriter.writeNext(new String[]{"SUMMARY"});
        csvWriter.writeNext(new String[]{"Total Students", String.valueOf(totalStudents)});
        csvWriter.writeNext(new String[]{"Total Present Days", String.valueOf(totalPresentDays)});
        csvWriter.writeNext(new String[]{"Total Absent Days", String.valueOf(totalAbsentDays)});
        csvWriter.writeNext(new String[]{"Overall Attendance", String.format("%.2f%%", overallAttendance)});
        
        csvWriter.writeNext(new String[]{""});
        csvWriter.writeNext(new String[]{"Report generated by School Attendance Management System"});
    }
}
