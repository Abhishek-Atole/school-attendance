package com.school.attendance.dto;

public class ClassPerformanceDto {
    private String standard;
    private double averageAttendance;
    private int totalStudents;
    private int presentStudents;
    private int absentStudents;

    public ClassPerformanceDto() {}

    public ClassPerformanceDto(String standard, double averageAttendance, int totalStudents) {
        this.standard = standard;
        this.averageAttendance = averageAttendance;
        this.totalStudents = totalStudents;
    }

    // Getters and Setters
    public String getStandard() { return standard; }
    public void setStandard(String standard) { this.standard = standard; }

    public double getAverageAttendance() { return averageAttendance; }
    public void setAverageAttendance(double averageAttendance) { this.averageAttendance = averageAttendance; }

    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }

    public int getPresentStudents() { return presentStudents; }
    public void setPresentStudents(int presentStudents) { this.presentStudents = presentStudents; }

    public int getAbsentStudents() { return absentStudents; }
    public void setAbsentStudents(int absentStudents) { this.absentStudents = absentStudents; }
}
