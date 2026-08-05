package structures;

// Menggunakan Generic <T> agar bisa dipakai untuk class apa saja (Student, Course, dll)
public class Node<T> {
    public T data;
    public Node<T> next;

    public Node(T data) {
        this.data = data;
        this.next = null;
    }
}