package structures;

import model.Student;

public class MyHashTable {
    // Node khusus untuk Hash Table
    private static class HashNode {
        String key;
        Student value;
        HashNode next;

        public HashNode(String key, Student value) {
            this.key = key;
            this.value = value;
        }
    }

    private HashNode[] buckets;
    private int numBuckets;
    private int size;

    public MyHashTable(int capacity) {
        this.numBuckets = capacity;
        this.buckets = new HashNode[numBuckets];
        this.size = 0;
    }

    // Fungsi Hashing buatan sendiri
    private int getBucketIndex(String key) {
        int hashCode = key.hashCode();
        return Math.abs(hashCode) % numBuckets;
    }

    // Insert atau Update Data
    public void put(String key, Student value) {
        int index = getBucketIndex(key);
        HashNode head = buckets[index];

        // Cek kalau NIM sudah ada, kita timpa data lamanya (Update)
        while (head != null) {
            if (head.key.equals(key)) {
                head.value = value;
                return;
            }
            head = head.next;
        }

        // Kalau belum ada, masukkan sebagai Head baru di bucket tersebut
        size++;
        head = buckets[index];
        HashNode newNode = new HashNode(key, value);
        newNode.next = head;
        buckets[index] = newNode;
    }

    // Get Data (Pencarian O(1))
    public Student get(String key) {
        int index = getBucketIndex(key);
        HashNode head = buckets[index];

        // Langsung cek bucket yang sesuai
        while (head != null) {
            if (head.key.equals(key)) {
                return head.value;
            }
            head = head.next;
        }
        return null; // Tidak ditemukan
    }

    // Hapus Data dari Cache
    public void remove(String key) {
        int index = getBucketIndex(key);
        HashNode head = buckets[index];
        HashNode prev = null;

        while (head != null) {
            if (head.key.equals(key)) {
                if (prev == null) {
                    buckets[index] = head.next;
                } else {
                    prev.next = head.next;
                }
                size--;
                return;
            }
            prev = head;
            head = head.next;
        }
    }

    // Bersihkan semua isi Hash Table
    public void clear() {
        buckets = new HashNode[numBuckets];
        size = 0;
    }

    public int getSize() {
        return size;
    }
}