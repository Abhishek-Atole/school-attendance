package com.school.attendance.dto;

import java.time.LocalDate;

public class AttendanceTrendDto {
    private LocalDate date;
    private int presentCount;
    private int absentCount;
    private int holidayCount;

    public AttendanceTrendDto() {}

    public AttendanceTrendDto(LocalDate date, int presentCount, int absentCount, int holidayCount) {
        this.date = date;
        this.presentCount = presentCount;
        this.absentCount = absentCount;
        this.holidayCount = holidayCount;
    }

    // Getters and Setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getPresentCount() { return presentCount; }
    public void setPresentCount(int presentCount) { this.presentCount = presentCount; }

    public int getAbsentCount() { return absentCount; }
    public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }

    public int getHolidayCount() { return holidayCount; }
    public void setHolidayCount(int holidayCount) { this.holidayCount = holidayCount; }
}
