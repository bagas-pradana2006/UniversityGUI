package repository;

import database.DatabaseConnection;
import model.Student;
import structures.MyLinkedList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class StudentRepository {

    // Menyimpan data mahasiswa ke SQL Server
    public void save(Student student) {
        String query = "INSERT INTO students (nim, name, faculty, department, semester, department_id) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            Integer departmentId = resolveDepartmentId(conn, student.getFaculty(), student.getDepartment());

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, student.getId());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getFaculty());
            stmt.setString(4, student.getDepartment());
            stmt.setInt(5, student.getSemester());
            if (departmentId != null) {
                stmt.setInt(6, departmentId);
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.executeUpdate(); // Eksekusi perintah INSERT
            System.out.println("[Database] Data berhasil disimpan ke SQL Server!");
        } catch (SQLException e) {
            System.out.println("[Database] Gagal menyimpan data: " + e.getMessage());
        }
    }

    // Mengambil semua data dari SQL Server untuk dimasukkan ke MyLinkedList
    // Sekarang JOIN ke department + faculty lewat department_id (bukan baca kolom teks lama langsung).
    // COALESCE dipasang sebagai jaring pengaman kalau ada baris lama yang department_id-nya masih NULL.
    public MyLinkedList<Student> findAll() {
        MyLinkedList<Student> students = new MyLinkedList<>();
        String query = "SELECT s.nim, s.name, s.semester, " +
                "COALESCE(f.faculty_name, s.faculty) AS faculty, " +
                "COALESCE(d.department_name, s.department) AS department " +
                "FROM students s " +
                "LEFT JOIN department d ON s.department_id = d.department_id " +
                "LEFT JOIN faculty f ON d.faculty_id = f.faculty_id";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery(); // Ambil data

            while (rs.next()) {
                Student s = new Student(
                        rs.getString("nim"),
                        rs.getString("name"),
                        rs.getString("faculty"),
                        rs.getString("department"),
                        rs.getInt("semester")
                );
                students.add(s);
            }
        } catch (SQLException e) {
            System.out.println("[Database] Gagal mengambil data: " + e.getMessage());
        }
        return students;
    }

    // --- FITUR UPDATE DATA ---
    public void update(Student student) {
        // SQL UPDATE: Mengubah data berdasarkan NIM (Primary Key)
        String query = "UPDATE students SET name=?, faculty=?, department=?, semester=?, department_id=? WHERE nim=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            Integer departmentId = resolveDepartmentId(conn, student.getFaculty(), student.getDepartment());

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getFaculty());
            stmt.setString(3, student.getDepartment());
            stmt.setInt(4, student.getSemester());
            if (departmentId != null) {
                stmt.setInt(5, departmentId);
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setString(6, student.getId()); // NIM ada di parameter ke-6

            stmt.executeUpdate();
            System.out.println("[Database] Data mahasiswa berhasil diupdate!");
        } catch (SQLException e) {
            System.out.println("[Database] Gagal update data: " + e.getMessage());
        }
    }

    // --- FITUR DELETE DATA ---
    public void delete(String nim) {
        // SQL DELETE: Menghapus data berdasarkan NIM
        String query = "DELETE FROM students WHERE nim=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nim);

            stmt.executeUpdate();
            System.out.println("[Database] Data mahasiswa berhasil dihapus dari SQL Server!");
        } catch (SQLException e) {
            System.out.println("[Database] Gagal hapus data: " + e.getMessage());
        }
    }

    // --- FUNGSI BANTUAN: CARI department_id YANG SESUAI, OTOMATIS BUAT BARU KALAU BELUM ADA ---
    // Ini "jembatan" supaya form yang masih pakai teks bebas (faculty/department)
    // tetap otomatis konsisten dengan tabel faculty & department yang baru.
    private Integer resolveDepartmentId(Connection conn, String facultyName, String departmentName) throws SQLException {
        if (facultyName == null || facultyName.isBlank() || departmentName == null || departmentName.isBlank()) {
            return null;
        }

        // 1. Cari faculty_id, kalau belum ada -> buat baru
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
                    System.out.println("[Database] Fakultas baru otomatis dibuat: " + facultyName);
                }
            }
        }

        // 2. Cari department_id, kalau belum ada -> buat baru
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
                    System.out.println("[Database] Jurusan baru otomatis dibuat: " + departmentName);
                }
            }
        }

        return departmentId;
    }
}