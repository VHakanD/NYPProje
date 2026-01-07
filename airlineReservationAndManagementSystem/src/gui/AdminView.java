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
import java.util.Optional;

public class AdminView {
	
	private MainApp mainApp;
    private FlightManager flightManager;
    private TableView<Flight> flightTable;
    private Staff adminStaff;
    private StaffManager staffManager;
    private ReservationManager reservationManager;
    
    private TextField txtNum, txtDep, txtArr, txtDist, txtDur, txtPlaneId;
    private Spinner<Integer> spinHour, spinMinute;;
    private DatePicker datePicker;
    
    private TableView<Staff> staffTable;
    private TextField txtStaffName, txtStaffSurname, txtStaffContact, txtStaffRole, txtStaffUser, txtStaffPass;
    
    public AdminView(MainApp mainApp, FlightManager flightManager, ReservationManager reservationManager, StaffManager staffManager, Staff adminStaff) {
        this.mainApp = mainApp;
        this.flightManager = flightManager;
        this.reservationManager = reservationManager;
        this.staffManager = staffManager;
        this.adminStaff = adminStaff; // Nesneyi kaydet
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
        
        String fullName = adminStaff.getName().toUpperCase() + " " + adminStaff.getSurname().toUpperCase();
        Label lblUser = new Label("Aktif Yönetici: " + fullName);
        lblUser.setStyle("-fx-text-fill: #555; -fx-font-style: italic; -fx-font-weight: bold;");
        
        Button btnBack = new Button("← Ana Menüye Dön");
        btnBack.setStyle("-fx-base: #f0f0f0;");
        
        btnBack.setOnAction(e -> mainApp.showAdminDashboard(adminStaff));
        
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
        
        TableColumn<Staff, String> colUser = new TableColumn<>("Kullanıcı Adı");
        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        
        TableColumn<Staff, String> colPass = new TableColumn<>("Şifre");
        colPass.setCellValueFactory(new PropertyValueFactory<>("password"));

        staffTable.getColumns().add(colName);
        staffTable.getColumns().add(colSurname);
        staffTable.getColumns().add(colContact);
        staffTable.getColumns().add(colRole);
        staffTable.getColumns().add(colUser);
        staffTable.getColumns().add(colPass);
        
        staffTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtStaffName.setText(newVal.getName());
                txtStaffSurname.setText(newVal.getSurname()); // Eğer surname private ise getSurname() kullanın
                txtStaffContact.setText(newVal.getContactInfo()); // getter kullanın
                txtStaffRole.setText(newVal.getRole()); // getter kullanın
                txtStaffUser.setText(newVal.getUsername());
                txtStaffPass.setText(newVal.getPassword()); // getter kullanın
            }
        });

        // 2. Form Alanları (Yeni Staff Constructor yapısına uygun)
        HBox formBox = new HBox(10);
        formBox.setAlignment(Pos.CENTER_LEFT);
        
        txtStaffUser = new TextField(); 
        txtStaffUser.setPromptText("Kullanıcı Adı");
        txtStaffUser.setPrefWidth(100);
        
        txtStaffPass = new TextField();
        txtStaffPass.setPromptText("Şifre");
        txtStaffPass.setPrefWidth(80);
        
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
                    txtStaffUser.getText(), // Username
                    txtStaffPass.getText(), // Password
                    txtStaffName.getText(),
                    txtStaffSurname.getText(),
                    txtStaffContact.getText(),
                    txtStaffRole.getText()
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
                    txtStaffUser.getText(), // Username
                    txtStaffPass.getText(), // Password
                    txtStaffName.getText(),
                    txtStaffSurname.getText(),
                    txtStaffContact.getText(),
                    txtStaffRole.getText()
                );
            
            // Manager üzerinden güncelle
            // (Eski nesneyi referans olarak veriyoruz ki listedeki yerini bulsun)
            boolean success = staffManager.updateStaff(selected, updatedStaffInfo);
            
            if (success) {
                showAlert("Başarılı", "Personel bilgileri güncellendi.");
                updateStaffTable(); // Tabloyu yenile
                
                // Alanları temizle
                txtStaffName.clear(); txtStaffSurname.clear(); txtStaffUser.clear();
                txtStaffContact.clear(); txtStaffRole.clear(); txtStaffPass.clear();
            } else {
                showAlert("Hata", "Güncelleme sırasında bir sorun oluştu.");
            }
            clearStaffFields();
        });

        formBox.getChildren().addAll(txtStaffName, txtStaffSurname, txtStaffContact, txtStaffRole, txtStaffUser, txtStaffPass, btnAddStaff, btnUpdateStaff,btnDelStaff);
        
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
        
        TableColumn<Flight, String> colModel = new TableColumn<>("Uçak Modeli");
        colModel.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPlane().getPlaneModel()));

        flightTable.getColumns().add(colNum);
        flightTable.getColumns().add(colModel);
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
        
        spinHour = new Spinner<>(0, 23, 12);
        spinHour.setEditable(true); // Elle yazmaya izin ver
        spinHour.setPrefWidth(70);

        // Dakika: 0-59 arası, varsayılan 00
        spinMinute = new Spinner<>(0, 59, 0);
        spinMinute.setEditable(true);
        spinMinute.setPrefWidth(70);

        // Değerler döngüsel olsun (59'dan sonra 0'a geçsin)
        SpinnerValueFactory.IntegerSpinnerValueFactory hourFactory = 
            (SpinnerValueFactory.IntegerSpinnerValueFactory) spinHour.getValueFactory();
        hourFactory.setWrapAround(true);

        SpinnerValueFactory.IntegerSpinnerValueFactory minFactory = 
            (SpinnerValueFactory.IntegerSpinnerValueFactory) spinMinute.getValueFactory();
        minFactory.setWrapAround(true);

        Button btnAdd = new Button("Ekle");
        Button btnUpdate = new Button("Güncelle");
        Button btnDelete = new Button("Sil");
        
        Button btnClear = new Button("Temizle");
        btnClear.setStyle("-fx-base: #95a5a6; -fx-text-fill: white;"); // Gri renk
        btnClear.setOnAction(e -> clearFlightFields());

        btnAdd.setOnAction(e -> {
        	try {
                String fNum = txtNum.getText();
                if(flightManager.getFlightByID(fNum) != null) { 
                    showAlert("Hata", "Bu Uçuş No zaten var."); return; 
                }
                
                // 1. UÇAĞI BUL (YENİ KISIM)
                String planeIdInput = txtPlaneId.getText().trim();
                Plane selectedPlane = flightManager.getPlaneTemplateByID(planeIdInput);
                
                if (selectedPlane == null) {
                    showAlert("Hata", "Girilen Uçak ID (" + planeIdInput + ") sistemde kayıtlı değil!\nLütfen planes.txt dosyasını kontrol edin.");
                    return;
                }

                // 2. KOLTUKLARI DÖŞE (SeatManager ile)
                // Uçağın kapasitesi planes.txt'den geldiği için (örn: 240), koltuklar ona göre oluşacak.
                new SeatManager().seatingArrangements(selectedPlane);

                // 3. UÇUŞU OLUŞTUR
                String timeStr = String.format("%02d:%02d", spinHour.getValue(), spinMinute.getValue());
                Route route = new Route(txtDep.getText(), txtArr.getText(), Integer.parseInt(txtDist.getText()));
                LocalDateTime ldt = LocalDateTime.of(datePicker.getValue(), LocalTime.parse(timeStr));
                
                Flight newFlight = new Flight(fNum, route, ldt, Integer.parseInt(txtDur.getText()));
                newFlight.setPlane(selectedPlane); // Bulduğumuz uçağı atadık
                
                flightManager.addFlight(newFlight);
                updateFlightTable();
                clearFlightFields();
                showAlert("Başarılı", "Uçuş Eklendi.\nAtanan Uçak: " + selectedPlane.getPlaneModel() + "\nKapasite: " + selectedPlane.getCapacity());
                
            } catch (Exception ex) {
                showAlert("Hata", "Girişleri kontrol edin: " + ex.getMessage());
            }
        });
        
        btnUpdate.setOnAction(this::handleUpdateFlight);

        /*btnDelete.setOnAction(e -> {
        	Flight selected = flightTable.getSelectionModel().getSelectedItem();
            
            // 2. Eğer tablodan seçilmediyse ama ID kutusunda bir şey yazıyorsa, ID ile bulmaya çalış
            if (selected == null && !txtNum.getText().trim().isEmpty()) {
                String flightId = txtNum.getText().trim();
                selected = flightManager.getFlightByID(flightId);
            }

            if (selected != null) {
                // 3. Onay Penceresi (Güvenlik Önlemi)
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Uçuş Sil");
                alert.setHeaderText("Uçuş Siliniyor: " + selected.getFlightNum());
                alert.setContentText("Bu işlem geri alınamaz. Silmek istediğinize emin misiniz?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    
                    // 4. Silme işlemini gerçekleştir
                    flightManager.deleteFlight(selected);
                    
                    // 5. Tabloyu ve ekranı güncelle
                    updateFlightTable();
                    clearFlightFields();
                    
                    showAlert("Başarılı", "Uçuş başarıyla silindi.");
                }
            } else {
                showAlert("Uyarı", "Lütfen silinecek uçuşu listeden seçiniz veya geçerli bir Uçuş No giriniz.");
            }
        });*/
        
        btnDelete.setOnAction(e -> {
            Flight selected = flightTable.getSelectionModel().getSelectedItem();
            
            if (selected == null && !txtNum.getText().trim().isEmpty()) {
                String flightId = txtNum.getText().trim();
                selected = flightManager.getFlightByID(flightId);
            }

            if (selected != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Uçuş Sil");
                alert.setHeaderText("Uçuş Siliniyor: " + selected.getFlightNum());
                // Uyarıyı güçlendirdik
                alert.setContentText("DİKKAT: Bu uçuşu silerseniz, uçuşa ait TÜM BİLETLER de silinecektir!\nOnaylıyor musunuz?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    
                    // 1. Önce bu uçuşa ait rezervasyonları sil
                    reservationManager.cancelReservationsByFlightID(selected.getFlightNum());
                    
                    // 2. Sonra uçuşu sil
                    flightManager.deleteFlight(selected);
                    
                    updateFlightTable();
                    clearFlightFields();
                    showAlert("Başarılı", "Uçuş ve ilgili tüm biletler silindi.");
                }
            } else {
                showAlert("Uyarı", "Lütfen silinecek uçuşu seçiniz.");
            }
        });

        form.getChildren().addAll(
            new HBox(10, txtNum, txtPlaneId, txtDep, txtArr),
            new HBox(10, datePicker, new Label("Saat:"), spinHour, new Label(":"), spinMinute, txtDur, txtDist),
            new HBox(10, btnAdd, btnUpdate, btnDelete, btnClear)
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
        	String newTime = String.format("%02d:%02d", spinHour.getValue(), spinMinute.getValue());
        	
            // Formdaki verileri al
            LocalDate newDate = datePicker.getValue();  
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
            
            String inputPlaneID = txtPlaneId.getText().trim();
            
            if (selectedFlight.getPlane() == null || !selectedFlight.getPlane().getPlaneID().equals(inputPlaneID)) {
                Plane newPlane = flightManager.getPlaneTemplateByID(inputPlaneID);
                
                if (newPlane == null) {
                    showAlert("Hata", "Güncellenen Uçak ID sistemde bulunamadı!");
                    return;
                }
                
                // Yeni uçağın koltuklarını hazırla
                new SeatManager().seatingArrangements(newPlane);
                updatedInfo.setPlane(newPlane);
            } else {
                // ID değişmediyse mevcut uçağı koru (Koltuk düzenini bozma)
                updatedInfo.setPlane(selectedFlight.getPlane());
            }

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
           
            String timeStr = selected.getHour(); // "14:30" döner
            if (timeStr != null && timeStr.contains(":")) {
                try {
                    String[] parts = timeStr.split(":");
                    int h = Integer.parseInt(parts[0]);
                    int m = Integer.parseInt(parts[1]);
                    
                    // Spinner değerlerini set et
                    spinHour.getValueFactory().setValue(h);
                    spinMinute.getValueFactory().setValue(m);
                } catch (NumberFormatException e) {
                    // Format hatası varsa varsayılan 12:00 olsun
                    spinHour.getValueFactory().setValue(12);
                    spinMinute.getValueFactory().setValue(0);
                }
            }
           
            txtDur.setText(String.valueOf(selected.getDuration()));
            txtNum.setText(selected.getFlightNum());
            
            // Eğer rota null değilse onları da dolduralım
            if (selected.getRoute() != null) {
                txtDep.setText(selected.getRoute().getDepartureCity());
                txtArr.setText(selected.getRoute().getArrivalCity());
                txtDist.setText(String.valueOf((int)selected.getRoute().getDistanceKm()));
            }
            
            txtPlaneId.setText(selected.getPlane().getPlaneID());
        }
    }
    
    private void clearFlightFields() {
        txtNum.clear(); 
        txtDep.clear(); 
        txtArr.clear(); 
        txtDist.clear();
        txtDur.clear();
        txtPlaneId.clear();
        spinHour.getValueFactory().setValue(12);
        spinMinute.getValueFactory().setValue(0);
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
