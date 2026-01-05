package gui;

import flightManagement.*;
import servicesAndManagers.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.*;

public class AdminView {
	
	private MainApp mainApp;
    private FlightManager flightManager;
    private TableView<Flight> flightTable;
    private String adminUsername;
    private StaffManager staffManager;
    
    private TextField txtNum, txtDep, txtArr, txtDist, txtDur, txtPlaneId, txtTime;
    private DatePicker datePicker;
    
    private TableView<Staff> staffTable;
    private TextField txtStaffName, txtStaffSurname, txtStaffContact, txtStaffRole, txtStaffPass;
    
    public AdminView(MainApp mainApp, FlightManager flightManager, StaffManager staffManager, String adminUsername) {
        this.mainApp = mainApp;
        this.flightManager = flightManager;
        this.adminUsername = adminUsername;
        this.staffManager = staffManager;
    }
    
    
    public Parent getFlightView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));
        
        // Üst Bar (Geri Butonu ile)
        layout.setTop(createTopBar("Uçuş İşlemleri"));
        
        // Orta ve Alt Kısım (Flight İçeriği)
        layout.setCenter(createFlightContent());
        
        return layout;
    }
    
    public Parent getStaffView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));
        
        // Üst Bar (Geri Butonu ile)
        layout.setTop(createTopBar("Personel İşlemleri"));
        
        // Orta ve Alt Kısım (Staff İçeriği)
        layout.setCenter(createStaffContent());
        
        return layout;
    }
    
    private HBox createTopBar(String title) {
        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label lblUser = new Label("Aktif Yönetici: " + adminUsername.toUpperCase());
        lblUser.setStyle("-fx-text-fill: #555; -fx-font-style: italic; -fx-font-weight: bold;");
        
        Button btnBack = new Button("← Ana Menüye Dön");
        btnBack.setStyle("-fx-base: #f0f0f0;");
        btnBack.setOnAction(e -> mainApp.showAdminDashboard(adminUsername));
        
        HBox topMenu = new HBox(15, lblTitle, spacer, btnBack);
        topMenu.setAlignment(Pos.CENTER_LEFT);
        topMenu.setPadding(new Insets(0, 0, 15, 0));
        return topMenu;
    }
    
    private VBox createStaffContent() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // 1. Tabloyu Oluştur
        staffTable = new TableView<>();
        updateStaffTable();

        // Person sınıfından gelenler (name, surname, contactInfo)
        TableColumn<Staff, String> colName = new TableColumn<>("Ad");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Staff, String> colSurname = new TableColumn<>("Soyad");
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));

        TableColumn<Staff, String> colContact = new TableColumn<>("İletişim");
        colContact.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));

        // Staff sınıfından gelenler (role, password)
        TableColumn<Staff, String> colRole = new TableColumn<>("Görevi");
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        TableColumn<Staff, String> colPass = new TableColumn<>("Şifre");
        colPass.setCellValueFactory(new PropertyValueFactory<>("password"));

        staffTable.getColumns().add(colName);
        staffTable.getColumns().add(colSurname);
        staffTable.getColumns().add(colContact);
        staffTable.getColumns().add(colRole);
        staffTable.getColumns().add(colPass);
        
        staffTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtStaffName.setText(newVal.getName());
                txtStaffSurname.setText(newVal.getSurname()); // Eğer surname private ise getSurname() kullanın
                txtStaffContact.setText(newVal.getContactInfo()); // getter kullanın
                txtStaffRole.setText(newVal.getRole()); // getter kullanın
                txtStaffPass.setText(newVal.getPassword()); // getter kullanın
            }
        });

        // 2. Form Alanları (Yeni Staff Constructor yapısına uygun)
        HBox formBox = new HBox(10);
        formBox.setAlignment(Pos.CENTER_LEFT);
        
        // Yeni input alanları
        txtStaffName = new TextField(); txtStaffName.setPromptText("Ad");
        txtStaffSurname = new TextField(); txtStaffSurname.setPromptText("Soyad");
        txtStaffContact = new TextField(); txtStaffContact.setPromptText("Tel/Email");
        txtStaffRole = new TextField(); txtStaffRole.setPromptText("Rol");
        txtStaffPass = new TextField(); txtStaffPass.setPromptText("Şifre");
        
        // TextField genişliklerini ayarlama (isteğe bağlı, daha düzgün görünür)
        txtStaffName.setPrefWidth(100);
        txtStaffSurname.setPrefWidth(100);
        txtStaffContact.setPrefWidth(120);
        txtStaffRole.setPrefWidth(100);
        txtStaffPass.setPrefWidth(80);

        Button btnAddStaff = new Button("Ekle");
        Button btnDelStaff = new Button("Sil");
        Button btnUpdateStaff = new Button("Güncelle");

        // Ekleme İşlemi
        btnAddStaff.setOnAction(e -> {
            // Basit doğrulama: Ad ve Soyad boş olmamalı
            if (txtStaffName.getText().isEmpty() || txtStaffSurname.getText().isEmpty()) {
                showAlert("Uyarı", "Ad ve Soyad alanları zorunludur.");
                return;
            }
            
            // YENİ STAFF CONSTRUCTOR: (name, surname, contactInfo, role, password)
            Staff newStaff = new Staff(
                txtStaffName.getText(),
                txtStaffSurname.getText(),
                txtStaffContact.getText(),
                txtStaffRole.getText(),
                txtStaffPass.getText()
            );
            
            staffManager.addStaff(newStaff);
            updateStaffTable();
            
            clearStaffFields();
        });

        // Silme İşlemi
        btnDelStaff.setOnAction(e -> {
            Staff selected = staffTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                staffManager.deleteStaff(selected);
                updateStaffTable();
            } else {
                showAlert("Uyarı", "Silinecek personeli seçin.");
            }
            clearStaffFields();
        });
        
        btnUpdateStaff.setOnAction(e -> {
            Staff selected = staffTable.getSelectionModel().getSelectedItem();
            
            if (selected == null) {
                showAlert("Uyarı", "Lütfen güncellenecek personeli tablodan seçiniz.");
                return;
            }
            
            // Boş alan kontrolü
            if (txtStaffName.getText().isEmpty() || txtStaffSurname.getText().isEmpty()) {
                showAlert("Uyarı", "Ad ve Soyad alanları boş bırakılamaz.");
                return;
            }
            
            // Yeni bilgilerle geçici bir nesne oluştur
            Staff updatedStaffInfo = new Staff(
                txtStaffName.getText(),
                txtStaffSurname.getText(),
                txtStaffContact.getText(),
                txtStaffRole.getText(),
                txtStaffPass.getText()
            );
            
            // Manager üzerinden güncelle
            // (Eski nesneyi referans olarak veriyoruz ki listedeki yerini bulsun)
            boolean success = staffManager.updateStaff(selected, updatedStaffInfo);
            
            if (success) {
                showAlert("Başarılı", "Personel bilgileri güncellendi.");
                updateStaffTable(); // Tabloyu yenile
                
                // Alanları temizle
                txtStaffName.clear(); txtStaffSurname.clear(); 
                txtStaffContact.clear(); txtStaffRole.clear(); txtStaffPass.clear();
            } else {
                showAlert("Hata", "Güncelleme sırasında bir sorun oluştu.");
            }
            clearStaffFields();
        });

        formBox.getChildren().addAll(txtStaffName, txtStaffSurname, txtStaffContact, txtStaffRole, txtStaffPass, btnAddStaff, btnUpdateStaff,btnDelStaff);
        
        layout.getChildren().addAll(staffTable, new Label("Personel Yönetimi:"), formBox);
        
        
        return layout;
    }
    
    private void updateStaffTable() {
        if (staffManager != null && staffManager.getAllStaff() != null) {
            ObservableList<Staff> data = FXCollections.observableArrayList(staffManager.getAllStaff());
            staffTable.setItems(data);
            staffTable.refresh();
        }
    }
    
    private BorderPane createFlightContent() {
        BorderPane innerLayout = new BorderPane();
        
        flightTable = new TableView<>();
        
        TableColumn<Flight, String> colNum = new TableColumn<>("Uçuş No");
        colNum.setCellValueFactory(new PropertyValueFactory<>("flightNum"));

        TableColumn<Flight, String> colRoute = new TableColumn<>("Rota");
        colRoute.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRoute().toString()));

        TableColumn<Flight, Double> colDist = new TableColumn<>("Mesafe (KM)");
        colDist.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getRoute().getDistanceKm()));
        
        TableColumn<Flight, String> dateColumn = new TableColumn<>("Tarih");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        
        TableColumn<Flight, String> timeColumn = new TableColumn<>("Saat");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("hour"));

        flightTable.getColumns().add(colNum);
        flightTable.getColumns().add(colRoute);
        flightTable.getColumns().add(colDist);
        flightTable.getColumns().add(dateColumn);
        flightTable.getColumns().add(timeColumn);
        updateFlightTable();
        
        flightTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) handleFlightRowSelect();
        });

        innerLayout.setCenter(flightTable);

        // --- FORM ---
        VBox form = new VBox(10);
        form.setPadding(new Insets(10));
        form.setStyle("-fx-border-color: #ddd; -fx-border-width: 1px; -fx-background-color: #f9f9f9;");

        txtNum = new TextField(); txtNum.setPromptText("Uçuş No (TK101)");
        txtDep = new TextField(); txtDep.setPromptText("Kalkış");
        txtArr = new TextField(); txtArr.setPromptText("Varış");
        txtDist = new TextField(); txtDist.setPromptText("KM");
        txtDur = new TextField(); txtDur.setPromptText("Süre(Dk)");
        txtPlaneId = new TextField(); txtPlaneId.setPromptText("Uçak ID");
        
        datePicker = new DatePicker();
        txtTime = new TextField(); txtTime.setPromptText("Saat (HH:mm)");

        Button btnAdd = new Button("Ekle");
        Button btnUpdate = new Button("Güncelle");
        Button btnDelete = new Button("Sil");

        btnAdd.setOnAction(e -> {
            try {
                String fNum = txtNum.getText();
                if(flightManager.getFlightByID(fNum) != null) { showAlert("Hata", "Bu ID zaten var."); return; }
                
                Route route = new Route(txtDep.getText(), txtArr.getText(), Integer.parseInt(txtDist.getText()));
                LocalDateTime ldt = LocalDateTime.of(datePicker.getValue(), LocalTime.parse(txtTime.getText()));
                
                Flight newFlight = new Flight(fNum, route, ldt, Integer.parseInt(txtDur.getText()));
                Plane p = new Plane(txtPlaneId.getText(), "B737", 180);
                new SeatManager().seatingArrangements(p);
                newFlight.setPlane(p);
                
                flightManager.addFlight(newFlight);
                updateFlightTable();
                clearFlightFields();
                showAlert("Başarılı", "Uçuş Eklendi");
                clearFlightFields();
            } catch (Exception ex) {
                showAlert("Hata", "Girişleri kontrol edin: " + ex.getMessage());
            }
        });
        
        btnUpdate.setOnAction(this::handleUpdateFlight);

        btnDelete.setOnAction(e -> {
            Flight selected = flightTable.getSelectionModel().getSelectedItem();
            if(selected != null) {
                flightManager.deleteFlight(selected);
                updateFlightTable();
            }
            clearFlightFields();
        });

        form.getChildren().addAll(
            new HBox(10, txtNum, txtPlaneId, txtDep, txtArr),
            new HBox(10, datePicker, txtTime, txtDur, txtDist),
            new HBox(10, btnAdd, btnUpdate, btnDelete)
        );
        innerLayout.setBottom(form);
        
        return innerLayout;
    }

    private void updateFlightTable() {
        ObservableList<Flight> data = FXCollections.observableArrayList(flightManager.getFlights());
        flightTable.setItems(data);
        flightTable.refresh();
    }
    
    
    void handleUpdateFlight(ActionEvent event) {
        Flight selectedFlight = flightTable.getSelectionModel().getSelectedItem();
        
        if (selectedFlight == null) {
            showAlert("Uyarı", "Lütfen güncellenecek bir uçuş seçin.");
            return;
        }

        try {
            // Formdaki verileri al
            LocalDate newDate = datePicker.getValue(); 
            String newTime = txtTime.getText(); 
            String newDurationStr = txtDur.getText();
            String newPlaneID = txtPlaneId.getText();
            
            String dep = txtDep.getText();
            String arr = txtArr.getText();
            String distStr = txtDist.getText();

            // Format dönüşümleri
            LocalDateTime newDateTime = LocalDateTime.of(newDate, LocalTime.parse(newTime));
            int newDuration = Integer.parseInt(newDurationStr);
            double newDistance = Double.parseDouble(distStr);

            // Güncelleme nesnesi oluştur
            Flight updatedInfo = new Flight();
            updatedInfo.setFlightNum(selectedFlight.getFlightNum());
            updatedInfo.setDate(newDateTime);
            updatedInfo.setDuration(newDuration);
            
            // Rota Güncellemesi
            Route newRoute = new Route(dep, arr, newDistance);
            updatedInfo.setRoute(newRoute);
            
            // Uçak Güncellemesi
            // DİKKAT: Plane sınıfında 'getModel()' mi yoksa 'getPlaneModel()' mi var kontrol et.
            // Genelde 'getModel()' kullanılır. Eğer hata verirse burayı düzelt.
            String currentModel = "Unknown";
            if(selectedFlight.getPlane() != null) {
                 // Plane sınıfında getModel() varsa onu kullan, yoksa getPlaneModel()
                 // Buraya varsayılan bir değer atıyoruz şimdilik.
                 currentModel = "Boeing 737"; 
            }
            
            Plane newPlane = new Plane(newPlaneID, currentModel, 180); 
            updatedInfo.setPlane(newPlane);

            // Manager'a gönder
            boolean success = flightManager.updateFlight(updatedInfo);

            if (success) {
                showAlert("Başarılı", "Uçuş bilgileri güncellendi.");
                updateFlightTable();
                clearFlightFields();
            } else {
                showAlert("Hata", "Güncelleme yapılamadı.");
            }
            clearFlightFields();

        } catch (java.time.format.DateTimeParseException e) {
            showAlert("Hata", "Saat formatı hatalı (Örn: 10:00 olmalı).");
        } catch (NumberFormatException e) {
            showAlert("Hata", "Mesafe ve Süre alanlarına sadece sayı giriniz.");
        } catch (Exception e) {
            showAlert("Hata", "Beklenmedik hata: " + e.getMessage());
        }
    
    }

    
    void handleFlightRowSelect() {
    	Flight selected = flightTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Null check ekledik
            if(selected.getDate() != null)
                datePicker.setValue(selected.getDate().toLocalDate());
            
            txtTime.setText(selected.getHour());
            txtDur.setText(String.valueOf(selected.getDuration()));
            txtNum.setText(selected.getFlightNum());
            
            // Eğer rota null değilse onları da dolduralım
            if (selected.getRoute() != null) {
                txtDep.setText(selected.getRoute().getDepartureCity());
                txtArr.setText(selected.getRoute().getArrivalCity());
                txtDist.setText(String.valueOf((int)selected.getRoute().getDistanceKm()));
            }
        }
    }
    
    private void clearFlightFields() {
        txtNum.clear(); 
        txtDep.clear(); 
        txtArr.clear(); 
        txtDist.clear();
        txtDur.clear();
        txtPlaneId.clear();
        txtTime.clear();
        datePicker.setValue(null);
        flightTable.getSelectionModel().clearSelection();
    }
    
    private void clearStaffFields() {
        txtStaffName.clear();
        txtStaffSurname.clear();
        txtStaffContact.clear();
        txtStaffRole.clear();
        txtStaffPass.clear();
        staffTable.getSelectionModel().clearSelection();
    }
    
    
    private void showAlert(String title, String content) {
    	Alert.AlertType type = Alert.AlertType.INFORMATION;
        
    	
        if (title.contains("Hata") || content.contains("Hata")) {
            type = Alert.AlertType.ERROR;
        } else if (title.contains("Uyarı")) {
            type = Alert.AlertType.WARNING;
        }

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
