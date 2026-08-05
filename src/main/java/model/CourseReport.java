package model;

public class CourseReport {
    private String courseId;
    private String courseName;
    private int totalStudents;
    private Double averageScore;

    public CourseReport(String courseId, String courseName, int totalStudents, Double averageScore) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.totalStudents = totalStudents;
        this.averageScore = averageScore;
    }

    public String getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public int getTotalStudents() { return totalStudents; }
    public Double getAverageScore() { return averageScore; }
}