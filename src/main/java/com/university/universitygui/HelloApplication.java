package com.university.universitygui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // 1. Kita panggil desain Layar Login
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));

        // 2. Ubah ukuran jendela menjadi kotak kecil (400 x 400)
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);

        // 3. Ubah judul aplikasinya
        stage.setTitle("Security Login");
        stage.setScene(scene);
        stage.setResizable(false); // Biar ukuran layar login tidak bisa ditarik-tarik
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}