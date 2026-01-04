package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AdminDashboardView {
	private MainApp mainApp;
    private String adminUsername;

    public AdminDashboardView(MainApp mainApp, String adminUsername) {
        this.mainApp = mainApp;
        this.adminUsername = adminUsername;
    }

    public Parent getView() {
        VBox layout = new VBox(20); // Elemanlar arası 20px boşluk
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.CENTER);
        
        // Başlık
        Label lblWelcome = new Label("Hoşgeldiniz, " + adminUsername.toUpperCase());
        lblWelcome.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        Label lblInstruction = new Label("Lütfen yapmak istediğiniz işlemi seçiniz:");
        
        // Butonlar
        Button btnFlights = new Button("Uçuş İşlemleri");
        btnFlights.setPrefWidth(200);
        btnFlights.setPrefHeight(50);
        btnFlights.setStyle("-fx-font-size: 16px; -fx-base: #4a90e2;");
        
        Button btnStaff = new Button("Personel İşlemleri");
        btnStaff.setPrefWidth(200);
        btnStaff.setPrefHeight(50);
        btnStaff.setStyle("-fx-font-size: 16px; -fx-base: #50c878;");
        
        Button btnLogout = new Button("Çıkış Yap (Logout)");
        btnLogout.setPrefWidth(200);
        btnLogout.setStyle("-fx-base: #e74c3c;");
        
        // Tıklama Olayları (Navigasyon)
        btnFlights.setOnAction(e -> mainApp.showFlightScreen(adminUsername));
        btnStaff.setOnAction(e -> mainApp.showStaffScreen(adminUsername));
        btnLogout.setOnAction(e -> mainApp.showLoginScreen());
        
        layout.getChildren().addAll(lblWelcome, lblInstruction, btnFlights, btnStaff, btnLogout);
        
        return layout;
    }

}
