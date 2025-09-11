package com.school.attendance.service;

import com.school.attendance.dto.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    public List<AttendanceTrendDto> getAttendanceTrends(LocalDate startDate, LocalDate endDate, String type) {
        // Mock data for demonstration
        List<AttendanceTrendDto> trends = new ArrayList<>();
        
        LocalDate current = startDate;
        Random random = new Random();
        
        while (!current.isAfter(endDate)) {
            AttendanceTrendDto trend = new AttendanceTrendDto();
            trend.setDate(current);
            trend.setPresentCount(80 + random.nextInt(20)); // 80-100 present
            trend.setAbsentCount(10 + random.nextInt(10));  // 10-20 absent
            trend.setHolidayCount(current.getDayOfWeek().getValue() >= 6 ? 1 : 0); // Weekend holidays
            
            trends.add(trend);
            current = current.plusDays(1);
        }
        
        return trends;
    }

    public GenderRatioDto getGenderRatio(LocalDate startDate, LocalDate endDate) {
        // Mock data for demonstration
        GenderRatioDto ratio = new GenderRatioDto();
        Random random = new Random();
        
        ratio.setBoysPresent(45 + random.nextInt(10));
        ratio.setGirlsPresent(40 + random.nextInt(10));
        ratio.setBoysAbsent(5 + random.nextInt(5));
        ratio.setGirlsAbsent(5 + random.nextInt(5));
        
        return ratio;
    }

    public List<ClassPerformanceDto> getClassPerformance(LocalDate startDate, LocalDate endDate) {
        // Mock data for demonstration
        List<ClassPerformanceDto> performances = new ArrayList<>();
        String[] standards = {"1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th"};
        Random random = new Random();
        
        for (String standard : standards) {
            ClassPerformanceDto performance = new ClassPerformanceDto();
            performance.setStandard(standard);
            performance.setAverageAttendance(75.0 + random.nextDouble() * 20); // 75-95%
            performance.setTotalStudents(25 + random.nextInt(15)); // 25-40 students
            
            performances.add(performance);
        }
        
        return performances;
    }

    public List<TopAbsenteeDto> getTopAbsentees(int limit) {
        // Mock data for demonstration
        List<TopAbsenteeDto> absentees = new ArrayList<>();
        Random random = new Random();
        
        String[] firstNames = {"John", "Jane", "Alice", "Bob", "Charlie", "Diana", "Edward", "Fiona"};
        String[] lastNames = {"Smith", "Johnson", "Brown", "Davis", "Wilson", "Miller", "Moore", "Taylor"};
        
        for (int i = 0; i < limit; i++) {
            TopAbsenteeDto absentee = new TopAbsenteeDto();
            absentee.setStudentId((long) (i + 1));
            absentee.setStudentName(firstNames[random.nextInt(firstNames.length)] + " " + 
                                  lastNames[random.nextInt(lastNames.length)]);
            absentee.setStandard((i + 1) + "th");
            absentee.setSection("A");
            absentee.setAbsenceCount(15 + random.nextInt(10)); // 15-25 absences
            absentee.setAttendancePercentage(70.0 - random.nextDouble() * 20); // 50-70%
            
            absentees.add(absentee);
        }
        
        return absentees.stream()
                .sorted((a, b) -> Integer.compare(b.getAbsenceCount(), a.getAbsenceCount()))
                .collect(Collectors.toList());
    }

    public DashboardStatsDto getDashboardStats() {
        // Mock data for demonstration
        DashboardStatsDto stats = new DashboardStatsDto();
        Random random = new Random();
        
        stats.setTotalStudents(450 + random.nextInt(50));
        stats.setTotalTeachers(25 + random.nextInt(10));
        stats.setAverageAttendance(85.0 + random.nextDouble() * 10);
        stats.setTotalHolidays(12 + random.nextInt(5));
        stats.setPresentToday(400 + random.nextInt(30));
        stats.setAbsentToday(20 + random.nextInt(15));
        
        return stats;
    }
}
