package structures;
import model.Student;

public class MyBST {
    private class TreeNode {
        Student data;
        TreeNode left, right;
        TreeNode(Student data) { this.data = data; }
    }
    private TreeNode root;

    // KITA UBAH DARI RECURSIVE MENJADI ITERATIVE (ANTI STACK-OVERFLOW)
    public void insert(Student student) {
        TreeNode newNode = new TreeNode(student);
        if (root == null) {
            root = newNode;
            return;
        }

        TreeNode current = root;
        TreeNode parent = null;

        while (true) {
            parent = current;
            if (student.getId().compareToIgnoreCase(current.data.getId()) < 0) {
                current = current.left;
                if (current == null) {
                    parent.left = newNode;
                    return;
                }
            } else if (student.getId().compareToIgnoreCase(current.data.getId()) > 0) {
                current = current.right;
                if (current == null) {
                    parent.right = newNode;
                    return;
                }
            } else {
                return; // Data duplicate (NIM sama), abaikan
            }
        }
    }

    public Student search(String nim) {
        // Search juga diubah jadi Iterative supaya makin kenceng
        TreeNode current = root;
        while (current != null) {
            if (current.data.getId().equalsIgnoreCase(nim)) {
                return current.data;
            }
            if (current.data.getId().compareToIgnoreCase(nim) > 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return null; // Tidak ditemukan
    }

    public void clear() { root = null; }
}