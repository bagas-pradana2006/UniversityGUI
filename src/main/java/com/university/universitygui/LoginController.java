package com.university.universitygui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import repository.UserRepository;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserRepository userRepository = new UserRepository();

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username dan Password tidak boleh kosong!");
            return;
        }

        // Cek ke Database SQL Server
        User loggedInUser = userRepository.login(username, password);

        if (loggedInUser != null) {
            errorLabel.setText("Login Berhasil! Memuat Dashboard...");
            errorLabel.setTextFill(javafx.scene.paint.Color.GREEN);

            // JIKA SUKSES, PINDAH KE LAYAR DASHBOARD (bawa data user & role-nya)
            pindahKeDashboard(loggedInUser);
        } else {
            errorLabel.setText("Username atau Password SALAH!");
        }
    }

    private void pindahKeDashboard(User loggedInUser) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Scene dashboardScene = new Scene(fxmlLoader.load(), 920, 780);

            // Kirim data user yang login ke HelloController, supaya tampilan
            // bisa disesuaikan berdasarkan role (Admin/Dosen/Mahasiswa)
            HelloController controller = fxmlLoader.getController();
            controller.setCurrentUser(loggedInUser);

            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.setResizable(true);
            currentStage.setScene(dashboardScene);
            currentStage.setTitle("University Management System - Dashboard (" + loggedInUser.getRole() + ")");
            currentStage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Gagal memuat Dashboard: " + e.getMessage());
        }
    }
}