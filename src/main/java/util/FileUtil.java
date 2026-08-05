package util;

import model.Student;
import structures.MyLinkedList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    // --- FITUR EXPORT ---
    public static void exportToCSV(MyLinkedList<Student> students, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("NIM,Nama,Fakultas,Jurusan,Semester\n");
            for (Student s : students) {
                writer.write(s.getId() + "," + s.getName() + "," + s.getFaculty() + "," +
                        s.getDepartment() + "," + s.getSemester() + "\n");
            }
            System.out.println("[File] Berhasil export data ke: " + filename);
        } catch (IOException e) {
            System.out.println("[File] Gagal export file: " + e.getMessage());
        }
    }

    // --- FITUR IMPORT (BARU) ---
    public static MyLinkedList<Student> importFromCSV(String filepath) {
        MyLinkedList<Student> importedStudents = new MyLinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line = br.readLine(); // Baca baris pertama (Header) dan abaikan

            while ((line = br.readLine()) != null) { // Baca baris selanjutnya
                String[] data = line.split(","); // Pecah berdasarkan tanda koma

                // Pastikan datanya lengkap (ada 5 kolom)
                if (data.length == 5) {
                    String nim = data[0];
                    String name = data[1];
                    String faculty = data[2];
                    String dept = data[3];
                    int semester = Integer.parseInt(data[4]);

                    importedStudents.add(new Student(nim, name, faculty, dept, semester));
                }
            }
            System.out.println("[File] Berhasil membaca file CSV!");
        } catch (Exception e) {
            System.out.println("[File] Gagal import file: " + e.getMessage());
        }

        return importedStudents;
    }
}