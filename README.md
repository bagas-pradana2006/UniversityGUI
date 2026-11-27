# 🎓 SIAKAD: Advanced University Management System

[![Java](https://img.shields.io/badge/-Java_17+-ED8B00?logo=openjdk&logoColor=white)](#)
[![JavaFX](https://img.shields.io/badge/-JavaFX-FF9900?logo=java&logoColor=white)](#)
[![SQL Server](https://img.shields.io/badge/-SQL_Server-CC2927?logo=microsoft-sql-server&logoColor=white)](#)
[![Data Structures](https://img.shields.io/badge/-Advanced_Algorithms-00ADD8?logo=databricks&logoColor=white)](#)


> **Sistem Informasi Akademik berskala Enterprise** | **60.000+ Data Mahasiswa** | **7 Struktur Data & Algoritma Lanjut** | **$O(1)$ Caching System**

---

<div align="center">

**🌐 Language**
**[Indonesian](README.md)** | [English (WIP)](#) 

</div>

---

**SIAKAD (Sistem Informasi Akademik)** ini bukan sekadar aplikasi CRUD biasa. Proyek ini dibangun sebagai demonstrasi implementasi **Struktur Data dan Algoritma (SDA)** di dunia nyata. 

Mampu menangani dan merender lebih dari **60.000+ baris data** secara instan berkat penggunaan sistem *caching* kustom, pengurutan asinkronus, dan manajemen memori yang dioptimalkan. Sangat cocok sebagai referensi *Final Project* (Tugas Akhir) mata kuliah Pemrograman Berorientasi Objek (PBO) maupun Struktur Data.

---

## 🚀 Fitur Utama & Struktur Data (The Engine)

Aplikasi ini menggunakan struktur data murni (*custom-built from scratch*) tanpa bergantung pada *library Collection* bawaan Java untuk logika bisnis utamanya:

| Struktur Data / Algoritma | Implementasi di Aplikasi | Kompleksitas |
|---------------------------|--------------------------|--------------|
| **Hash Table** ⚡ | Sistem *caching* untuk pencarian instan Mahasiswa berdasarkan NIM. | **$O(1)$** |
| **Binary Search Tree (BST)** 🌳 | Pohon pencarian yang diseimbangkan dengan *Fisher-Yates Shuffle* untuk *indexing* data dari database. | **$O(\log N)$** |
| **Queue (FIFO)** 🚶‍♂️ | Sistem Antrean pengajuan KRS. Mahasiswa mendaftar, Admin menyetujui secara berurutan. | **$O(1)$** |
| **Graph** 🕸️ | Pemetaan Relasi Prasyarat Mata Kuliah (*Prerequisites*). Mencegah KRS jika syarat belum lulus. | **$O(V + E)$** |
| **Stack (LIFO)** ⏪ | Sistem *Undo*. Jika Admin salah menghapus mahasiswa, cukup klik "Undo" untuk memulihkan data. | **$O(1)$** |
| **Benchmarking Sorting** ⏱️ | Pengukur waktu eksekusi (*nanoseconds*) untuk komparasi: *Bubble, Selection, Insertion, Merge, Quick Sort*. | **$O(N \log N)$** |
| **Binary Search** 🔍 | Algoritma pencarian alternatif membelah array terurut. | **$O(\log N)$** |

---

## 👥 Role-Based Access Control (RBAC)

Aplikasi ini mendukung *multi-login* dengan antarmuka dan *Dashboard* yang menyesuaikan secara dinamis berdasarkan *Role*:

*   👑 **ADMIN:** Memiliki akses penuh (Dosen, Ruangan, Jadwal). Dapat melihat statistik global kampus (Pie Chart & Bar Chart), dan menyetujui Antrean KRS.
*   💼 **DOSEN:** Dashboard menampilkan jadwal mengajar hari ini. Hanya dapat mengakses Data Mahasiswa, menginput nilai (otomatis terkonversi ke Grade A-E), dan mengisi Presensi.
*   🎓 **MAHASISWA:** Dashboard menampilkan IPK dan SKS berjalan. Hanya dapat melihat transkrip nilai miliknya sendiri dan melakukan "Ambil MK" (KRS).

---

## 📦 Arsitektur Proyek (What's Inside)

Proyek ini menerapkan prinsip **Clean Code** dan **MVC (Model-View-Controller)**.

```text
SIAKAD-JavaFX/
|-- src/main/java/
|   |-- controller/        # JavaFX Controllers (Logika UI & Event Listeners)
|   |-- model/             # Entitas Data (Student, Course, Lecturer, Attendance)
|   |-- repository/        # Logika SQL & JDBC (Akses langsung ke SQL Server)
|   |-- service/           # Logika Bisnis, Penghubung Repo & Controller
|   |-- structures/        # Kumpulan Algoritma Murni (Inti dari Proyek Ini)
|   |   |-- MyBST.java         # Iterative Binary Search Tree
|   |   |-- MyGraph.java       # Relasi Prasyarat MK
|   |   |-- MyHashTable.java   # Custom Chaining Hash Table
|   |   |-- MyLinkedList.java  # Custom Linked List
|   |   |-- MyQueue.java       # Antrean KRS
|   |   |-- MyStack.java       # Undo System
|   |-- util/              # Helper (Export CSV, Hitung Grade)
|-- src/main/resources/
|   |-- fxml/              # Desain UI JavaFX
|   |-- css/               # Styling Aplikasi
|-- sql/                   # Python Script Google Colab untuk Generate 60k+ Data
