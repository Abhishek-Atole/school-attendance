package com.school.attendance.dto;

public class TopAbsenteeDto {
    private Long studentId;
    private String studentName;
    private String standard;
    private String section;
    private int absenceCount;
    private double attendancePercentage;
    private String grNo;
    private String rollNo;

    public TopAbsenteeDto() {}

    public TopAbsenteeDto(Long studentId, String studentName, String standard, String section, 
                         int absenceCount, double attendancePercentage) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.standard = standard;
        this.section = section;
        this.absenceCount = absenceCount;
        this.attendancePercentage = attendancePercentage;
    }

    // Getters and Setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStandard() { return standard; }
    public void setStandard(String standard) { this.standard = standard; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public int getAbsenceCount() { return absenceCount; }
    public void setAbsenceCount(int absenceCount) { this.absenceCount = absenceCount; }

    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }

    public String getGrNo() { return grNo; }
    public void setGrNo(String grNo) { this.grNo = grNo; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
}
