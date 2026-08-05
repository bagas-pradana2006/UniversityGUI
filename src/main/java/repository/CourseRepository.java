package repository;

import database.DatabaseConnection;
import model.Course;
import structures.MyLinkedList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class CourseRepository {

    // --- FUNGSI TAMBAH MATA KULIAH BARU ---
    public void save(Course course) {
        String query = "INSERT INTO courses (course_id, course_name, faculty, credits, department_id) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            Integer departmentId = resolveDepartmentId(conn, course.getFaculty(), course.getDepartment());

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, course.getCourseId());
            stmt.setString(2, course.getCourseName());
            stmt.setString(3, course.getFaculty());
            stmt.setInt(4, course.getCredits());
            if (departmentId != null) {
                stmt.setInt(5, departmentId);
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.executeUpdate();
            System.out.println("[Database] Mata kuliah berhasil disimpan!");
        } catch (SQLException e) {
            System.out.println("[Database] Gagal menyimpan mata kuliah: " + e.getMessage());
        }
    }

    // --- FUNGSI AMBIL SEMUA MATA KULIAH (UNTUK TABEL MANAJEMEN MATA KULIAH) ---
    public MyLinkedList<Course> findAll() {
        MyLinkedList<Course> courses = new MyLinkedList<>();
        String query = "SELECT c.course_id, c.course_name, c.credits, " +
                "COALESCE(f.faculty_name, c.faculty) AS faculty, d.department_name " +
                "FROM courses c " +
                "LEFT JOIN department d ON c.department_id = d.department_id " +
                "LEFT JOIN faculty f ON d.faculty_id = f.faculty_id";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Course c = new Course(
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getString("faculty"),
                        rs.getString("department_name"),
                        rs.getInt("credits")
                );
                courses.add(c);
            }
        } catch (SQLException e) {
            System.out.println("[Database] Gagal mengambil data mata kuliah: " + e.getMessage());
        }
        return courses;
    }

    // --- FUNGSI EDIT MATA KULIAH ---
    public void update(Course course) {
        String query = "UPDATE courses SET course_name=?, faculty=?, credits=?, department_id=? WHERE course_id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            Integer departmentId = resolveDepartmentId(conn, course.getFaculty(), course.getDepartment());

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, course.getCourseName());
            stmt.setString(2, course.getFaculty());
            stmt.setInt(3, course.getCredits());
            if (departmentId != null) {
                stmt.setInt(4, departmentId);
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setString(5, course.getCourseId());

            stmt.executeUpdate();
            System.out.println("[Database] Mata kuliah berhasil diupdate!");
        } catch (SQLException e) {
            System.out.println("[Database] Gagal update mata kuliah: " + e.getMessage());
        }
    }

    // --- FUNGSI HAPUS MATA KULIAH ---
    // Aman: enrollments.course_id sudah ON DELETE CASCADE dari desain awal database,
    // jadi nilai KRS yang terkait mata kuliah ini otomatis ikut terhapus.
    public void delete(String courseId) {
        String query = "DELETE FROM courses WHERE course_id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, courseId);

            stmt.executeUpdate();
            System.out.println("[Database] Mata kuliah berhasil dihapus!");
        } catch (SQLException e) {
            System.out.println("[Database] Gagal hapus mata kuliah: " + e.getMessage());
        }
    }

    // --- SAMA PERSIS DENGAN resolveDepartmentId DI StudentRepository/LecturerRepository ---
    private Integer resolveDepartmentId(Connection conn, String facultyName, String departmentName) throws SQLException {
        if (facultyName == null || facultyName.isBlank() || departmentName == null || departmentName.isBlank()) {
            return null;
        }

        int facultyId;
        String findFaculty = "SELECT faculty_id FROM faculty WHERE faculty_name = ?";
        try (PreparedStatement findFacStmt = conn.prepareStatement(findFaculty)) {
            findFacStmt.setString(1, facultyName);
            ResultSet facRs = findFacStmt.executeQuery();
            if (facRs.next()) {
                facultyId = facRs.getInt("faculty_id");
            } else {
                String insertFaculty = "INSERT INTO faculty (faculty_name) VALUES (?)";
                try (PreparedStatement insFacStmt = conn.prepareStatement(insertFaculty, Statement.RETURN_GENERATED_KEYS)) {
                    insFacStmt.setString(1, facultyName);
                    insFacStmt.executeUpdate();
                    ResultSet keys = insFacStmt.getGeneratedKeys();
                    keys.next();
                    facultyId = keys.getInt(1);
                }
            }
        }

        int departmentId;
        String findDept = "SELECT department_id FROM department WHERE department_name = ? AND faculty_id = ?";
        try (PreparedStatement findDeptStmt = conn.prepareStatement(findDept)) {
            findDeptStmt.setString(1, departmentName);
            findDeptStmt.setInt(2, facultyId);
            ResultSet deptRs = findDeptStmt.executeQuery();
            if (deptRs.next()) {
                departmentId = deptRs.getInt("department_id");
            } else {
                String insertDept = "INSERT INTO department (department_name, faculty_id) VALUES (?, ?)";
                try (PreparedStatement insDeptStmt = conn.prepareStatement(insertDept, Statement.RETURN_GENERATED_KEYS)) {
                    insDeptStmt.setString(1, departmentName);
                    insDeptStmt.setInt(2, facultyId);
                    insDeptStmt.executeUpdate();
                    ResultSet keys = insDeptStmt.getGeneratedKeys();
                    keys.next();
                    departmentId = keys.getInt(1);
                }
            }
        }

        return departmentId;
    }
}