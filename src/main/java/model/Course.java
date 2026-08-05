package model;

public class Course {
    private String courseId;
    private String courseName;
    private String faculty;
    private String department;
    private int credits;

    // Constructor lama (tanpa department) TETAP ADA supaya EnrollmentRepository.getAllCourses()
    // yang sudah ada tidak perlu diubah
    public Course(String courseId, String courseName, String faculty, int credits) {
        this(courseId, courseName, faculty, null, credits);
    }

    // Constructor baru, dipakai untuk fitur CRUD Mata Kuliah
    public Course(String courseId, String courseName, String faculty, String department, int credits) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.faculty = faculty;
        this.department = department;
        this.credits = credits;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getDepartment() {
        return department;
    }

    public int getCredits() {
        return credits;
    }

    // Ini yang membuat ComboBox menampilkan "Nama Matkul (SKS) — Fakultas"
    @Override
    public String toString() {
        return courseName + " (" + credits + " SKS) — " + faculty;
    }
}