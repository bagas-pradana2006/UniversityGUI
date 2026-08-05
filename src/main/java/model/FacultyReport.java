package model;

public class FacultyReport {
    private String facultyName;
    private int totalStudents;
    private Double averageScore;

    public FacultyReport(String facultyName, int totalStudents, Double averageScore) {
        this.facultyName = facultyName;
        this.totalStudents = totalStudents;
        this.averageScore = averageScore;
    }

    public String getFacultyName() { return facultyName; }
    public int getTotalStudents() { return totalStudents; }
    public Double getAverageScore() { return averageScore; }
}