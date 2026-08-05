package repository;

import database.DatabaseConnection;
import model.Schedule;
import structures.MyLinkedList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ScheduleRepository {

    // --- CEK BENTROK: ruangan atau dosen yang sama, di hari & jam yang tumpang tindih ---
    // excludeScheduleId dipakai saat EDIT, biar jadwal itu sendiri tidak dianggap "bentrok sama dirinya sendiri"
    // Return null kalau aman, atau pesan penjelasan kalau bentrok.
    public String checkConflict(String day, String startTime, String endTime,
                                String lecturerId, Integer classroomId, Integer excludeScheduleId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            int exclude = (excludeScheduleId == null) ? -1 : excludeScheduleId;

            if (classroomId != null) {
                String q = "SELECT c.course_name FROM schedule s " +
                        "JOIN courses c ON s.course_id = c.course_id " +
                        "WHERE s.day_of_week = ? AND s.classroom_id = ? " +
                        "AND s.start_time < ? AND ? < s.end_time AND s.schedule_id <> ?";
                try (PreparedStatement stmt = conn.prepareStatement(q)) {
                    stmt.setString(1, day);
                    stmt.setInt(2, classroomId);
                    stmt.setString(3, endTime);
                    stmt.setString(4, startTime);
                    stmt.setInt(5, exclude);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        return "Ruangan ini sudah dipakai mata kuliah \"" + rs.getString("course_name") + "\" di jam yang tumpang tindih!";
                    }
                }
            }

            if (lecturerId != null && !lecturerId.isBlank()) {
                String q = "SELECT c.course_name FROM schedule s " +
                        "JOIN courses c ON s.course_id = c.course_id " +
                        "WHERE s.day_of_week = ? AND s.lecturer_id = ? " +
                        "AND s.start_time < ? AND ? < s.end_time AND s.schedule_id <> ?";
                try (PreparedStatement stmt = conn.prepareStatement(q)) {
                    stmt.setString(1, day);
                    stmt.setString(2, lecturerId);
                    stmt.setString(3, endTime);
                    stmt.setString(4, startTime);
                    stmt.setInt(5, exclude);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        return "Dosen ini sudah mengajar mata kuliah \"" + rs.getString("course_name") + "\" di jam yang tumpang tindih!";
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("[Database] Gagal cek bentrok jadwal: " + e.getMessage());
        }
        return null; // Aman, tidak ada bentrok
    }

    public void save(String courseId, String lecturerId, Integer classroomId, String day, String startTime, String endTime) {
        String query = "INSERT INTO schedule (course_id, lecturer_id, classroom_id, day_of_week, start_time, end_time) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, courseId);
            if (lecturerId != null && !lecturerId.isBlank()) stmt.setString(2, lecturerId); else stmt.setNull(2, Types.VARCHAR);
            if (classroomId != null) stmt.setInt(3, classroomId); else stmt.setNull(3, Types.INTEGER);
            stmt.setString(4, day);
            stmt.setString(5, startTime);
            stmt.setString(6, endTime);
            stmt.executeUpdate();
            System.out.println("[Database] Jadwal berhasil disimpan!");
        } catch (SQLException e) {
            System.out.println("[Database] Gagal menyimpan jadwal: " + e.getMessage());
        }
    }

    // --- FUNGSI AMBIL JADWAL MILIK 1 DOSEN SAJA (UNTUK DASHBOARD DOSEN) ---
    public MyLinkedList<Schedule> findByLecturer(String lecturerId) {
        MyLinkedList<Schedule> list = new MyLinkedList<>();
        String query = "SELECT s.schedule_id, s.course_id, c.course_name, l.name AS lecturer_name, " +
                "cr.building, cr.room_name, s.day_of_week, s.start_time, s.end_time " +
                "FROM schedule s " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "LEFT JOIN lecturer l ON s.lecturer_id = l.lecturer_id " +
                "LEFT JOIN classroom cr ON s.classroom_id = cr.classroom_id " +
                "WHERE s.lecturer_id = ? " +
                "ORDER BY CASE s.day_of_week " +
                "WHEN 'Senin' THEN 1 WHEN 'Selasa' THEN 2 WHEN 'Rabu' THEN 3 " +
                "WHEN 'Kamis' THEN 4 WHEN 'Jumat' THEN 5 WHEN 'Sabtu' THEN 6 ELSE 7 END, s.start_time";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, lecturerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String lecturerName = rs.getString("lecturer_name");
                String building = rs.getString("building");
                String roomName = rs.getString("room_name");
                String roomLabel = (building == null) ? "-" : building + " - " + roomName;

                list.add(new Schedule(
                        rs.getInt("schedule_id"),
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        lecturerName == null ? "-" : lecturerName,
                        roomLabel,
                        rs.getString("day_of_week"),
                        rs.getString("start_time"),
                        rs.getString("end_time")
                ));
            }
        } catch (SQLException e) {
            System.out.println("[Database] Gagal mengambil jadwal dosen: " + e.getMessage());
        }
        return list;
    }

    // Diurutkan Senin -> Sabtu, lalu berdasarkan jam mulai
    public MyLinkedList<Schedule> findAll() {
        MyLinkedList<Schedule> list = new MyLinkedList<>();
        String query = "SELECT s.schedule_id, s.course_id, c.course_name, l.name AS lecturer_name, " +
                "cr.building, cr.room_name, s.day_of_week, s.start_time, s.end_time " +
                "FROM schedule s " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "LEFT JOIN lecturer l ON s.lecturer_id = l.lecturer_id " +
                "LEFT JOIN classroom cr ON s.classroom_id = cr.classroom_id " +
                "ORDER BY CASE s.day_of_week " +
                "WHEN 'Senin' THEN 1 WHEN 'Selasa' THEN 2 WHEN 'Rabu' THEN 3 " +
                "WHEN 'Kamis' THEN 4 WHEN 'Jumat' THEN 5 WHEN 'Sabtu' THEN 6 ELSE 7 END, s.start_time";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String lecturerName = rs.getString("lecturer_name");
                String building = rs.getString("building");
                String roomName = rs.getString("room_name");
                String roomLabel = (building == null) ? "-" : building + " - " + roomName;

                list.add(new Schedule(
                        rs.getInt("schedule_id"),
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        lecturerName == null ? "-" : lecturerName,
                        roomLabel,
                        rs.getString("day_of_week"),
                        rs.getString("start_time"),
                        rs.getString("end_time")
                ));
            }
        } catch (SQLException e) {
            System.out.println("[Database] Gagal mengambil data jadwal: " + e.getMessage());
        }
        return list;
    }

    public void update(int scheduleId, String courseId, String lecturerId, Integer classroomId, String day, String startTime, String endTime) {
        String query = "UPDATE schedule SET course_id=?, lecturer_id=?, classroom_id=?, day_of_week=?, start_time=?, end_time=? " +
                "WHERE schedule_id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, courseId);
            if (lecturerId != null && !lecturerId.isBlank()) stmt.setString(2, lecturerId); else stmt.setNull(2, Types.VARCHAR);
            if (classroomId != null) stmt.setInt(3, classroomId); else stmt.setNull(3, Types.INTEGER);
            stmt.setString(4, day);
            stmt.setString(5, startTime);
            stmt.setString(6, endTime);
            stmt.setInt(7, scheduleId);
            stmt.executeUpdate();
            System.out.println("[Database] Jadwal berhasil diupdate!");
        } catch (SQLException e) {
            System.out.println("[Database] Gagal update jadwal: " + e.getMessage());
        }
    }

    public void delete(int scheduleId) {
        String query = "DELETE FROM schedule WHERE schedule_id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, scheduleId);
            stmt.executeUpdate();
            System.out.println("[Database] Jadwal berhasil dihapus!");
        } catch (SQLException e) {
            System.out.println("[Database] Gagal hapus jadwal: " + e.getMessage());
        }
    }
}