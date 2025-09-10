package com.school.attendance.dto;

public class DashboardStatsDto {
    private int totalStudents;
    private int totalTeachers;
    private double averageAttendance;
    private int totalHolidays;
    private int presentToday;
    private int absentToday;
    private int lateToday;
    private int totalClasses;

    public DashboardStatsDto() {}

    public DashboardStatsDto(int totalStudents, int totalTeachers, double averageAttendance, int totalHolidays) {
        this.totalStudents = totalStudents;
        this.totalTeachers = totalTeachers;
        this.averageAttendance = averageAttendance;
        this.totalHolidays = totalHolidays;
    }

    // Getters and Setters
    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }

    public int getTotalTeachers() { return totalTeachers; }
    public void setTotalTeachers(int totalTeachers) { this.totalTeachers = totalTeachers; }

    public double getAverageAttendance() { return averageAttendance; }
    public void setAverageAttendance(double averageAttendance) { this.averageAttendance = averageAttendance; }

    public int getTotalHolidays() { return totalHolidays; }
    public void setTotalHolidays(int totalHolidays) { this.totalHolidays = totalHolidays; }

    public int getPresentToday() { return presentToday; }
    public void setPresentToday(int presentToday) { this.presentToday = presentToday; }

    public int getAbsentToday() { return absentToday; }
    public void setAbsentToday(int absentToday) { this.absentToday = absentToday; }

    public int getLateToday() { return lateToday; }
    public void setLateToday(int lateToday) { this.lateToday = lateToday; }

    public int getTotalClasses() { return totalClasses; }
    public void setTotalClasses(int totalClasses) { this.totalClasses = totalClasses; }

    public int getTotalPresentToday() { return presentToday + lateToday; }
    public double getTodayAttendancePercentage() { 
        int total = presentToday + absentToday + lateToday;
        return total > 0 ? ((double)(presentToday + lateToday) / total) * 100 : 0.0;
    }
}
