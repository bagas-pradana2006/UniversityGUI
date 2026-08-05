package model;

public class User {
    private int userId;
    private String username;
    private String role;   // "ADMIN", "DOSEN", atau "MAHASISWA"
    private String refId;  // NIM kalau MAHASISWA, NIP kalau DOSEN, NULL kalau ADMIN

    public User(int userId, String username, String role, String refId) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.refId = refId;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getRefId() { return refId; }
}