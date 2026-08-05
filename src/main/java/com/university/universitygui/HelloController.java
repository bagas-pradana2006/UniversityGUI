package com.university.universitygui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import model.Student;
import model.EnrollmentData;
import model.Course;
import model.Lecturer;
import model.User;
import model.Classroom;
import model.Schedule;
import model.CourseReport;
import model.FacultyReport;
import model.Attendance;
import service.StudentService;
import repository.LecturerRepository;
import repository.CourseRepository;
import repository.ClassroomRepository;
import repository.ScheduleRepository;
import repository.EnrollmentRepository;
import repository.AttendanceRepository;

// IMPORT STRUKTUR DATA BARU KITA
import structures.MyQueue;
import structures.MyGraph;

public class HelloController {

    @FXML private TabPane mainTabPane;
    @FXML private Tab dashboardTab, dataMahasiswaTab, nilaiTab, dosenTab, courseManagementTab, ruanganTab, jadwalTab, statistikTab, riwayatTab;
    @FXML private Button btnAddStudent, btnEditStudent, btnDeleteStudent, btnUndoDelete, btnImportStudent, btnEnrollCourse, btnInputNilai, btnDeleteGrade;

    // Tombol Baru untuk Admin (Approve KRS)
    @FXML private Button btnApproveKrs;

    @FXML private Label dashboardGreetingLabel, dashboardSubtitleLabel, card1Title, card1Value, card2Title, card2Value, card3Title, card3Value, card4Title, card4Value, card5Title, card5Value;
    @FXML private VBox card1Box, card2Box, card3Box, card4Box, card5Box;

    @FXML private TextField nimField, nameField, facultyField, deptField, semesterField, searchField;
    @FXML private ComboBox<String> searchModeComboBox, searchAlgorithmComboBox, sortAlgorithmComboBox;
    @FXML private Label statusLabel;

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> colNim, colName, colFaculty, colDept, colIPK;
    @FXML private TableColumn<Student, Integer> colSemester;

    @FXML private TableView<EnrollmentData> gradeTable;
    @FXML private TableColumn<EnrollmentData, String> colCourseId, colCourseName, colGrade;
    @FXML private TableColumn<EnrollmentData, Integer> colCredits;
    @FXML private TableColumn<EnrollmentData, Double> colTugas, colQuiz, colUts, colUas, colFinalScore;
    @FXML private Label gradeTitleLabel, ipkLabel;
    @FXML private ComboBox<Course> courseComboBox;
    @FXML private TextField tugasField, quizField, utsField, uasField;

    @FXML private TextField lecturerIdField, lecturerNameField, lecturerFacultyField, lecturerDeptField, lecturerEmailField;
    @FXML private TableView<Lecturer> lecturerTable;
    @FXML private TableColumn<Lecturer, String> colLecturerId, colLecturerName, colLecturerFaculty, colLecturerDept, colLecturerEmail;

    @FXML private TextField courseIdMgmtField, courseNameMgmtField, courseFacultyMgmtField, courseDeptMgmtField, courseCreditsMgmtField;
    @FXML private TableView<Course> courseManagementTable;
    @FXML private TableColumn<Course, String> colMgmtCourseId, colMgmtCourseName, colMgmtFaculty, colMgmtDept;
    @FXML private TableColumn<Course, Integer> colMgmtCredits;

    @FXML private TextField buildingField, roomNameField, capacityField;
    @FXML private TableView<Classroom> classroomTable;
    @FXML private TableColumn<Classroom, String> colClassroomBuilding, colClassroomRoom;
    @FXML private TableColumn<Classroom, Integer> colClassroomCapacity;

    @FXML private ComboBox<Course> scheduleCourseComboBox;
    @FXML private ComboBox<Lecturer> scheduleLecturerComboBox;
    @FXML private ComboBox<Classroom> scheduleRoomComboBox;
    @FXML private ComboBox<String> scheduleDayComboBox;
    @FXML private TextField scheduleStartField, scheduleEndField;
    @FXML private TableView<Schedule> scheduleTable;
    @FXML private TableColumn<Schedule, String> colScheduleDay, colScheduleTime, colScheduleCourse, colScheduleLecturer, colScheduleRoom;

    @FXML private PieChart facultyChart;
    @FXML private BarChart<String, Number> avgIpkChart, semesterChart;
    @FXML private ListView<String> historyListView;

    @FXML private Tab laporanTab;
    @FXML private ComboBox<String> reportTypeComboBox;
    @FXML private TableView<CourseReport> courseReportTable;
    @FXML private TableColumn<CourseReport, String> colRepCourseId, colRepCourseName, colRepCourseAvg;
    @FXML private TableColumn<CourseReport, Integer> colRepCourseStudents;
    @FXML private TableView<FacultyReport> facultyReportTable;
    @FXML private TableColumn<FacultyReport, String> colRepFacultyName, colRepFacultyAvg;
    @FXML private TableColumn<FacultyReport, Integer> colRepFacultyStudents;

    // --- KOMPONEN FXML TAB PRESENSI BARU ---
    @FXML private Tab presensiTab;
    @FXML private TextField presensiNimField;
    @FXML private ComboBox<Course> presensiCourseComboBox;
    @FXML private ComboBox<Integer> presensiMeetingComboBox;
    @FXML private ComboBox<String> presensiStatusComboBox;
    @FXML private TableView<Attendance> attendanceTable;
    @FXML private TableColumn<Attendance, Integer> colAttMeeting;
    @FXML private TableColumn<Attendance, String> colAttStatus;

    private StudentService studentService;
    private EnrollmentRepository enrollmentRepo = new EnrollmentRepository();
    private AttendanceRepository attendanceRepo = new AttendanceRepository();
    private ObservableList<String> historyLog = FXCollections.observableArrayList();
    private static final String LOG_FILE = "riwayat_perubahan.log";
    private LecturerRepository lecturerRepo = new LecturerRepository();
    private CourseRepository courseRepo = new CourseRepository();
    private ClassroomRepository classroomRepo = new ClassroomRepository();
    private ScheduleRepository scheduleRepo = new ScheduleRepository();
    private User currentUser;
    private Integer selectedClassroomId, selectedScheduleId;

    // --- INSTANSIASI STRUKTUR DATA QUEUE DAN GRAPH ---
    private MyQueue<String[]> krsQueue = new MyQueue<>();
    private MyGraph prereqGraph = new MyGraph();

    @FXML
    public void initialize() {
        // CONTOH DATA GRAPH (PRASYARAT MATA KULIAH)
        prereqGraph.addPrerequisite("IF201", "IF101");
        prereqGraph.addPrerequisite("IF301", "IF201");

        colNim.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colFaculty.setCellValueFactory(new PropertyValueFactory<>("faculty"));
        colDept.setCellValueFactory(new PropertyValueFactory<>("department"));
        colSemester.setCellValueFactory(new PropertyValueFactory<>("semester"));
        colIPK.setCellValueFactory(cellData -> {
            if(studentService == null) return new SimpleStringProperty("-");
            String nim = cellData.getValue().getId();
            double ipk = calculateIPKForStudent(nim);
            return new SimpleStringProperty(ipk == 0.0 ? "-" : String.format("%.2f", ipk));
        });

        colCourseId.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colCredits.setCellValueFactory(new PropertyValueFactory<>("credits"));
        colTugas.setCellValueFactory(new PropertyValueFactory<>("tugas"));
        colQuiz.setCellValueFactory(new PropertyValueFactory<>("quiz"));
        colUts.setCellValueFactory(new PropertyValueFactory<>("uts"));
        colUas.setCellValueFactory(new PropertyValueFactory<>("uas"));
        colFinalScore.setCellValueFactory(new PropertyValueFactory<>("finalScore"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        colLecturerId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLecturerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colLecturerFaculty.setCellValueFactory(new PropertyValueFactory<>("faculty"));
        colLecturerDept.setCellValueFactory(new PropertyValueFactory<>("department"));
        colLecturerEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colMgmtCourseId.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        colMgmtCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colMgmtFaculty.setCellValueFactory(new PropertyValueFactory<>("faculty"));
        colMgmtDept.setCellValueFactory(new PropertyValueFactory<>("department"));
        colMgmtCredits.setCellValueFactory(new PropertyValueFactory<>("credits"));

        colClassroomBuilding.setCellValueFactory(new PropertyValueFactory<>("building"));
        colClassroomRoom.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        colClassroomCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        colScheduleDay.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
        colScheduleTime.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getStartTime() + " - " + cellData.getValue().getEndTime()));
        colScheduleCourse.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colScheduleLecturer.setCellValueFactory(new PropertyValueFactory<>("lecturerName"));
        colScheduleRoom.setCellValueFactory(new PropertyValueFactory<>("roomLabel"));

        colRepCourseId.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        colRepCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colRepCourseStudents.setCellValueFactory(new PropertyValueFactory<>("totalStudents"));
        colRepCourseAvg.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getAverageScore() == null ? "-" : String.format("%.2f", cellData.getValue().getAverageScore())
        ));

        colRepFacultyName.setCellValueFactory(new PropertyValueFactory<>("facultyName"));
        colRepFacultyStudents.setCellValueFactory(new PropertyValueFactory<>("totalStudents"));
        colRepFacultyAvg.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getAverageScore() == null ? "-" : String.format("%.2f", cellData.getValue().getAverageScore())
        ));

        // --- SETUP KOLOM DAN DROPDOWN PRESENSI ---
        colAttMeeting.setCellValueFactory(new PropertyValueFactory<>("meetingNum"));
        colAttStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        ObservableList<Integer> meetings = FXCollections.observableArrayList();
        for(int i=1; i<=16; i++) meetings.add(i);
        presensiMeetingComboBox.setItems(meetings);
        presensiStatusComboBox.setItems(FXCollections.observableArrayList("Hadir", "Izin", "Sakit", "Alpa"));

        presensiCourseComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal != null && !presensiNimField.getText().isEmpty()) {
                loadAttendanceData(presensiNimField.getText(), newVal.getCourseId());
            }
        });

        reportTypeComboBox.setItems(FXCollections.observableArrayList("Rekap per Mata Kuliah", "Rekap per Fakultas"));
        reportTypeComboBox.setValue("Rekap per Mata Kuliah");
        searchModeComboBox.setItems(FXCollections.observableArrayList("NIM", "Nama", "Fakultas"));
        searchModeComboBox.setValue("NIM");

        searchAlgorithmComboBox.setItems(FXCollections.observableArrayList(
                "Linear Search", "Binary Search", "Hash Table (O(1))", "Binary Search Tree (O(log N))"
        ));
        searchAlgorithmComboBox.setValue("Hash Table (O(1))");

        sortAlgorithmComboBox.setItems(FXCollections.observableArrayList(
                "Bubble Sort", "Selection Sort", "Insertion Sort", "Merge Sort", "Quick Sort"
        ));
        sortAlgorithmComboBox.setValue("Quick Sort");

        lecturerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                lecturerIdField.setText(newSelection.getId());
                lecturerNameField.setText(newSelection.getName());
                lecturerFacultyField.setText(newSelection.getFaculty());
                lecturerDeptField.setText(newSelection.getDepartment());
                lecturerEmailField.setText(newSelection.getEmail());
                lecturerIdField.setDisable(true);
            }
        });

        courseManagementTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                courseIdMgmtField.setText(newSelection.getCourseId());
                courseNameMgmtField.setText(newSelection.getCourseName());
                courseFacultyMgmtField.setText(newSelection.getFaculty());
                courseDeptMgmtField.setText(newSelection.getDepartment());
                courseCreditsMgmtField.setText(String.valueOf(newSelection.getCredits()));
                courseIdMgmtField.setDisable(true);
            }
        });

        classroomTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedClassroomId = newSelection.getClassroomId();
                buildingField.setText(newSelection.getBuilding());
                roomNameField.setText(newSelection.getRoomName());
                capacityField.setText(String.valueOf(newSelection.getCapacity()));
            }
        });

        scheduleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedScheduleId = newSelection.getScheduleId();
                for (Course c : scheduleCourseComboBox.getItems()) {
                    if (c.getCourseId().equals(newSelection.getCourseId())) {
                        scheduleCourseComboBox.setValue(c);
                        break;
                    }
                }
                scheduleDayComboBox.setValue(newSelection.getDayOfWeek());
                scheduleStartField.setText(newSelection.getStartTime());
                scheduleEndField.setText(newSelection.getEndTime());
            }
        });

        gradeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                for (Course c : courseComboBox.getItems()) {
                    if (c.getCourseId().equals(newSelection.getCourseId())) {
                        courseComboBox.setValue(c);
                        break;
                    }
                }
                tugasField.setText(newSelection.getTugas() == null ? "" : String.valueOf(newSelection.getTugas()));
                quizField.setText(newSelection.getQuiz() == null ? "" : String.valueOf(newSelection.getQuiz()));
                utsField.setText(newSelection.getUts() == null ? "" : String.valueOf(newSelection.getUts()));
                uasField.setText(newSelection.getUas() == null ? "" : String.valueOf(newSelection.getUas()));
            }
        });

        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && studentService != null) {
                nimField.setText(newSelection.getId());
                nameField.setText(newSelection.getName());
                facultyField.setText(newSelection.getFaculty());
                deptField.setText(newSelection.getDepartment());
                semesterField.setText(String.valueOf(newSelection.getSemester()));
                nimField.setDisable(true);

                gradeTitleLabel.setText("Transkrip Nilai: " + newSelection.getName());
                loadStudentGrades(newSelection.getId());
                loadCourseComboBox(newSelection.getFaculty());

                // --- UPDATE DROPDOWN PRESENSI KETIKA MAHASISWA DIKLIK ---
                presensiNimField.setText(newSelection.getId());
                ObservableList<Course> enrolledCourses = FXCollections.observableArrayList();
                for(EnrollmentData ed : enrollmentRepo.getStudentGrades(newSelection.getId())) {
                    for(Course c : courseRepo.findAll()) {
                        if(c.getCourseId().equals(ed.getCourseId())) { enrolledCourses.add(c); break; }
                    }
                }
                presensiCourseComboBox.setItems(enrolledCourses);
                attendanceTable.getItems().clear();

            } else {
                gradeTitleLabel.setText("Transkrip Nilai: (Pilih mahasiswa di tabel atas)");
                gradeTable.getItems().clear();
                ipkLabel.setText("IPK: -");
                loadCourseComboBox(null);
                presensiNimField.clear();
                presensiCourseComboBox.setItems(null);
                attendanceTable.getItems().clear();
            }
        });

        historyListView.setItems(historyLog);

        // --- PANGGIL DATA DI BACKGROUND AGAR UI TIDAK NOT RESPONDING ---
        loadDataInBackground();
    }

    // =====================================================================
    // BACKGROUND LOADER (AGAR UI TIDAK NOT RESPONDING SAAT LOAD 60.000 DATA)
    // =====================================================================
    private void loadDataInBackground() {
        statusLabel.setText("Status: Sedang memuat puluhan ribu data dari database... Harap tunggu.");

        btnAddStudent.setDisable(true);
        btnEditStudent.setDisable(true);
        btnDeleteStudent.setDisable(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Proses berat menarik ribuan data, hash table, dan BST dilakukan di sini
                studentService = new StudentService();

                Platform.runLater(() -> {
                    refreshTable();
                    refreshLecturerTable();
                    refreshCourseManagementTable();
                    refreshClassroomTable();
                    refreshScheduleTable();
                    loadScheduleDropdowns();
                    loadCourseComboBox();
                    populateDashboard();

                    btnAddStudent.setDisable(false);
                    btnEditStudent.setDisable(false);
                    btnDeleteStudent.setDisable(false);
                    statusLabel.setText("Status: Aplikasi siap digunakan. Total mahasiswa termuat: " + studentTable.getItems().size());
                });
                return null;
            }
        };

        new Thread(task).start();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        applyRoleRestrictions();
    }

    private void applyRoleRestrictions() {
        if (currentUser == null) return;

        switch (currentUser.getRole()) {
            case "DOSEN":
                mainTabPane.getTabs().remove(dosenTab);
                mainTabPane.getTabs().remove(courseManagementTab);
                mainTabPane.getTabs().remove(ruanganTab);
                mainTabPane.getTabs().remove(jadwalTab);
                mainTabPane.getTabs().remove(riwayatTab);
                mainTabPane.getTabs().remove(laporanTab);
                btnAddStudent.setDisable(true); btnEditStudent.setDisable(true); btnDeleteStudent.setDisable(true);
                btnUndoDelete.setDisable(true); btnImportStudent.setDisable(true); btnEnrollCourse.setDisable(true);

                if(btnApproveKrs != null) {
                    btnApproveKrs.setVisible(false); btnApproveKrs.setManaged(false);
                }
                statusLabel.setText("Status: Login sebagai Dosen (" + currentUser.getUsername() + ").");
                break;

            case "MAHASISWA":
                mainTabPane.getTabs().remove(dosenTab);
                mainTabPane.getTabs().remove(courseManagementTab);
                mainTabPane.getTabs().remove(ruanganTab);
                mainTabPane.getTabs().remove(jadwalTab);
                mainTabPane.getTabs().remove(riwayatTab);
                mainTabPane.getTabs().remove(statistikTab);
                mainTabPane.getTabs().remove(dataMahasiswaTab);
                mainTabPane.getTabs().remove(laporanTab);
                btnInputNilai.setDisable(true); btnUndoDelete.setDisable(true);

                if(btnApproveKrs != null) {
                    btnApproveKrs.setVisible(false); btnApproveKrs.setManaged(false);
                }
                loadOwnStudentData(currentUser.getRefId());
                statusLabel.setText("Status: Login sebagai Mahasiswa (" + currentUser.getUsername() + ").");
                break;

            case "ADMIN":
            default:
                if(btnApproveKrs != null) {
                    btnApproveKrs.setVisible(true); btnApproveKrs.setManaged(true);
                }
                statusLabel.setText("Status: Login sebagai Admin (" + currentUser.getUsername() + ").");
                break;
        }
        if(studentService != null) {
            populateDashboard();
        }
    }

    private void populateDashboard() {
        setCardVisible(card1Box, true); setCardVisible(card2Box, true); setCardVisible(card3Box, true);
        setCardVisible(card4Box, true); setCardVisible(card5Box, true);

        if (currentUser == null) { populateDashboardAdmin(); return; }

        switch (currentUser.getRole()) {
            case "DOSEN": populateDashboardDosen(); break;
            case "MAHASISWA": populateDashboardMahasiswa(); break;
            case "ADMIN": default: populateDashboardAdmin(); break;
        }
    }

    private void populateDashboardAdmin() {
        dashboardGreetingLabel.setText("Selamat datang, Admin!");
        dashboardSubtitleLabel.setText("Ringkasan sistem University Management System");

        int totalMahasiswa = 0; double totalIpk = 0; int mahasiswaDenganIpk = 0;
        if(studentService != null) {
            for (Student s : studentService.getAllStudents()) {
                totalMahasiswa++;
                double ipk = calculateIPKForStudent(s.getId());
                if (ipk > 0) { totalIpk += ipk; mahasiswaDenganIpk++; }
            }
        }
        double rataRataIpk = (mahasiswaDenganIpk == 0) ? 0 : totalIpk / mahasiswaDenganIpk;

        int totalDosen = 0; for (Lecturer l : lecturerRepo.findAll()) totalDosen++;
        int totalMataKuliah = 0; for (Course c : courseRepo.findAll()) totalMataKuliah++;
        int totalRuangan = 0; for (Classroom c : classroomRepo.findAll()) totalRuangan++;

        card1Title.setText("Jumlah Mahasiswa"); card1Value.setText(String.valueOf(totalMahasiswa));
        card2Title.setText("Jumlah Dosen"); card2Value.setText(String.valueOf(totalDosen));
        card3Title.setText("Jumlah Mata Kuliah"); card3Value.setText(String.valueOf(totalMataKuliah));
        card4Title.setText("Jumlah Ruangan"); card4Value.setText(String.valueOf(totalRuangan));
        card5Title.setText("Rata-rata IPK"); card5Value.setText(rataRataIpk == 0 ? "-" : String.format("%.2f", rataRataIpk));
    }

    private void populateDashboardDosen() {
        dashboardGreetingLabel.setText("Selamat datang, " + currentUser.getUsername() + "!");
        String hariIni = getHariIniIndonesia();
        dashboardSubtitleLabel.setText("Hari ini: " + hariIni);
        String lecturerId = currentUser.getRefId();
        int kelasHariIni = 0; int totalMahasiswaHariIni = 0;
        java.util.Set<String> mataKuliahDiampu = new java.util.HashSet<>();

        if (lecturerId != null && !lecturerId.isBlank()) {
            for (Schedule s : scheduleRepo.findByLecturer(lecturerId)) {
                mataKuliahDiampu.add(s.getCourseId());
                if (s.getDayOfWeek().equalsIgnoreCase(hariIni)) {
                    kelasHariIni++;
                    totalMahasiswaHariIni += enrollmentRepo.countEnrollmentsByCourse(s.getCourseId());
                }
            }
        }
        card1Title.setText("Kelas Hari Ini"); card1Value.setText(String.valueOf(kelasHariIni));
        card2Title.setText("Mahasiswa Hari Ini"); card2Value.setText(String.valueOf(totalMahasiswaHariIni));
        card3Title.setText("Total Mata Kuliah Diampu"); card3Value.setText(String.valueOf(mataKuliahDiampu.size()));
        setCardVisible(card4Box, false); setCardVisible(card5Box, false);
    }

    private void populateDashboardMahasiswa() {
        String nim = currentUser.getRefId();
        Student sendiri = (nim == null || nim.isBlank() || studentService == null) ? null : studentService.search(nim);
        dashboardGreetingLabel.setText("Halo, " + (sendiri != null ? sendiri.getName() : currentUser.getUsername()) + "!");
        dashboardSubtitleLabel.setText("Ringkasan akademik kamu");
        double ipk = 0; int totalSksLulus = 0; int mkBerjalan = 0;

        if (nim != null && !nim.isBlank()) {
            ipk = calculateIPKForStudent(nim);
            for (EnrollmentData data : enrollmentRepo.getStudentGrades(nim)) {
                if (data.getGrade() != null) totalSksLulus += data.getCredits();
                else mkBerjalan++;
            }
        }
        card1Title.setText("IPK"); card1Value.setText(ipk == 0 ? "-" : String.format("%.2f", ipk));
        card2Title.setText("Total SKS Lulus"); card2Value.setText(String.valueOf(totalSksLulus));
        card3Title.setText("MK Sedang Diambil"); card3Value.setText(String.valueOf(mkBerjalan));
        setCardVisible(card4Box, false); setCardVisible(card5Box, false);
    }

    private void setCardVisible(VBox card, boolean visible) {
        card.setVisible(visible); card.setManaged(visible);
    }

    private String getHariIniIndonesia() {
        java.time.DayOfWeek hari = java.time.LocalDate.now().getDayOfWeek();
        switch (hari) {
            case MONDAY: return "Senin"; case TUESDAY: return "Selasa"; case WEDNESDAY: return "Rabu";
            case THURSDAY: return "Kamis"; case FRIDAY: return "Jumat"; case SATURDAY: return "Sabtu";
            default: return "Minggu";
        }
    }

    private void loadOwnStudentData(String nim) {
        if (nim == null || nim.isBlank() || studentService == null) return;
        Student sendiri = studentService.search(nim);
        if (sendiri == null) return;
        nimField.setText(sendiri.getId()); nameField.setText(sendiri.getName());
        facultyField.setText(sendiri.getFaculty()); deptField.setText(sendiri.getDepartment());
        semesterField.setText(String.valueOf(sendiri.getSemester()));
        nimField.setDisable(true);
        gradeTitleLabel.setText("Transkrip Nilai: " + sendiri.getName());
        loadStudentGrades(sendiri.getId());
        loadCourseComboBox(sendiri.getFaculty());
        mainTabPane.getSelectionModel().select(nilaiTab);
    }

    private void loadStudentGrades(String nim) {
        ObservableList<EnrollmentData> gradeList = FXCollections.observableArrayList();
        for (EnrollmentData data : enrollmentRepo.getStudentGrades(nim)) gradeList.add(data);
        gradeTable.setItems(gradeList);
        calculateAndDisplayIPK(gradeList);
    }

    private void calculateAndDisplayIPK(ObservableList<EnrollmentData> gradeList) {
        double totalBobot = 0; int totalSks = 0;
        for (EnrollmentData data : gradeList) {
            if (data.getGrade() == null) continue;
            double poin = gradeToPoin(data.getGrade());
            totalBobot += poin * data.getCredits();
            totalSks += data.getCredits();
        }
        if (totalSks == 0) ipkLabel.setText("IPK: -");
        else ipkLabel.setText(String.format("IPK: %.2f", totalBobot / totalSks));
    }

    private double calculateIPKForStudent(String nim) {
        double totalBobot = 0; int totalSks = 0;
        for (EnrollmentData data : enrollmentRepo.getStudentGrades(nim)) {
            if (data.getGrade() == null) continue;
            totalBobot += gradeToPoin(data.getGrade()) * data.getCredits();
            totalSks += data.getCredits();
        }
        return totalSks == 0 ? 0.0 : totalBobot / totalSks;
    }

    private double gradeToPoin(String grade) {
        if (grade == null) return 0.0;
        switch (grade.trim().toUpperCase()) {
            case "A": return 4.0; case "B": return 3.0; case "C": return 2.0; case "D": return 1.0;
            case "E": return 0.0; default: return 0.0;
        }
    }

    private void loadCourseComboBox() { loadCourseComboBox(null); }

    private void loadCourseComboBox(String facultyFilter) {
        ObservableList<Course> courseList = FXCollections.observableArrayList();
        for (Course course : enrollmentRepo.getAllCourses()) {
            if (facultyFilter == null || facultyFilter.isBlank()) courseList.add(course);
            else {
                String a = course.getFaculty().toLowerCase(), b = facultyFilter.toLowerCase();
                if (a.contains(b) || b.contains(a)) courseList.add(course);
            }
        }
        courseComboBox.setItems(courseList);
    }

    @FXML
    protected void onAddButtonClick() {
        if (studentService == null) return;
        try {
            String nim = nimField.getText(); String name = nameField.getText();
            String faculty = facultyField.getText(); String dept = deptField.getText();
            int semester = Integer.parseInt(semesterField.getText());

            if (nim.isEmpty() || name.isEmpty()) { statusLabel.setText("Status: NIM dan Nama tidak boleh kosong!"); return; }
            if (semester < 1 || semester > 14) { statusLabel.setText("Status: Semester harus di antara 1 dan 14!"); return; }
            if (studentService.search(nim) != null) { statusLabel.setText("Status: NIM sudah terdaftar!"); return; }

            Student newStudent = new Student(nim, name, faculty, dept, semester);
            studentService.addStudent(newStudent);

            onClearButtonClick(); refreshTable();
            logActivity("Tambah mahasiswa baru: " + name + " (NIM " + nim + ")");
            statusLabel.setText("Status: Data mahasiswa berhasil ditambahkan!");
        } catch (NumberFormatException e) {
            statusLabel.setText("Status: Error! Semester harus berupa angka.");
        }
    }

    @FXML
    protected void onClearButtonClick() {
        nimField.clear(); nameField.clear(); facultyField.clear(); deptField.clear(); semesterField.clear();
        nimField.setDisable(false); studentTable.getSelectionModel().clearSelection();
        statusLabel.setText("Status: Form dibersihkan.");
    }

    @FXML
    protected void onEditButtonClick() {
        if (nimField.getText().isEmpty() || studentService == null) return;
        try {
            int semester = Integer.parseInt(semesterField.getText());
            String nim = nimField.getText();
            Student updatedStudent = new Student(nim, nameField.getText(), facultyField.getText(), deptField.getText(), semester);
            studentService.updateStudent(updatedStudent);
            refreshTable(); onClearButtonClick();
            logActivity("Edit data mahasiswa NIM " + nim);
            statusLabel.setText("Status: Data berhasil di-update!");
        } catch (NumberFormatException e) {
            statusLabel.setText("Status: Semester harus angka!");
        }
    }

    @FXML
    protected void onDeleteButtonClick() {
        String nim = nimField.getText();
        if (nim.isEmpty() || studentService == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Hapus data ini?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            String namaDihapus = nameField.getText();
            studentService.deleteStudent(nim);
            refreshTable(); onClearButtonClick();
            logActivity("Hapus mahasiswa: " + namaDihapus + " (NIM " + nim + ")");
            statusLabel.setText("Status: Data berhasil dihapus permanen!");
        }
    }

    @FXML
    protected void onUndoDeleteButtonClick() {
        if(studentService == null) return;
        Student restored = studentService.undoDelete();
        if (restored != null) {
            refreshTable();
            logActivity("Undo Hapus Mahasiswa: " + restored.getName() + " (NIM " + restored.getId() + ")");
            statusLabel.setText("Status: Data " + restored.getName() + " berhasil dikembalikan (Undo)!");
        } else {
            statusLabel.setText("Status: Tidak ada data yang bisa di-undo.");
        }
    }

    @FXML
    protected void onAdvancedSortButtonClick() {
        if(studentService == null) return;
        String algo = sortAlgorithmComboBox.getValue();
        if (algo == null) algo = "Bubble Sort";
        String executionTime = studentService.executeAdvancedSort(algo);
        refreshTable();
        logActivity("Mengurutkan data pakai " + algo + ". Waktu: " + executionTime);
        statusLabel.setText("Status: Data berhasil diurutkan dengan " + algo + ". Eksekusi: " + executionTime);
    }

    @FXML
    protected void onSortByNameButtonClick() {
        if(studentService != null) { studentService.sortByName(); refreshTable(); }
    }

    @FXML
    protected void onSortByIPKButtonClick() {
        if(studentService != null) { studentService.sortByIPK(enrollmentRepo); refreshTable(); }
    }

    @FXML
    protected void onExportButtonClick() { if(studentService != null) studentService.exportData(); }

    @FXML
    protected void onImportButtonClick() {
        if(studentService == null) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(nimField.getScene().getWindow());
        if (file != null) {
            studentService.importData(file.getAbsolutePath());
            refreshTable();
        }
    }

    @FXML
    protected void onSearchButtonClick() {
        String keyword = searchField.getText();
        if(keyword.isEmpty() || studentService == null){ refreshTable(); return; }

        String mode = searchModeComboBox.getValue();
        if (mode == null) mode = "NIM";

        String searchAlgo = searchAlgorithmComboBox.getValue();
        if (searchAlgo == null) searchAlgo = "Linear Search";

        ObservableList<Student> results = FXCollections.observableArrayList();

        if (searchAlgo.equals("Hash Table (O(1))") || searchAlgo.equals("Binary Search Tree (O(log N))") || searchAlgo.equals("Binary Search")) {
            if (!mode.equals("NIM")) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Algoritma ini hanya untuk NIM. Dialihkan ke Linear Search.");
                alert.showAndWait();
                executeLinearSearch(keyword, mode, results);
            } else {
                long start = System.nanoTime();
                Student s = null;

                if(searchAlgo.equals("Hash Table (O(1))")) s = studentService.searchByCache(keyword);
                else if(searchAlgo.equals("Binary Search Tree (O(log N))")) s = studentService.searchByBST(keyword);
                else s = studentService.binarySearch(keyword);

                long end = System.nanoTime();
                if (s != null) results.add(s);
                statusLabel.setText("Status: " + searchAlgo + " dieksekusi dalam " + (end - start) + " ns. Ditemukan " + results.size() + " mahasiswa.");
            }
        } else {
            executeLinearSearch(keyword, mode, results);
        }

        if (!results.isEmpty()) studentTable.setItems(results);
        else statusLabel.setText(statusLabel.getText() + " (Namun mahasiswa tidak ditemukan)");
    }

    private void executeLinearSearch(String keyword, String mode, ObservableList<Student> results) {
        long start = System.nanoTime();
        for (Student s : studentService.searchByKeyword(keyword, mode)) results.add(s);
        long end = System.nanoTime();
        statusLabel.setText("Status: Linear Search dieksekusi dalam " + (end - start) + " ns.");
    }

    private void refreshTable() {
        if(studentService == null) return;
        ObservableList<Student> fxList = FXCollections.observableArrayList();
        for (Student s : studentService.getAllStudents()) fxList.add(s);
        studentTable.setItems(fxList);
        updateFacultyChart(fxList); updateAvgIpkChart(fxList); updateSemesterChart(fxList);
    }

    // =====================================================================
    // FITUR: QUEUE (ANTREAN KRS) & GRAPH (PRASYARAT MK)
    // =====================================================================

    private static final int MAX_SKS_PER_SEMESTER = 24;

    @FXML
    protected void onEnrollCourseButtonClick() {
        String nim = nimField.getText();
        Course selectedCourse = courseComboBox.getValue();
        if (nim.isEmpty() || selectedCourse == null) return;

        String courseId = selectedCourse.getCourseId();

        // VALIDASI GRAPH: Cek Prasyarat Mata Kuliah (Prerequisites)
        List<String> prereqs = prereqGraph.getPrerequisites(courseId);
        if (!prereqs.isEmpty()) {
            boolean passedAll = true;
            for (String req : prereqs) {
                boolean passedThis = false;
                for (EnrollmentData ed : gradeTable.getItems()) {
                    if (ed.getCourseId().equals(req) && ed.getFinalScore() != null && ed.getFinalScore() >= 60) {
                        passedThis = true; break;
                    }
                }
                if (!passedThis) { passedAll = false; break; }
            }
            if (!passedAll) {
                statusLabel.setText("Gagal: Kamu belum lulus mata kuliah prasyarat untuk MK ini!");
                return;
            }
        }

        int sksBerjalan = enrollmentRepo.getTotalOngoingSks(nim);
        int sksSetelahAmbil = sksBerjalan + selectedCourse.getCredits();
        if (sksSetelahAmbil > MAX_SKS_PER_SEMESTER) {
            statusLabel.setText("Status: Gagal - SKS melebihi " + MAX_SKS_PER_SEMESTER);
            return;
        }

        // VALIDASI QUEUE: Jika Mahasiswa, masukkan ke Antrean. Jika Admin, langsung eksekusi.
        if (currentUser != null && currentUser.getRole().equals("MAHASISWA")) {
            krsQueue.enqueue(new String[]{nim, courseId, selectedCourse.getCourseName()});
            statusLabel.setText("Status: Pengajuan KRS masuk ke dalam Antrean Persetujuan Admin.");
        } else {
            enrollmentRepo.enrollCourse(nim, courseId);
            loadStudentGrades(nim);
            statusLabel.setText("Status: Berhasil KRS Mata Kuliah " + selectedCourse.getCourseName());
        }

        // Update label tombol Admin
        if (btnApproveKrs != null) {
            btnApproveKrs.setText("Setujui Antrean KRS (" + krsQueue.getSize() + ")");
        }
        courseComboBox.setValue(null);
    }

    // Fungsi klik tombol "Setujui Antrean KRS" oleh Admin
    @FXML
    protected void onApproveKrsButtonClick() {
        if (krsQueue.isEmpty()) {
            statusLabel.setText("Status: Antrean KRS saat ini kosong.");
            return;
        }
        int count = 0;
        while (!krsQueue.isEmpty()) {
            String[] req = krsQueue.dequeue();
            enrollmentRepo.enrollCourse(req[0], req[1]);
            count++;
        }

        btnApproveKrs.setText("Setujui Antrean KRS (0)");
        statusLabel.setText("Status: Berhasil menyetujui " + count + " pengajuan KRS dari antrean!");

        // Refresh tabel nilai jika sedang buka NIM seseorang
        if (!nimField.getText().isEmpty()) {
            loadStudentGrades(nimField.getText());
        }
    }

    @FXML
    protected void onInputNilaiButtonClick() {
        String nim = nimField.getText();
        Course selectedCourse = courseComboBox.getValue();
        if (nim.isEmpty() || selectedCourse == null) return;
        double[] komponen = parseKomponenNilai();
        if (komponen == null) return;
        enrollmentRepo.updateGrade(nim, selectedCourse.getCourseId(), komponen[0], komponen[1], komponen[2], komponen[3]);
        loadStudentGrades(nim);
        courseComboBox.setValue(null);
        clearKomponenNilaiFields();
        statusLabel.setText("Status: Nilai berhasil disimpan!");
    }

    private double[] parseKomponenNilai() {
        try {
            double tugas = Double.parseDouble(tugasField.getText());
            double quiz = Double.parseDouble(quizField.getText());
            double uts = Double.parseDouble(utsField.getText());
            double uas = Double.parseDouble(uasField.getText());
            for (double n : new double[]{tugas, quiz, uts, uas}) {
                if (n < 0 || n > 100) return null;
            }
            return new double[]{tugas, quiz, uts, uas};
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void clearKomponenNilaiFields() {
        tugasField.clear(); quizField.clear(); utsField.clear(); uasField.clear();
    }

    @FXML
    protected void onDeleteGradeButtonClick() {
        String nim = nimField.getText();
        EnrollmentData selectedRow = gradeTable.getSelectionModel().getSelectedItem();
        if (nim.isEmpty() || selectedRow == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Batalkan KRS ini?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            enrollmentRepo.deleteGrade(nim, selectedRow.getCourseId());
            loadStudentGrades(nim);
            courseComboBox.setValue(null);
            clearKomponenNilaiFields();
        }
    }

    @FXML
    protected void onPrintTranscriptButtonClick() {
        if (nimField.getText().isEmpty()) return;
        VBox printLayout = buildTranscriptPrintLayout();
        new Scene(printLayout);
        printLayout.applyCss();
        printLayout.layout();
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(nimField.getScene().getWindow())) {
            if (job.printPage(printLayout)) job.endJob();
        }
    }

    private VBox buildTranscriptPrintLayout() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(30));
        Label judul = new Label("TRANSKRIP NILAI");
        judul.setFont(Font.font("System", FontWeight.BOLD, 20));
        Label nama = new Label("Nama   : " + nameField.getText());
        Label nim = new Label("NIM    : " + nimField.getText());
        Label ipk = new Label(ipkLabel.getText());
        GridPane grid = new GridPane();
        grid.setHgap(20.0); grid.setVgap(6.0);
        String[] header = {"Kode MK", "Mata Kuliah", "SKS", "Nilai", "Grade"};
        for (int i = 0; i < header.length; i++) {
            Label h = new Label(header[i]);
            h.setFont(Font.font("System", FontWeight.BOLD, 12));
            grid.add(h, i, 0);
        }
        int baris = 1;
        for (EnrollmentData data : gradeTable.getItems()) {
            grid.add(new Label(data.getCourseId()), 0, baris);
            grid.add(new Label(data.getCourseName()), 1, baris);
            grid.add(new Label(String.valueOf(data.getCredits())), 2, baris);
            grid.add(new Label(data.getFinalScore() == null ? "-" : String.format("%.1f", data.getFinalScore())), 3, baris);
            grid.add(new Label(data.getGrade()), 4, baris);
            baris++;
        }
        box.getChildren().addAll(judul, nama, nim, ipk, grid);
        return box;
    }

    private void logActivity(String pesan) {
        String waktu = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        String entry = "[" + waktu + "] " + pesan;
        historyLog.add(0, entry);
        writeLogToFile(entry);
    }

    private void writeLogToFile(String entry) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(entry + System.lineSeparator());
        } catch (IOException e) {}
    }

    @FXML
    protected void onRefreshReportButtonClick() {
        String type = reportTypeComboBox.getValue();
        if (type.equals("Rekap per Mata Kuliah")) {
            courseReportTable.setVisible(true);
            facultyReportTable.setVisible(false);
            ObservableList<CourseReport> list = FXCollections.observableArrayList();
            for (CourseReport r : enrollmentRepo.getCourseAggregateReport()) list.add(r);
            courseReportTable.setItems(list);
        } else if (type.equals("Rekap per Fakultas")) {
            courseReportTable.setVisible(false);
            facultyReportTable.setVisible(true);
            ObservableList<FacultyReport> list = FXCollections.observableArrayList();
            for (FacultyReport r : enrollmentRepo.getFacultyAggregateReport()) list.add(r);
            facultyReportTable.setItems(list);
        }
    }

    // ==========================================================
    // MODULE: PRESENSI (METHOD BARU UNTUK TAB PRESENSI)
    // ==========================================================

    private void loadAttendanceData(String nim, String courseId) {
        ObservableList<Attendance> list = FXCollections.observableArrayList();
        for(Attendance a : attendanceRepo.getAttendance(nim, courseId)) {
            list.add(a);
        }
        attendanceTable.setItems(list);
    }

    @FXML
    protected void onSaveAttendanceButtonClick() {
        String nim = presensiNimField.getText();
        Course course = presensiCourseComboBox.getValue();
        Integer meeting = presensiMeetingComboBox.getValue();
        String statusAtt = presensiStatusComboBox.getValue();

        if(nim.isEmpty() || course == null || meeting == null || statusAtt == null) {
            statusLabel.setText("Status: Lengkapi form presensi terlebih dahulu!"); return;
        }

        attendanceRepo.saveAttendance(nim, course.getCourseId(), meeting, statusAtt);
        loadAttendanceData(nim, course.getCourseId());
        logActivity("Input Presensi NIM " + nim + " MK " + course.getCourseId() + " Pertemuan " + meeting + " : " + statusAtt);
        statusLabel.setText("Status: Presensi berhasil disimpan!");
    }

    // =====================================================================
    // SISA METHOD-METHOD DOSEN, MATKUL, DLL
    // =====================================================================

    private void refreshLecturerTable() { ObservableList<Lecturer> fxList = FXCollections.observableArrayList(); for (Lecturer l : lecturerRepo.findAll()) { fxList.add(l); } lecturerTable.setItems(fxList); }
    @FXML protected void onAddLecturerButtonClick() { String nip = lecturerIdField.getText(); String name = lecturerNameField.getText(); String faculty = lecturerFacultyField.getText(); String dept = lecturerDeptField.getText(); String email = lecturerEmailField.getText(); if (nip.isEmpty() || name.isEmpty()) return; Lecturer newLecturer = new Lecturer(nip, name, faculty, dept, email); lecturerRepo.save(newLecturer); onClearLecturerButtonClick(); refreshLecturerTable(); }
    @FXML protected void onEditLecturerButtonClick() { if (lecturerIdField.getText().isEmpty()) return; String nip = lecturerIdField.getText(); Lecturer updatedLecturer = new Lecturer(nip, lecturerNameField.getText(), lecturerFacultyField.getText(), lecturerDeptField.getText(), lecturerEmailField.getText()); lecturerRepo.update(updatedLecturer); refreshLecturerTable(); onClearLecturerButtonClick(); }
    @FXML protected void onDeleteLecturerButtonClick() { String nip = lecturerIdField.getText(); if (nip.isEmpty()) return; lecturerRepo.delete(nip); refreshLecturerTable(); onClearLecturerButtonClick(); }
    @FXML protected void onClearLecturerButtonClick() { lecturerIdField.clear(); lecturerNameField.clear(); lecturerFacultyField.clear(); lecturerDeptField.clear(); lecturerEmailField.clear(); lecturerIdField.setDisable(false); lecturerTable.getSelectionModel().clearSelection(); }

    private void refreshCourseManagementTable() { ObservableList<Course> fxList = FXCollections.observableArrayList(); for (Course c : courseRepo.findAll()) { fxList.add(c); } courseManagementTable.setItems(fxList); }
    @FXML protected void onAddCourseButtonClick() { try { String courseId = courseIdMgmtField.getText(); String courseName = courseNameMgmtField.getText(); String faculty = courseFacultyMgmtField.getText(); String dept = courseDeptMgmtField.getText(); int credits = Integer.parseInt(courseCreditsMgmtField.getText()); if (courseId.isEmpty() || courseName.isEmpty()) return; Course newCourse = new Course(courseId, courseName, faculty, dept, credits); courseRepo.save(newCourse); onClearCourseButtonClick(); refreshCourseManagementTable(); loadCourseComboBox(); } catch (NumberFormatException e) {} }
    @FXML protected void onEditCourseButtonClick() { if (courseIdMgmtField.getText().isEmpty()) return; try { int credits = Integer.parseInt(courseCreditsMgmtField.getText()); String courseId = courseIdMgmtField.getText(); Course updatedCourse = new Course(courseId, courseNameMgmtField.getText(), courseFacultyMgmtField.getText(), courseDeptMgmtField.getText(), credits); courseRepo.update(updatedCourse); refreshCourseManagementTable(); loadCourseComboBox(); onClearCourseButtonClick(); } catch (NumberFormatException e) {} }
    @FXML protected void onDeleteCourseButtonClick() { String courseId = courseIdMgmtField.getText(); if (courseId.isEmpty()) return; courseRepo.delete(courseId); refreshCourseManagementTable(); loadCourseComboBox(); onClearCourseButtonClick(); }
    @FXML protected void onClearCourseButtonClick() { courseIdMgmtField.clear(); courseNameMgmtField.clear(); courseFacultyMgmtField.clear(); courseDeptMgmtField.clear(); courseCreditsMgmtField.clear(); courseIdMgmtField.setDisable(false); courseManagementTable.getSelectionModel().clearSelection(); }

    private void refreshClassroomTable() { ObservableList<Classroom> fxList = FXCollections.observableArrayList(); for (Classroom c : classroomRepo.findAll()) { fxList.add(c); } classroomTable.setItems(fxList); }
    @FXML protected void onAddClassroomButtonClick() { try { String building = buildingField.getText(); String roomName = roomNameField.getText(); int capacity = Integer.parseInt(capacityField.getText()); classroomRepo.save(building, roomName, capacity); onClearClassroomButtonClick(); refreshClassroomTable(); loadScheduleDropdowns(); } catch (Exception e) {} }
    @FXML protected void onEditClassroomButtonClick() { if (selectedClassroomId == null) return; try { int capacity = Integer.parseInt(capacityField.getText()); classroomRepo.update(selectedClassroomId, buildingField.getText(), roomNameField.getText(), capacity); refreshClassroomTable(); loadScheduleDropdowns(); onClearClassroomButtonClick(); } catch (Exception e) {} }
    @FXML protected void onDeleteClassroomButtonClick() { if (selectedClassroomId == null) return; classroomRepo.delete(selectedClassroomId); refreshClassroomTable(); loadScheduleDropdowns(); onClearClassroomButtonClick(); }
    @FXML protected void onClearClassroomButtonClick() { selectedClassroomId = null; buildingField.clear(); roomNameField.clear(); capacityField.clear(); classroomTable.getSelectionModel().clearSelection(); }

    private void loadScheduleDropdowns() { ObservableList<Course> courseList = FXCollections.observableArrayList(); for (Course c : courseRepo.findAll()) courseList.add(c); scheduleCourseComboBox.setItems(courseList); ObservableList<Lecturer> lecturerList = FXCollections.observableArrayList(); for (Lecturer l : lecturerRepo.findAll()) lecturerList.add(l); scheduleLecturerComboBox.setItems(lecturerList); ObservableList<Classroom> roomList = FXCollections.observableArrayList(); for (Classroom c : classroomRepo.findAll()) roomList.add(c); scheduleRoomComboBox.setItems(roomList); scheduleDayComboBox.setItems(FXCollections.observableArrayList("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")); }
    private void refreshScheduleTable() { ObservableList<Schedule> fxList = FXCollections.observableArrayList(); for (Schedule s : scheduleRepo.findAll()) fxList.add(s); scheduleTable.setItems(fxList); }
    private boolean isValidTimeFormat(String time) { return time != null && time.matches("([01]\\d|2[0-3]):[0-5]\\d"); }
    @FXML protected void onAddScheduleButtonClick() { Course course = scheduleCourseComboBox.getValue(); Lecturer lecturer = scheduleLecturerComboBox.getValue(); Classroom room = scheduleRoomComboBox.getValue(); String day = scheduleDayComboBox.getValue(); String start = scheduleStartField.getText(); String end = scheduleEndField.getText(); if (course == null || day == null) return; if (!isValidTimeFormat(start) || !isValidTimeFormat(end)) return; String lecturerId = (lecturer == null) ? null : lecturer.getId(); Integer classroomId = (room == null) ? null : room.getClassroomId(); scheduleRepo.save(course.getCourseId(), lecturerId, classroomId, day, start, end); refreshScheduleTable(); onClearScheduleButtonClick(); }
    @FXML protected void onEditScheduleButtonClick() { if (selectedScheduleId == null) return; Course course = scheduleCourseComboBox.getValue(); Lecturer lecturer = scheduleLecturerComboBox.getValue(); Classroom room = scheduleRoomComboBox.getValue(); String day = scheduleDayComboBox.getValue(); String start = scheduleStartField.getText(); String end = scheduleEndField.getText(); if (course == null || day == null) return; String lecturerId = (lecturer == null) ? null : lecturer.getId(); Integer classroomId = (room == null) ? null : room.getClassroomId(); scheduleRepo.update(selectedScheduleId, course.getCourseId(), lecturerId, classroomId, day, start, end); refreshScheduleTable(); onClearScheduleButtonClick(); }
    @FXML protected void onDeleteScheduleButtonClick() { if (selectedScheduleId == null) return; scheduleRepo.delete(selectedScheduleId); refreshScheduleTable(); onClearScheduleButtonClick(); }
    @FXML protected void onClearScheduleButtonClick() { selectedScheduleId = null; scheduleCourseComboBox.setValue(null); scheduleLecturerComboBox.setValue(null); scheduleRoomComboBox.setValue(null); scheduleDayComboBox.setValue(null); scheduleStartField.clear(); scheduleEndField.clear(); scheduleTable.getSelectionModel().clearSelection(); }

    // =====================================================================
    // FITUR STATISTIK & GRAFIK
    // =====================================================================
    private void updateFacultyChart(ObservableList<Student> students) {
        Map<String, Integer> countByFaculty = new HashMap<>();
        for (Student s : students) countByFaculty.merge(s.getFaculty(), 1, Integer::sum);
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : countByFaculty.entrySet()) {
            chartData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
        }
        facultyChart.setData(chartData);
    }

    private void updateAvgIpkChart(ObservableList<Student> students) {
        Map<String, Double> totalIpk = new HashMap<>();
        Map<String, Integer> jumlahMahasiswa = new HashMap<>();
        for (Student s : students) {
            double ipk = calculateIPKForStudent(s.getId());
            totalIpk.merge(s.getFaculty(), ipk, Double::sum);
            jumlahMahasiswa.merge(s.getFaculty(), 1, Integer::sum);
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Rata-rata IPK");
        for (String fakultas : totalIpk.keySet()) {
            double rataRata = totalIpk.get(fakultas) / jumlahMahasiswa.get(fakultas);
            series.getData().add(new XYChart.Data<>(fakultas, rataRata));
        }
        avgIpkChart.getData().clear();
        avgIpkChart.getData().add(series);
    }

    private void updateSemesterChart(ObservableList<Student> students) {
        Map<Integer, Integer> countBySemester = new TreeMap<>();
        for (Student s : students) countBySemester.merge(s.getSemester(), 1, Integer::sum);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Jumlah Mahasiswa");
        for (Map.Entry<Integer, Integer> entry : countBySemester.entrySet()) {
            series.getData().add(new XYChart.Data<>("Semester " + entry.getKey(), entry.getValue()));
        }
        semesterChart.getData().clear();
        semesterChart.getData().add(series);
    }
}