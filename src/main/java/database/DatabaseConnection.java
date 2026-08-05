package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // URL untuk terhubung ke SQL Server di komputermu (localhost)
    private static final String URL = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=university_db;"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    // UBAH BAGIAN INI:
    // Jika kamu login SSMS pakai username "sa", biarkan "sa".
    // Tapi passwordnya wajib diganti dengan password SSMS kamu!
    private static final String USER = "sa";
    private static final String PASSWORD = "admin1234";

    private static Connection connection = null;

    private DatabaseConnection() {} // Singleton

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[Sistem] HORE! Koneksi ke SQL Server Berhasil! 🎉");
            } catch (SQLException e) {
                System.out.println("[Sistem] Yaaah, Gagal koneksi ke SQL Server!");
                e.printStackTrace();
            }
        }
        return connection;
    }

    // METHOD MAIN SEMENTARA (Hanya untuk ngetes koneksi)
    public static void main(String[] args) {
        // Saat file ini di-run, dia akan mencoba memanggil koneksi
        getConnection();
    }
}