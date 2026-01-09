package gui;

import java.util.*;

import flightManagement.Staff;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import reservationAndTicketing.Passenger;
import servicesAndManagers.StaffManager;

public class LoginView {
	private MainApp mainApp;
	
	private StaffManager staffManager;
	
	private VBox mainLayout;
    private VBox loginBox;
    private VBox registerBox;

    public LoginView(MainApp mainApp, StaffManager staffManager) {
        this.mainApp = mainApp;
        this.staffManager = staffManager;
    }
    
    public Parent getView() {
        mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: #f0f2f5;");

        Label lblTitle = new Label("Hazerfen Airlines Rezervasyon Sistemi");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        createLoginBox();
        
        createRegisterBox();

        mainLayout.getChildren().addAll(lblTitle, new Separator(), loginBox);
        
        return mainLayout;
    }

    
    private void createLoginBox() {
        loginBox = new VBox(10);
        loginBox.setAlignment(Pos.CENTER);

        Label lblHeader = new Label("Giriş Yap");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        ComboBox<String> cmbRole = new ComboBox<>();
        cmbRole.getItems().addAll("Yönetici", "Yolcu");
        cmbRole.setValue("Yönetici");
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

        Hyperlink linkRegister = new Hyperlink("Hesabınız yok mu? Kayıt Olun");
        linkRegister.setOnAction(e -> showRegisterScreen());
        
        linkRegister.setVisible(false);

        
        cmbRole.setOnAction(e -> {
            String selected = cmbRole.getValue();
            if (selected.contains("Admin")) {
                linkRegister.setVisible(false);
            } else {
                linkRegister.setVisible(true);
            }
        });
        
        TextField txtPassShown = new TextField();
        txtPassShown.setPromptText("Şifre");
        txtPassShown.setPrefWidth(250);
        txtPassShown.setManaged(false);
        txtPassShown.setVisible(false);

        txtPass.textProperty().bindBidirectional(txtPassShown.textProperty());

        StackPane passStack = new StackPane(txtPass, txtPassShown);
        passStack.setAlignment(Pos.CENTER);
        passStack.setMaxWidth(250);

        CheckBox chkShowPass = new CheckBox("Şifreyi Göster");
        chkShowPass.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");
        
        chkShowPass.setOnAction(e -> {
            if (chkShowPass.isSelected()) {
                txtPassShown.setManaged(true);
                txtPassShown.setVisible(true);
                txtPass.setManaged(false);
                txtPass.setVisible(false);
            } else {
                txtPass.setManaged(true);
                txtPass.setVisible(true);
                txtPassShown.setManaged(false);
                txtPassShown.setVisible(false);
            }
        });
        
        btnLogin.setOnAction(e -> {
            String role = cmbRole.getValue();
            if (role.equals("Yönetici") || role.contains("Admin")) {
                handleAdminLogin(txtUser.getText(), txtPass.getText());
            } else {
                handlePassengerLogin(txtUser.getText(), txtPass.getText());
            }
        });

        loginBox.getChildren().addAll(lblHeader, cmbRole, txtUser, passStack, chkShowPass, btnLogin, linkRegister);
    }
    
    private void createRegisterBox() {
        registerBox = new VBox(10);
        registerBox.setAlignment(Pos.CENTER);

        Label lblHeader = new Label("Yeni Yolcu Kaydı");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextField regName = new TextField();
        regName.setPromptText("Ad");
        TextField regSurname = new TextField();
        regSurname.setPromptText("Soyad");
        TextField regID = new TextField();
        regID.setPromptText("TC / Pasaport No");
        TextField regPhone = new TextField();
        regPhone.setPromptText("Telefon (05XX...)");
        TextField regUser = new TextField();
        regUser.setPromptText("Kullanıcı Adı Belirle");
        
        PasswordField regPass = new PasswordField(); 
        regPass.setPromptText("Şifre Belirle");
        
        TextField regPassShown = new TextField();
        regPassShown.setPromptText("Şifre Belirle");
        regPassShown.setManaged(false);
        regPassShown.setVisible(false);
        
        regPass.textProperty().bindBidirectional(regPassShown.textProperty());
        
        StackPane regPassStack = new StackPane(regPass, regPassShown);
        regPassStack.setAlignment(Pos.CENTER);
        
        CheckBox chkShowRegPass = new CheckBox("Şifreyi Göster");
        chkShowRegPass.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");
        
        chkShowRegPass.setOnAction(e -> {
            if (chkShowRegPass.isSelected()) {
                regPassShown.setManaged(true);
                regPassShown.setVisible(true);
                regPass.setManaged(false);
                regPass.setVisible(false);
            } else {
                regPass.setManaged(true);
                regPass.setVisible(true);
                regPassShown.setManaged(false);
                regPassShown.setVisible(false);
            }
        });
        
        regName.setPrefWidth(250); regSurname.setPrefWidth(250); regID.setPrefWidth(250);
        regPhone.setPrefWidth(250); regUser.setPrefWidth(250); regPass.setPrefWidth(250);

        Button btnRegister = new Button("Kayıt Ol");
        btnRegister.setStyle("-fx-base: #2ecc71; -fx-text-fill: white;");
        btnRegister.setPrefWidth(250);

        Hyperlink linkBack = new Hyperlink("Giriş Ekranına Dön");
        linkBack.setOnAction(e -> showLoginScreen());

        btnRegister.setOnAction(e -> handleRegistration(regName, regSurname, regID, regPhone, regUser, regPass));

        registerBox.getChildren().addAll(lblHeader, regName, regSurname, regID, regPhone, new Separator(), regUser, regPassStack, chkShowRegPass, btnRegister, linkBack);
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
        	showAlert(Alert.AlertType.WARNING, "Eksik Bilgi", "Lütfen tüm alanları doldurunuz.");
            return;
        }
        
        Passenger newP = new Passenger(
            id.getText(), name.getText(), sur.getText(), 
            phone.getText(), user.getText(), pass.getText()
        );
        
        mainApp.savePassengerToFile(newP);
        
        showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Kayıt tamamlandı! Şimdi giriş yapabilirsiniz.");
        showLoginScreen();
    }

    private void handlePassengerLogin(String username, String password) {
    	List<Passenger> list = mainApp.getPassengerList();
        Passenger foundPassenger = null;
        boolean found = false;
        int i = 0;
        
        while (i < list.size() && !found) {
            Passenger p = list.get(i);
            
            if (p.getUsername() != null && p.getUsername().equals(username) && 
                p.getPassword() != null && p.getPassword().equals(password)) {
                
                foundPassenger = p;
                found = true;
            }
            i++;
        }

        if (found) {
            System.out.println("Yolcu Girişi Başarılı: " + foundPassenger.getName());
            mainApp.showUserSearchScreen(foundPassenger); 
        } else {
            showAlert(Alert.AlertType.ERROR, "Giriş Başarısız", "Kullanıcı adı veya şifre hatalı.");
        }
    }

    private void handleAdminLogin(String username, String password) {
        Staff foundStaff = staffManager.validateLogin(username, password);

        if (foundStaff != null) {
            if (foundStaff.getRole().equalsIgnoreCase("Admin")) {
                System.out.println("Personel Girişi Başarılı: " + foundStaff.getUsername());
                mainApp.showAdminDashboard(foundStaff);
            } else {
                showAlert(Alert.AlertType.WARNING, "Yetkisiz Giriş", "Bu panele sadece yöneticiler erişebilir.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Giriş Başarısız", "Kullanıcı adı veya şifre hatalı.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
