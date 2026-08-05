package model;

public class Lecturer extends Person {
    private String faculty;
    private String department;
    private String email;

    public Lecturer(String lecturerId, String name, String faculty, String department, String email) {
        super(lecturerId, name); // NIP dipakai sebagai id, sama pola dengan Student pakai NIM
        this.faculty = faculty;
        this.department = department;
        this.email = email;
    }

    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public void displayInfo() {
        System.out.println("--- Data Dosen ---");
        System.out.println("NIP      : " + id);
        System.out.println("Nama     : " + name);
        System.out.println("Fakultas : " + faculty);
        System.out.println("Jurusan  : " + department);
        System.out.println("Email    : " + email);
        System.out.println("------------------");
    }
}