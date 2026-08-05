package repository;

import database.DatabaseConnection;
import model.EnrollmentData;
import model.Course;
import structures.MyLinkedList;
import util.GradeCalculator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnrollmentRepository {

    // KEAJAIBAN SQL JOIN ADA DI SINI
    public MyLinkedList<EnrollmentData> getStudentGrades(String nim) {
        MyLinkedList<EnrollmentData> grades = new MyLinkedList<>();

        // Kita gabungkan tabel enrollments (e) dengan tabel courses (c)
        String query = "SELECT c.course_id, c.course_name, c.credits, " +
                "e.tugas, e.quiz, e.uts, e.uas, e.final_score, e.grade " +
                "FROM enrollments e " +
                "JOIN courses c ON e.course_id = c.course_id " +
                "WHERE e.nim = ?";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nim);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                EnrollmentData data = new EnrollmentData(
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getInt("credits"),
                        getNullableDouble(rs, "tugas"),
                        getNullableDouble(rs, "quiz"),
                        getNullableDouble(rs, "uts"),
                        getNullableDouble(rs, "uas"),
                        getNullableDouble(rs, "final_score"),
                        rs.getString("grade")
                );
                grades.add(data);
            }
        } catch (SQLException e) {
            System.out.println("[Database] Gagal mengambil nilai KRS: " + e.getMessage());
        }

        return grades;
    }

    // --- FUNGSI MENAMBAH NILAI BARU (DARI 4 KOMPONEN, NILAI AKHIR & HURUF DIHITUNG OTOMATIS) ---
    public void addGrade(String nim, String courseId, double tugas, double quiz, double uts, double uas) {
        double finalScore = GradeCalculator.calculateFinalScore(tugas, quiz, uts, uas);
        String grade = GradeCalculator.calculateGrade(finalScore);

        String query = "INSERT INTO enrollments (nim, course_id, tugas, quiz, uts, uas, final_score, grade) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nim);
            stmt.setString(2, courseId);
            stmt.setDouble(3, tugas);
            stmt.setDouble(4, quiz);
            stmt.setDouble(5, uts);
            stmt.setDouble(6, uas);
            stmt.setDouble(7, finalScore);
            stmt.setString(8, grade);

            stmt.executeUpdate();
            System.out.println("[Database] Nilai berhasil ditambahkan untuk NIM: " + nim);
        } catch (SQLException e) {
            System.out.println("[Database] Gagal menambah nilai: " + e.getMessage());
        }
    }

    // --- FUNGSI UPDATE NILAI YANG SUDAH ADA (DARI 4 KOMPONEN) ---
    public void updateGrade(String nim, String courseId, double tugas, double quiz, double uts, double uas) {
        double finalScore = GradeCalculator.calculateFinalScore(tugas, quiz, uts, uas);
        String grade = GradeCalculator.calculateGrade(finalScore);

        String query = "UPDATE enrollments SET tugas=?, quiz=?, uts=?, uas=?, final_score=?, grade=? " +
                "WHERE nim = ? AND course_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDouble(1, tugas);
            stmt.setDouble(2, quiz);
            stmt.setDouble(3, uts);
            stmt.setDouble(4, uas);
            stmt.setDouble(5, finalScore);
            stmt.setString(6, grade);
            stmt.setString(7, nim);
            stmt.setString(8, courseId);

            stmt.executeUpdate();
            System.out.println("[Database] Nilai berhasil diupdate untuk NIM: " + nim);
        } catch (SQLException e) {
            System.out.println("[Database] Gagal mengupdate nilai: " + e.getMessage());
        }
    }

    // --- FUNGSI HAPUS NILAI ---
    public void deleteGrade(String nim, String courseId) {
        String query = "DELETE FROM enrollments WHERE nim = ? AND course_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nim);
            stmt.setString(2, courseId);

            stmt.executeUpdate();
            System.out.println("[Database] Nilai berhasil dihapus untuk NIM: " + nim);
        } catch (SQLException e) {
            System.out.println("[Database] Gagal menghapus nilai: " + e.getMessage());
        }
    }

    // --- FUNGSI HITUNG JUMLAH MAHASISWA YANG MENGAMBIL SUATU MATA KULIAH (UNTUK DASHBOARD DOSEN) ---
    public int countEnrollmentsByCourse(String courseId) {
        String query = "SELECT COUNT(*) AS total FROM enrollments WHERE course_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("[Database] Gagal menghitung jumlah mahasiswa per mata kuliah: " + e.getMessage());
        }
        return 0;
    }

    // --- FUNGSI "AMBIL MK" (KRS) -- HANYA DAFTAR, BELUM ADA NILAI SAMA SEKALI ---
    // tugas/quiz/uts/uas/final_score/grade dibiarkan NULL, diisi belakangan oleh dosen lewat updateGrade()
    public void enrollCourse(String nim, String courseId) {
        String query = "INSERT INTO enrollments (nim, course_id) VALUES (?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nim);
            stmt.setString(2, courseId);
            stmt.executeUpdate();
            System.out.println("[Database] Mahasiswa berhasil mengambil mata kuliah (KRS): " + courseId);
        } catch (SQLException e) {
            System.out.println("[Database] Gagal KRS: " + e.getMessage());
        }
    }

    // --- FUNGSI HITUNG TOTAL SKS YANG SEDANG BERJALAN (UNTUK VALIDASI KRS) ---
    // "Sedang berjalan" = enrollment yang belum punya final_score (belum dinilai dosen)
    public int getTotalOngoingSks(String nim) {
        String query = "SELECT COALESCE(SUM(c.credits), 0) AS total FROM enrollments e " +
                "JOIN courses c ON e.course_id = c.course_id " +
                "WHERE e.nim = ? AND e.final_score IS NULL";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nim);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("[Database] Gagal menghitung total SKS: " + e.getMessage());
        }
        return 0;
    }

    // --- FUNGSI MENGAMBIL SEMUA MATA KULIAH (UNTUK COMBOBOX) ---
    // Faculty diambil lewat JOIN department_id -> department -> faculty
    public MyLinkedList<Course> getAllCourses() {
        MyLinkedList<Course> courses = new MyLinkedList<>();
        String query = "SELECT c.course_id, c.course_name, c.credits, " +
                "COALESCE(f.faculty_name, c.faculty) AS faculty " +
                "FROM courses c " +
                "LEFT JOIN department d ON c.department_id = d.department_id " +
                "LEFT JOIN faculty f ON d.faculty_id = f.faculty_id";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Course course = new Course(
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getString("faculty"),
                        rs.getInt("credits")
                );
                courses.add(course);
            }
        } catch (SQLException e) {
            System.out.println("[Database] Gagal mengambil daftar mata kuliah: " + e.getMessage());
        }

        return courses;
    }

    // --- FUNGSI BANTUAN: BACA KOLOM FLOAT YANG BOLEH NULL DI DATABASE ---
    private Double getNullableDouble(ResultSet rs, String column) throws SQLException {
        double value = rs.getDouble(column);
        return rs.wasNull() ? null : value;
    }

    // =====================================================================
    // FITUR LAPORAN AGREGAT
    // =====================================================================

    // 1. Laporan Rekap per Mata Kuliah
    public MyLinkedList<model.CourseReport> getCourseAggregateReport() {
        MyLinkedList<model.CourseReport> reportList = new MyLinkedList<>();
        String query = "SELECT c.course_id, c.course_name, COUNT(e.nim) AS total_students, AVG(e.final_score) AS avg_score " +
                "FROM courses c " +
                "LEFT JOIN enrollments e ON c.course_id = e.course_id " +
                "GROUP BY c.course_id, c.course_name";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reportList.add(new model.CourseReport(
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getInt("total_students"),
                        getNullableDouble(rs, "avg_score")
                ));
            }
        } catch (SQLException e) {
            System.out.println("[Database] Gagal mengambil Laporan Mata Kuliah: " + e.getMessage());
        }
        return reportList;
    }

    // 2. Laporan Rekap per Fakultas
    public MyLinkedList<model.FacultyReport> getFacultyAggregateReport() {
        MyLinkedList<model.FacultyReport> reportList = new MyLinkedList<>();
        String query = "SELECT s.faculty, COUNT(DISTINCT s.nim) AS total_students, AVG(e.final_score) AS avg_score " +
                "FROM students s " +
                "LEFT JOIN enrollments e ON s.nim = e.nim " +
                "GROUP BY s.faculty";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reportList.add(new model.FacultyReport(
                        rs.getString("faculty"),
                        rs.getInt("total_students"),
                        getNullableDouble(rs, "avg_score")
                ));
            }
        } catch (SQLException e) {
            System.out.println("[Database] Gagal mengambil Laporan Fakultas: " + e.getMessage());
        }
        return reportList;
    }
}