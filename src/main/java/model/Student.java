package model;

public class Student extends Person {
    private String faculty;
    private String department;
    private int semester;

    public Student(String nim, String name, String faculty, String department, int semester) {
        super(nim, name); // Memanggil constructor dari class induk (Person)
        this.faculty = faculty;
        this.department = department;
        this.semester = semester;
    }

    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    @Override
    public void displayInfo() {
        System.out.println("--- Data Mahasiswa ---");
        System.out.println("NIM      : " + id);
        System.out.println("Nama     : " + name);
        System.out.println("Fakultas : " + faculty);
        System.out.println("Jurusan  : " + department);
        System.out.println("Semester : " + semester);
        System.out.println("----------------------");
    }
}