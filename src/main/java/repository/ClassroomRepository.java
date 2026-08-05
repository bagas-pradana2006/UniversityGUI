package repository;

import database.DatabaseConnection;
import model.Classroom;
import structures.MyLinkedList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClassroomRepository {

    public void save(String building, String roomName, int capacity) {
        String query = "INSERT INTO classroom (building, room_name, capacity) VALUES (?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, building);
            stmt.setString(2, roomName);
            stmt.setInt(3, capacity);
            stmt.executeUpdate();
            System.out.println("[Database] Ruangan berhasil disimpan!");
        } catch (SQLException e) {
            System.out.println("[Database] Gagal menyimpan ruangan: " + e.getMessage());
        }
    }

    public MyLinkedList<Classroom> findAll() {
        MyLinkedList<Classroom> list = new MyLinkedList<>();
        String query = "SELECT classroom_id, building, room_name, capacity FROM classroom";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Classroom(
                        rs.getInt("classroom_id"),
                        rs.getString("building"),
                        rs.getString("room_name"),
                        rs.getInt("capacity")
                ));
            }
        } catch (SQLException e) {
            System.out.println("[Database] Gagal mengambil data ruangan: " + e.getMessage());
        }
        return list;
    }

    public void update(int classroomId, String building, String roomName, int capacity) {
        String query = "UPDATE classroom SET building=?, room_name=?, capacity=? WHERE classroom_id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, building);
            stmt.setString(2, roomName);
            stmt.setInt(3, capacity);
            stmt.setInt(4, classroomId);
            stmt.executeUpdate();
            System.out.println("[Database] Ruangan berhasil diupdate!");
        } catch (SQLException e) {
            System.out.println("[Database] Gagal update ruangan: " + e.getMessage());
        }
    }

    public void delete(int classroomId) {
        String query = "DELETE FROM classroom WHERE classroom_id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, classroomId);
            stmt.executeUpdate();
            System.out.println("[Database] Ruangan berhasil dihapus!");
        } catch (SQLException e) {
            System.out.println("[Database] Gagal hapus ruangan (mungkin masih dipakai di jadwal): " + e.getMessage());
        }
    }
}