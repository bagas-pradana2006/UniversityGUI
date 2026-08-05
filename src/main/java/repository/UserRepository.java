package repository;

import database.DatabaseConnection;
import model.User;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    // Cek username + password (di-hash dulu) ke tabel users
    public User login(String username, String password) {
        String hashedInput = PasswordUtil.hash(password);
        String query = "SELECT user_id, username, role, ref_id FROM users WHERE username = ? AND password_hash = ?";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, hashedInput);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("ref_id")
                );
            }
        } catch (SQLException e) {
            System.out.println("[Database] Gagal login: " + e.getMessage());
        }
        return null; // Username/password salah
    }
}