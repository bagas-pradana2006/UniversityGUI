package model;

// Gabungan data jadwal untuk ditampilkan di tabel (hasil JOIN course/lecturer/classroom)
public class Schedule {
    private int scheduleId;
    private String courseId;
    private String courseName;
    private String lecturerName; // "-" kalau belum ditentukan
    private String roomLabel;    // "-" kalau belum ditentukan
    private String dayOfWeek;
    private String startTime;
    private String endTime;

    public Schedule(int scheduleId, String courseId, String courseName, String lecturerName,
                    String roomLabel, String dayOfWeek, String startTime, String endTime) {
        this.scheduleId = scheduleId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.lecturerName = lecturerName;
        this.roomLabel = roomLabel;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getScheduleId() { return scheduleId; }
    public String getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public String getLecturerName() { return lecturerName; }
    public String getRoomLabel() { return roomLabel; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
}