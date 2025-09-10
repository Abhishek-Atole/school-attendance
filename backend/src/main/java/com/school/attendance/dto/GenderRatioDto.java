package com.school.attendance.dto;

public class GenderRatioDto {
    private int boysPresent;
    private int girlsPresent;
    private int boysAbsent;
    private int girlsAbsent;

    public GenderRatioDto() {}

    public GenderRatioDto(int boysPresent, int girlsPresent, int boysAbsent, int girlsAbsent) {
        this.boysPresent = boysPresent;
        this.girlsPresent = girlsPresent;
        this.boysAbsent = boysAbsent;
        this.girlsAbsent = girlsAbsent;
    }

    // Getters and Setters
    public int getBoysPresent() { return boysPresent; }
    public void setBoysPresent(int boysPresent) { this.boysPresent = boysPresent; }

    public int getGirlsPresent() { return girlsPresent; }
    public void setGirlsPresent(int girlsPresent) { this.girlsPresent = girlsPresent; }

    public int getBoysAbsent() { return boysAbsent; }
    public void setBoysAbsent(int boysAbsent) { this.boysAbsent = boysAbsent; }

    public int getGirlsAbsent() { return girlsAbsent; }
    public void setGirlsAbsent(int girlsAbsent) { this.girlsAbsent = girlsAbsent; }

    public int getTotalPresent() { return boysPresent + girlsPresent; }
    public int getTotalAbsent() { return boysAbsent + girlsAbsent; }
    public int getTotalBoys() { return boysPresent + boysAbsent; }
    public int getTotalGirls() { return girlsPresent + girlsAbsent; }
}
