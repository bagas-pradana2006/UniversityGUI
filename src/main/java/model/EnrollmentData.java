package model;

// Class ini adalah gabungan data untuk ditampilkan di tabel nilai
public class EnrollmentData {
    private String courseId;
    private String courseName;
    private int credits;
    private Double tugas;
    private Double quiz;
    private Double uts;
    private Double uas;
    private Double finalScore;
    private String grade;

    public EnrollmentData(String courseId, String courseName, int credits,
                          Double tugas, Double quiz, Double uts, Double uas,
                          Double finalScore, String grade) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credits = credits;
        this.tugas = tugas;
        this.quiz = quiz;
        this.uts = uts;
        this.uas = uas;
        this.finalScore = finalScore;
        this.grade = grade;
    }

    // Getter wajib ada untuk JavaFX TableView
    public String getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public int getCredits() { return credits; }
    public Double getTugas() { return tugas; }
    public Double getQuiz() { return quiz; }
    public Double getUts() { return uts; }
    public Double getUas() { return uas; }
    public Double getFinalScore() { return finalScore; }
    public String getGrade() { return grade; }
}