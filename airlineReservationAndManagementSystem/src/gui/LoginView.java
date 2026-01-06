package gui;

import java.util.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import reservationAndTicketing.Passenger;

public class LoginView {
	private MainApp mainApp;
	
	private Map<String, String> adminCredentials;
	
	private VBox mainLayout;
    private VBox loginBox;
    private VBox registerBox;

    // Constructor: MainApp referansını alıyoruz ki ekran değiştirebilelim
    public LoginView(MainApp mainApp) {
        this.mainApp = mainApp;
        
        adminCredentials = new HashMap<>();
        adminCredentials.put("VHakanD", "1234");
        adminCredentials.put("zeyneppkts", "5678"); 
        adminCredentials.put("root", "0000");
        adminCredentials.put("a", "1");
    }

    /*public Parent getView() {
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
    }*/
    
    public Parent getView() {
        mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: #f0f2f5;");

        Label lblTitle = new Label("Havayolu Rezervasyon Sistemi");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        // --- GİRİŞ KUTUSU ---
        createLoginBox();
        
        // --- KAYIT KUTUSU ---
        createRegisterBox();

        // Varsayılan olarak Giriş ekranı görünür
        mainLayout.getChildren().addAll(lblTitle, new Separator(), loginBox);
        
        return mainLayout;
    }

    // Giriş kontrolü yapan yardımcı metot
    /*private void handleLogin(String role, String username, String password) {
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
            mainApp.showUserSearchScreen(username); // Yolcu ekranına geç
        }
    }*/
    
    private void createLoginBox() {
        loginBox = new VBox(10);
        loginBox.setAlignment(Pos.CENTER);

        Label lblHeader = new Label("Giriş Yap");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        ComboBox<String> cmbRole = new ComboBox<>();
        cmbRole.getItems().addAll("Yönetici (Admin)", "Yolcu (Passenger)");
        cmbRole.setValue("Yönetici (Admin)");
        cmbRole.setPrefWidth(250);

        TextField txtUser = new TextField(); 
        txtUser.setPromptText("Kullanıcı Adı"); 
        txtUser.setPrefWidth(250);
        
        PasswordField txtPass = new PasswordField(); 
        txtPass.setPromptText("Şifre"); 
        txtPass.setPrefWidth(250);

        Button btnLogin = new Button("Giriş Yap");
        btnLogin.setStyle("-fx-base: #3498db; -fx-text-fill: white;");
        btnLogin.setPrefWidth(250);

        // Kayıt Ol Linki
        Hyperlink linkRegister = new Hyperlink("Hesabınız yok mu? Kayıt Olun");
        linkRegister.setOnAction(e -> showRegisterScreen());

        btnLogin.setOnAction(e -> {
            String role = cmbRole.getValue();
            if (role.contains("Admin")) {
                handleAdminLogin(txtUser.getText(), txtPass.getText());
            } else {
                handlePassengerLogin(txtUser.getText(), txtPass.getText());
            }
        });

        loginBox.getChildren().addAll(lblHeader, cmbRole, txtUser, txtPass, btnLogin, linkRegister);
    }
    
    private void createRegisterBox() {
        registerBox = new VBox(10);
        registerBox.setAlignment(Pos.CENTER);

        Label lblHeader = new Label("Yeni Yolcu Kaydı");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextField regName = new TextField(); regName.setPromptText("Ad");
        TextField regSurname = new TextField(); regSurname.setPromptText("Soyad");
        TextField regID = new TextField(); regID.setPromptText("TC / Pasaport No");
        TextField regPhone = new TextField(); regPhone.setPromptText("Telefon (05XX...)");
        TextField regUser = new TextField(); regUser.setPromptText("Kullanıcı Adı Belirle");
        PasswordField regPass = new PasswordField(); regPass.setPromptText("Şifre Belirle");
        
        // Genişlik ayarı
        regName.setPrefWidth(250); regSurname.setPrefWidth(250); regID.setPrefWidth(250);
        regPhone.setPrefWidth(250); regUser.setPrefWidth(250); regPass.setPrefWidth(250);

        Button btnRegister = new Button("Kayıt Ol");
        btnRegister.setStyle("-fx-base: #2ecc71; -fx-text-fill: white;");
        btnRegister.setPrefWidth(250);

        Hyperlink linkBack = new Hyperlink("Giriş Ekranına Dön");
        linkBack.setOnAction(e -> showLoginScreen());

        btnRegister.setOnAction(e -> handleRegistration(regName, regSurname, regID, regPhone, regUser, regPass));

        registerBox.getChildren().addAll(lblHeader, regName, regSurname, regID, regPhone, new Separator(), regUser, regPass, btnRegister, linkBack);
    }
    
    private void showRegisterScreen() {
        mainLayout.getChildren().remove(loginBox);
        if (!mainLayout.getChildren().contains(registerBox)) {
            mainLayout.getChildren().add(registerBox);
        }
    }

    private void showLoginScreen() {
        mainLayout.getChildren().remove(registerBox);
        if (!mainLayout.getChildren().contains(loginBox)) {
            mainLayout.getChildren().add(loginBox);
        }
    }
    
    private void handleRegistration(TextField name, TextField sur, TextField id, TextField phone, TextField user, PasswordField pass) {
        if(name.getText().isEmpty() || sur.getText().isEmpty() || id.getText().isEmpty() ||
           user.getText().isEmpty() || pass.getText().isEmpty()) {
            showAlert("Hata", "Lütfen tüm alanları doldurunuz.");
            return;
        }
        
        // Yeni Yolcu Oluştur
        Passenger newP = new Passenger(
            id.getText(), name.getText(), sur.getText(), 
            phone.getText(), user.getText(), pass.getText()
        );
        
        // MainApp üzerinden kaydet
        mainApp.savePassengerToFile(newP);
        
        showAlert("Başarılı", "Kayıt tamamlandı! Şimdi giriş yapabilirsiniz.");
        showLoginScreen();
    }

    private void handlePassengerLogin(String username, String password) {
        List<Passenger> list = mainApp.getPassengerList();
        Passenger foundPassenger = null;
        
        // Listede kullanıcı adı ve şifre ara
        for(Passenger p : list) {
            if (p.getUsername() != null && p.getUsername().equals(username) && 
                p.getPassword() != null && p.getPassword().equals(password)) {
                foundPassenger = p;
                break;
            }
        }

        if (foundPassenger != null) {
            System.out.println("Yolcu Girişi Başarılı: " + foundPassenger.getName());
            mainApp.showUserSearchScreen(foundPassenger); 
        } else {
            showAlert("Hata", "Kullanıcı adı veya şifre hatalı. Kayıtlı değilseniz lütfen kayıt olun.");
        }
    }

    private void handleAdminLogin(String user, String pass) {
        if (adminCredentials.containsKey(user) && adminCredentials.get(user).equals(pass)) {
             mainApp.showAdminDashboard(user);
        } else {
             showAlert("Hata", "Yönetici bilgileri hatalı.");
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
