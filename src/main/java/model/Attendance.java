package model;

public class Attendance {
    private String nim;
    private String courseId;
    private int meetingNum;
    private String status;

    public Attendance(String nim, String courseId, int meetingNum, String status) {
        this.nim = nim;
        this.courseId = courseId;
        this.meetingNum = meetingNum;
        this.status = status;
    }

    public String getNim() { return nim; }
    public String getCourseId() { return courseId; }
    public int getMeetingNum() { return meetingNum; }
    public String getStatus() { return status; }
}