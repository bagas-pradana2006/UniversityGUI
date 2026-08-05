package service;

import interfaces.Searchable;
import interfaces.Sortable;
import model.EnrollmentData;
import model.Student;
import repository.EnrollmentRepository;
import repository.StudentRepository;
import structures.MyLinkedList;
import structures.MyStack;
import structures.MyHashTable;
import structures.MyBST;
import structures.Node;
import util.FileUtil;

public class StudentService implements Searchable<Student>, Sortable {
    private MyLinkedList<Student> studentList;
    private StudentRepository repository;
    private MyStack<Student> deletedStudentsStack;
    private MyHashTable studentCache;

    // --- BST UNTUK PENCARIAN O(log N) ---
    private MyBST studentBST;

    public StudentService() {
        repository = new StudentRepository();
        deletedStudentsStack = new MyStack<>();
        studentCache = new MyHashTable(100);
        studentBST = new MyBST();
        loadFromDatabase();
    }

    private void loadFromDatabase() {
        studentList = repository.findAll();

        studentCache.clear();
        studentBST.clear();

        // 1. Hitung jumlah data dan simpan ke Hash Table (Sangat Cepat O(1))
        int count = 0;
        for (Student s : studentList) {
            studentCache.put(s.getId(), s);
            count++;
        }

        // 2. Pindahkan ke Array sementara untuk diacak
        Student[] arr = new Student[count];
        int i = 0;
        for (Student s : studentList) {
            arr[i++] = s;
        }

        // 3. ALGORITMA FISHER-YATES SHUFFLE (Acak urutan data)
        // Ini adalah kunci rahasia agar BST tidak menjadi garis lurus dan bikin PC ngelag!
        java.util.Random rand = new java.util.Random();
        for (int j = arr.length - 1; j > 0; j--) {
            int index = rand.nextInt(j + 1);
            Student temp = arr[index];
            arr[index] = arr[j];
            arr[j] = temp;
        }

        // 4. Masukkan data yang sudah diacak ke dalam BST (O(N log N) - Sangat Cepat!)
        for (Student s : arr) {
            studentBST.insert(s);
        }
    }

    public void addStudent(Student student) {
        repository.save(student);
        studentList.add(student);
        studentCache.put(student.getId(), student);
        studentBST.insert(student);
    }

    public MyLinkedList<Student> getAllStudents() { return studentList; }
    public void exportData() { FileUtil.exportToCSV(studentList, "Data_Mahasiswa.csv"); }

    @Override
    public Student search(String keyword) { return studentCache.get(keyword); }

    public Student searchByCache(String nim) { return studentCache.get(nim); }

    // PENCARIAN DENGAN BST
    public Student searchByBST(String nim) { return studentBST.search(nim); }

    public MyLinkedList<Student> searchByKeyword(String keyword, String mode) {
        MyLinkedList<Student> results = new MyLinkedList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Student s : studentList) {
            boolean match;
            switch (mode) {
                case "Nama": match = s.getName().toLowerCase().contains(lowerKeyword); break;
                case "Fakultas": match = s.getFaculty().toLowerCase().contains(lowerKeyword); break;
                case "NIM":
                default: match = s.getId().toLowerCase().contains(lowerKeyword); break;
            }
            if (match) results.add(s);
        }
        return results;
    }

    public Student binarySearch(String keyword) {
        Student[] arr = toArray();
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int cmp = arr[mid].getId().compareToIgnoreCase(keyword);
            if (cmp == 0) return arr[mid];
            if (cmp < 0) left = mid + 1;
            else right = mid - 1;
        }
        return null;
    }

    public String executeAdvancedSort(String algorithm) {
        long startTime = System.nanoTime();
        switch (algorithm) {
            case "Selection Sort": selectionSort(); break;
            case "Insertion Sort": insertionSort(); break;
            case "Merge Sort": mergeSort(); break;
            case "Quick Sort": quickSort(); break;
            case "Bubble Sort": default: sort(); break;
        }
        long endTime = System.nanoTime();
        long durationNs = (endTime - startTime);
        long durationMs = durationNs / 1000000;
        if (durationMs > 0) return durationMs + " ms (" + durationNs + " ns)";
        return durationNs + " ns";
    }

    @Override
    public void sort() {
        if (studentList.isEmpty() || studentList.getHead().next == null) return;
        boolean swapped;
        do {
            swapped = false;
            Node<Student> current = studentList.getHead();
            while (current.next != null) {
                if (current.data.getId().compareTo(current.next.data.getId()) > 0) {
                    Student temp = current.data;
                    current.data = current.next.data;
                    current.next.data = temp;
                    swapped = true;
                }
                current = current.next;
            }
        } while (swapped);
    }

    public void selectionSort() {
        if (studentList.isEmpty() || studentList.getHead().next == null) return;
        Node<Student> current = studentList.getHead();
        while (current != null) {
            Node<Student> minNode = current;
            Node<Student> search = current.next;
            while (search != null) {
                if (search.data.getId().compareTo(minNode.data.getId()) < 0) minNode = search;
                search = search.next;
            }
            Student temp = current.data;
            current.data = minNode.data;
            minNode.data = temp;
            current = current.next;
        }
    }

    private Student[] toArray() {
        int size = 0;
        for (Student s : studentList) size++;
        Student[] arr = new Student[size];
        int i = 0;
        for (Student s : studentList) arr[i++] = s;
        return arr;
    }

    private void rebuildFromArray(Student[] arr) {
        int i = 0;
        Node<Student> curr = studentList.getHead();
        while (curr != null) {
            curr.data = arr[i++];
            curr = curr.next;
        }
    }

    public void insertionSort() {
        if (studentList.isEmpty() || studentList.getHead().next == null) return;
        Student[] arr = toArray();
        for (int i = 1; i < arr.length; i++) {
            Student key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j].getId().compareTo(key.getId()) > 0) {
                arr[j + 1] = arr[j];
                j = j - 1;
            }
            arr[j + 1] = key;
        }
        rebuildFromArray(arr);
    }

    public void mergeSort() {
        if (studentList.isEmpty() || studentList.getHead().next == null) return;
        Student[] arr = toArray();
        mergeSortHelper(arr, 0, arr.length - 1);
        rebuildFromArray(arr);
    }

    private void mergeSortHelper(Student[] arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSortHelper(arr, left, mid);
            mergeSortHelper(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    private void merge(Student[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        Student[] L = new Student[n1];
        Student[] R = new Student[n2];
        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);
        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L[i].getId().compareTo(R[j].getId()) <= 0) arr[k++] = L[i++];
            else arr[k++] = R[j++];
        }
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    public void quickSort() {
        if (studentList.isEmpty() || studentList.getHead().next == null) return;
        Student[] arr = toArray();
        quickSortHelper(arr, 0, arr.length - 1);
        rebuildFromArray(arr);
    }

    private void quickSortHelper(Student[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSortHelper(arr, low, pi - 1);
            quickSortHelper(arr, pi + 1, high);
        }
    }

    private int partition(Student[] arr, int low, int high) {
        String pivot = arr[high].getId();
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (arr[j].getId().compareTo(pivot) < 0) {
                i++;
                Student temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        Student temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    public void sortByName() {
        if (studentList.isEmpty() || studentList.getHead().next == null) return;
        boolean swapped;
        do {
            swapped = false;
            Node<Student> current = studentList.getHead();
            while (current.next != null) {
                if (current.data.getName().compareToIgnoreCase(current.next.data.getName()) > 0) {
                    Student temp = current.data;
                    current.data = current.next.data;
                    current.next.data = temp;
                    swapped = true;
                }
                current = current.next;
            }
        } while (swapped);
    }

    public void sortByIPK(EnrollmentRepository enrollmentRepo) {
        if (studentList.isEmpty() || studentList.getHead().next == null) return;
        boolean swapped;
        do {
            swapped = false;
            Node<Student> current = studentList.getHead();
            while (current.next != null) {
                double ipkCurrent = calculateIPK(enrollmentRepo, current.data.getId());
                double ipkNext = calculateIPK(enrollmentRepo, current.next.data.getId());
                if (ipkCurrent < ipkNext) {
                    Student temp = current.data;
                    current.data = current.next.data;
                    current.next.data = temp;
                    swapped = true;
                }
                current = current.next;
            }
        } while (swapped);
    }

    private double calculateIPK(EnrollmentRepository enrollmentRepo, String nim) {
        double totalBobot = 0;
        int totalSks = 0;
        for (EnrollmentData data : enrollmentRepo.getStudentGrades(nim)) {
            totalBobot += gradeToPoin(data.getGrade()) * data.getCredits();
            totalSks += data.getCredits();
        }
        return totalSks == 0 ? 0.0 : totalBobot / totalSks;
    }

    private double gradeToPoin(String grade) {
        if (grade == null) return 0.0;
        switch (grade.trim().toUpperCase()) {
            case "A": return 4.0;
            case "B": return 3.0;
            case "C": return 2.0;
            case "D": return 1.0;
            case "E": return 0.0;
            default: return 0.0;
        }
    }

    public void importData(String filepath) {
        MyLinkedList<Student> newStudents = FileUtil.importFromCSV(filepath);
        int successCount = 0;
        for (Student s : newStudents) {
            if (search(s.getId()) == null) {
                addStudent(s);
                successCount++;
            }
        }
        System.out.println("Berhasil import " + successCount + " data baru.");
    }

    public void updateStudent(Student student) {
        repository.update(student);
        loadFromDatabase();
    }

    public void deleteStudent(String nim) {
        Student target = search(nim);
        if (target != null) {
            deletedStudentsStack.push(target);
        }
        repository.delete(nim);
        loadFromDatabase();
    }

    public Student undoDelete() {
        if (deletedStudentsStack.isEmpty()) return null;
        Student restoredStudent = deletedStudentsStack.pop();
        addStudent(restoredStudent);
        return restoredStudent;
    }
}