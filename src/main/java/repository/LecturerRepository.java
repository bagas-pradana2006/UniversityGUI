package repository;

import database.DatabaseConnection;
import model.Lecturer;
import structures.MyLinkedList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class LecturerRepository {

    // Menyimpan data dosen ke SQL Server
    public void save(Lecturer lecturer) {
        String query = "INSERT INTO lecturer (lecturer_id, name, department_id, email) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            Integer departmentId = resolveDepartmentId(conn, lecturer.getFaculty(), lecturer.getDepartment());

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, lecturer.getId());
            stmt.setString(2, lecturer.getName());
            if (departmentId != null) {
                stmt.setInt(3, departmentId);
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setString(4, lecturer.getEmail());

            stmt.executeUpdate();
            System.out.println("[Database] Data dosen berhasil disimpan ke SQL Server!");
        } catch (SQLException e) {
            System.out.println("[Database] Gagal menyimpan data dosen: " + e.getMessage());
        }
    }

    // Mengambil semua data dosen, JOIN ke department + faculty untuk dapat nama fakultas/jurusan
    public MyLinkedList<Lecturer> findAll() {
        MyLinkedList<Lecturer> lecturers = new MyLinkedList<>();
        String query = "SELECT l.lecturer_id, l.name, l.email, " +
                "f.faculty_name, d.department_name " +
                "FROM lecturer l " +
                "LEFT JOIN department d ON l.department_id = d.department_id " +
                "LEFT JOIN faculty f ON d.faculty_id = f.faculty_id";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Lecturer l = new Lecturer(
                        rs.getString("lecturer_id"),
                        rs.getString("name"),
                        rs.getString("faculty_name"),
                        rs.getString("department_name"),
                        rs.getString("email")
                );
                lecturers.add(l);
            }
        } catch (SQLException e) {
            System.out.println("[Database] Gagal mengambil data dosen: " + e.getMessage());
        }
        return lecturers;
    }

    // --- FITUR UPDATE DATA DOSEN ---
    public void update(Lecturer lecturer) {
        String query = "UPDATE lecturer SET name=?, department_id=?, email=? WHERE lecturer_id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            Integer departmentId = resolveDepartmentId(conn, lecturer.getFaculty(), lecturer.getDepartment());

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, lecturer.getName());
            if (departmentId != null) {
                stmt.setInt(2, departmentId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, lecturer.getEmail());
            stmt.setString(4, lecturer.getId());

            stmt.executeUpdate();
            System.out.println("[Database] Data dosen berhasil diupdate!");
        } catch (SQLException e) {
            System.out.println("[Database] Gagal update data dosen: " + e.getMessage());
        }
    }

    // --- FITUR HAPUS DATA DOSEN ---
    public void delete(String lecturerId) {
        String query = "DELETE FROM lecturer WHERE lecturer_id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, lecturerId);

            stmt.executeUpdate();
            System.out.println("[Database] Data dosen berhasil dihapus dari SQL Server!");
        } catch (SQLException e) {
            System.out.println("[Database] Gagal hapus data dosen: " + e.getMessage());
        }
    }

    // --- SAMA PERSIS DENGAN resolveDepartmentId DI StudentRepository ---
    // (kalau nanti mau dirapikan, ini bisa diekstrak jadi 1 class util bersama)
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