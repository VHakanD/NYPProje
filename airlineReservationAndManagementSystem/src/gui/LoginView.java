package gui;

import java.util.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView {
	private MainApp mainApp;
	
	private Map<String, String> adminCredentials;

    // Constructor: MainApp referansını alıyoruz ki ekran değiştirebilelim
    public LoginView(MainApp mainApp) {
        this.mainApp = mainApp;
        
        adminCredentials = new HashMap<>();
        adminCredentials.put("VHakanD", "1234");
        adminCredentials.put("zeyneppkts", "5678"); 
        adminCredentials.put("root", "0000");
    }

    public Parent getView() {
        // 1. DÜZEN (LAYOUT) OLUŞTURMA
        VBox layout = new VBox(15); // Elemanlar arası 15px boşluk
        layout.setPadding(new Insets(40)); // Kenarlardan 40px boşluk
        layout.setAlignment(Pos.CENTER); // Her şeyi ortala
        layout.setStyle("-fx-background-color: #f0f2f5;"); // Hafif gri arka plan

        // 2. GÖRSEL BİLEŞENLERİ TANIMLAMA
        
        // Başlık
        Label lblTitle = new Label("Havayolu Rezervasyon Sistemi");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblTitle.setStyle("-fx-text-fill: #2c3e50;");

        // Rol Seçimi (Admin mi Yolcu mu?)
        Label lblRole = new Label("Giriş Türü:");
        ComboBox<String> cmbRole = new ComboBox<>();
        cmbRole.getItems().addAll("Yönetici (Admin)", "Yolcu (Passenger)");
        cmbRole.setValue("Yönetici (Admin)"); // Varsayılan seçili gelsin
        cmbRole.setPrefWidth(250);

        // Kullanıcı Adı / ID
        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Kullanıcı Adı veya ID");
        txtUsername.setPrefWidth(250);

        // Şifre
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Şifre");
        txtPassword.setPrefWidth(250);

        // Giriş Butonu
        Button btnLogin = new Button("Giriş Yap");
        btnLogin.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px;");
        btnLogin.setPrefWidth(250);
        btnLogin.setPrefHeight(40);

        // 3. BUTON TIKLAMA OLAYI (ACTION)
        btnLogin.setOnAction(e -> {
            String role = cmbRole.getValue();
            String username = txtUsername.getText();
            String password = txtPassword.getText();

            handleLogin(role, username, password);
        });

        // 4. BİLEŞENLERİ DÜZENE EKLEME
        layout.getChildren().addAll(lblTitle, new Separator(), lblRole, cmbRole, txtUsername, txtPassword, btnLogin);

        return layout;
    }

    // Giriş kontrolü yapan yardımcı metot
    private void handleLogin(String role, String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Hata", "Lütfen tüm alanları doldurunuz!");
            return;
        }

        if (role.contains("Admin")) {
        	if (adminCredentials.containsKey(username)) {
                String correctPass = adminCredentials.get(username);
                
                if (correctPass.equals(password)) {
                    System.out.println("Giriş Başarılı: " + username);
                    
                    // KRİTİK NOKTA: Giriş yapan admin ismini MainApp'e gönderiyoruz!
                    mainApp.showAdminDashboard(username); 
                    
                } else {
                    showAlert("Giriş Başarısız", "Şifre hatalı!");
                }
            } else {
                showAlert("Giriş Başarısız", "Böyle bir admin kullanıcısı bulunamadı!");
            }
        } 
        else {
            // Yolcu Girişi
            // Gerçek senaryoda passengerList içinde ID kontrolü yapılabilir.
            // Şimdilik herkesi kabul ediyoruz.
            System.out.println("Yolcu girişi: " + username);
            mainApp.showUserSearchScreen(); // Yolcu ekranına geç
        }
    }

    // Uyarı mesajı göstermek için yardımcı metot
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
