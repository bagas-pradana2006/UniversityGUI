package repository;

import database.DatabaseConnection;
import structures.MyLinkedList;
import model.Attendance;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AttendanceRepository {

    public void saveAttendance(String nim, String courseId, int meetingNum, String status) {
        // Upsert logic: Update kalau sudah ada, Insert kalau belum
        String checkQuery = "SELECT * FROM attendance WHERE nim = ? AND course_id = ? AND meeting_num = ?";
        String updateQuery = "UPDATE attendance SET status = ? WHERE nim = ? AND course_id = ? AND meeting_num = ?";
        String insertQuery = "INSERT INTO attendance (nim, course_id, meeting_num, status) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, nim); checkStmt.setString(2, courseId); checkStmt.setInt(3, meetingNum);

            if (checkStmt.executeQuery().next()) {
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, status); updateStmt.setString(2, nim); updateStmt.setString(3, courseId); updateStmt.setInt(4, meetingNum);
                updateStmt.executeUpdate();
            } else {
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, nim); insertStmt.setString(2, courseId); insertStmt.setInt(3, meetingNum); insertStmt.setString(4, status);
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("[Database] Gagal simpan presensi: " + e.getMessage());
        }
    }

    public MyLinkedList<Attendance> getAttendance(String nim, String courseId) {
        MyLinkedList<Attendance> list = new MyLinkedList<>();
        String query = "SELECT * FROM attendance WHERE nim = ? AND course_id = ? ORDER BY meeting_num ASC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nim); stmt.setString(2, courseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Attendance(rs.getString("nim"), rs.getString("course_id"), rs.getInt("meeting_num"), rs.getString("status")));
            }
        } catch (SQLException e) { }
        return list;
    }
}