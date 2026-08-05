package model;

public abstract class Person {
    protected String id; // Bisa berupa NIM untuk Mahasiswa atau NIDN untuk Dosen
    protected String name;

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter & Setter (Encapsulation)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Abstract method (Polymorphism)
    public abstract void displayInfo();
}