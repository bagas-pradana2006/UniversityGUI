package util;

public class GradeCalculator {

    // Bobot komponen nilai: Tugas 20%, Quiz 10%, UTS 30%, UAS 40%
    public static double calculateFinalScore(double tugas, double quiz, double uts, double uas) {
        return (tugas * 0.20) + (quiz * 0.10) + (uts * 0.30) + (uas * 0.40);
    }

    // Konversi nilai akhir (skala 0-100) ke huruf mutu (skala A/B/C/D/E,
    // sengaja dibuat 5 tingkat biar tetap cocok sama perhitungan IPK yang sudah ada)
    public static String calculateGrade(double finalScore) {
        if (finalScore >= 80) return "A";
        if (finalScore >= 70) return "B";
        if (finalScore >= 60) return "C";
        if (finalScore >= 50) return "D";
        return "E";
    }
}