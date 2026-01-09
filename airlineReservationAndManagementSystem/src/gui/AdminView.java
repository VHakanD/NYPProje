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
        this.adminStaff = adminStaff;
    }
    
    public Parent getFlightView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));
        
        layout.setTop(createTopBar("Uçuş İşlemleri"));
        
        
        layout.setCenter(createFlightContent());
        
        return layout;
    }
    
    public Parent getStaffView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));
        
        layout.setTop(createTopBar("Personel İşlemleri"));
        
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
        
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(0, 0, 10, 0));
        
        Label lblSearch = new Label("Görev Filtrele:");
        lblSearch.setStyle("-fx-font-weight: bold; -fx-text-fill: #2980b9;");
        
        TextField txtSearchRole = new TextField();
        txtSearchRole.setPromptText("Örn: Pilot, Hostes, Admin...");
        txtSearchRole.setPrefWidth(250);
        
        txtSearchRole.textProperty().addListener((obs, oldVal, newVal) -> {
            filterStaffByRole(newVal);
        });
        
        searchBox.getChildren().addAll(lblSearch, txtSearchRole);

        staffTable = new TableView<>();
        updateStaffTable();

        TableColumn<Staff, String> colName = new TableColumn<>("Ad");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Staff, String> colSurname = new TableColumn<>("Soyad");
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));

        TableColumn<Staff, String> colContact = new TableColumn<>("İletişim");
        colContact.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));

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
                txtStaffSurname.setText(newVal.getSurname());
                txtStaffContact.setText(newVal.getContactInfo());
                txtStaffRole.setText(newVal.getRole());
                txtStaffUser.setText(newVal.getUsername());
                txtStaffPass.setText(newVal.getPassword());
            }
        });

        HBox formBox = new HBox(10);
        formBox.setAlignment(Pos.CENTER_LEFT);
        
        txtStaffUser = new TextField(); 
        txtStaffUser.setPromptText("Kullanıcı Adı");
        txtStaffUser.setPrefWidth(100);
        
        txtStaffPass = new TextField();
        txtStaffPass.setPromptText("Şifre");
        txtStaffPass.setPrefWidth(80);
        
        txtStaffName = new TextField(); txtStaffName.setPromptText("Ad");
        txtStaffSurname = new TextField(); txtStaffSurname.setPromptText("Soyad");
        txtStaffContact = new TextField(); txtStaffContact.setPromptText("Tel/Email");
        txtStaffRole = new TextField(); txtStaffRole.setPromptText("Rol");
        txtStaffPass = new TextField(); txtStaffPass.setPromptText("Şifre");
        
        txtStaffName.setPrefWidth(100);
        txtStaffSurname.setPrefWidth(100);
        txtStaffContact.setPrefWidth(120);
        txtStaffRole.setPrefWidth(100);
        txtStaffPass.setPrefWidth(80);

        Button btnAddStaff = new Button("Ekle");
        btnAddStaff.setStyle("-fx-base: #35f121; -fx-text-fill: white; -fx-font-weight: bold;");
        Button btnDelStaff = new Button("Sil");
        btnDelStaff.setStyle("-fx-base: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        Button btnUpdateStaff = new Button("Güncelle");
        btnUpdateStaff.setStyle("-fx-base: #2372ea; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Button btnClearStaff = new Button("Temizle");
        btnClearStaff.setStyle("-fx-base: #95a5a6; -fx-text-fill: white;"); 
        btnClearStaff.setOnAction(e -> clearStaffFields());

        btnAddStaff.setOnAction(e -> {
            if (txtStaffName.getText().isEmpty() || txtStaffSurname.getText().isEmpty()) {
                showAlert("Uyarı", "Ad ve Soyad alanları zorunludur.");
                return;
            }
            
            Staff newStaff = new Staff(
                    txtStaffUser.getText(),
                    txtStaffPass.getText(),
                    txtStaffName.getText(),
                    txtStaffSurname.getText(),
                    txtStaffContact.getText(),
                    txtStaffRole.getText()
                );
            
            staffManager.addStaff(newStaff);
            updateStaffTable();
            
            clearStaffFields();
        });

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
            
            if (txtStaffName.getText().isEmpty() || txtStaffSurname.getText().isEmpty()) {
                showAlert("Uyarı", "Ad ve Soyad alanları boş bırakılamaz.");
                return;
            }
            
            Staff updatedStaffInfo = new Staff(
                    txtStaffUser.getText(), 
                    txtStaffPass.getText(),
                    txtStaffName.getText(),
                    txtStaffSurname.getText(),
                    txtStaffContact.getText(),
                    txtStaffRole.getText()
                );
            
            
            boolean success = staffManager.updateStaff(selected, updatedStaffInfo);
            
            if (success) {
                showAlert("Başarılı", "Personel bilgileri güncellendi.");
                updateStaffTable();
                
                txtStaffName.clear(); txtStaffSurname.clear(); txtStaffUser.clear();
                txtStaffContact.clear(); txtStaffRole.clear(); txtStaffPass.clear();
            } else {
                showAlert("Hata", "Güncelleme sırasında bir sorun oluştu.");
            }
            clearStaffFields();
        });

        formBox.getChildren().addAll(txtStaffName, txtStaffSurname, txtStaffContact, txtStaffRole, txtStaffUser, txtStaffPass, btnAddStaff, btnUpdateStaff, btnDelStaff, btnClearStaff);
        
        layout.getChildren().addAll(searchBox, staffTable, new Label("Personel Yönetimi:"), formBox);
        
        
        return layout;
    }
    
    private void updateStaffTable() {
        if (staffManager != null && staffManager.getAllStaff() != null) {
            ObservableList<Staff> data = FXCollections.observableArrayList(staffManager.getAllStaff());
            staffTable.setItems(data);
            staffTable.refresh();
        }
    }
    
    private void filterStaffByRole(String roleQuery) {
        if (staffManager == null || staffManager.getAllStaff() == null) return;

        if (roleQuery == null || roleQuery.trim().isEmpty()) {
            updateStaffTable();
            return;
        }

        String lowerQuery = roleQuery.toLowerCase(java.util.Locale.ENGLISH);
        ObservableList<Staff> filteredList = FXCollections.observableArrayList();

        for (Staff s : staffManager.getAllStaff()) {
            if (s.getRole() != null && s.getRole().toLowerCase(java.util.Locale.ENGLISH).contains(lowerQuery)) {
                filteredList.add(s);
            }
        }
        
        staffTable.setItems(filteredList);
        staffTable.refresh();
    }
    
    private BorderPane createFlightContent() {
        BorderPane innerLayout = new BorderPane();
        
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(0, 0, 10, 0));

        Label lblSearch = new Label("Ara:");
        
        ComboBox<String> cmbSearchType = new ComboBox<>();
        cmbSearchType.getItems().addAll("Tümü", "Uçuş No", "Kalkış Yeri", "Varış Yeri", "Uçak Modeli");
        cmbSearchType.setValue("Tümü");
        cmbSearchType.setPrefWidth(120);

        
        TextField txtSearchFlight = new TextField();
        txtSearchFlight.setPromptText("Aranacak kelime...");
        txtSearchFlight.setPrefWidth(250);
        
        
        txtSearchFlight.textProperty().addListener((obs, oldVal, newVal) -> 
            filterFlights(newVal, cmbSearchType.getValue()));
            
        cmbSearchType.valueProperty().addListener((obs, oldVal, newVal) -> 
            filterFlights(txtSearchFlight.getText(), newVal));
        
        searchBox.getChildren().addAll(lblSearch, cmbSearchType, txtSearchFlight);
        
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
        
        VBox centerBox = new VBox(searchBox, flightTable);
        innerLayout.setCenter(centerBox);

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
        spinHour.setEditable(true);
        spinHour.setPrefWidth(70);

        spinMinute = new Spinner<>(0, 59, 0);
        spinMinute.setEditable(true);
        spinMinute.setPrefWidth(70);

        
        SpinnerValueFactory.IntegerSpinnerValueFactory hourFactory = 
            (SpinnerValueFactory.IntegerSpinnerValueFactory) spinHour.getValueFactory();
        hourFactory.setWrapAround(true);

        SpinnerValueFactory.IntegerSpinnerValueFactory minFactory = 
            (SpinnerValueFactory.IntegerSpinnerValueFactory) spinMinute.getValueFactory();
        minFactory.setWrapAround(true);

        Button btnAdd = new Button("Ekle");
        btnAdd.setStyle("-fx-base: #35f121; -fx-text-fill: white; -fx-font-weight: bold;");
        Button btnUpdate = new Button("Güncelle");
        btnUpdate.setStyle("-fx-base: #2372ea; -fx-text-fill: white; -fx-font-weight: bold;");
        Button btnDelete = new Button("Sil");
        btnDelete.setStyle("-fx-base: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Button btnClear = new Button("Temizle");
        btnClear.setStyle("-fx-base: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");
        btnClear.setOnAction(e -> clearFlightFields());
        
        Button btnShowDetails = new Button("📋 Yolcu Listesi ve Doluluk");
        btnShowDetails.setStyle("-fx-base: #8e44ad; -fx-text-fill: white; -fx-font-weight: bold;");
        btnShowDetails.setOnAction(e -> handleShowFlightDetails());

        btnAdd.setOnAction(e -> {
        	try {
                String fNum = txtNum.getText();
                if(flightManager.getFlightByID(fNum) != null) { 
                    showAlert("Hata", "Bu Uçuş No zaten var."); return; 
                }
                
                String planeIdInput = txtPlaneId.getText().trim();
                Plane selectedPlane = flightManager.getPlaneTemplateByID(planeIdInput);
                
                if (selectedPlane == null) {
                    showAlert("Hata", "Girilen Uçak ID (" + planeIdInput + ") sistemde kayıtlı değil!\nLütfen planes.txt dosyasını kontrol edin.");
                    return;
                }

                new SeatManager().seatingArrangements(selectedPlane);

                String timeStr = String.format("%02d:%02d", spinHour.getValue(), spinMinute.getValue());
                Route route = new Route(txtDep.getText(), txtArr.getText(), Integer.parseInt(txtDist.getText()));
                
                if (datePicker.getValue() == null) { 
                	showAlert("Hata", "Lütfen bir tarih seçiniz."); 
                	return; 
                }
                
                LocalDateTime ldt = LocalDateTime.of(datePicker.getValue(), LocalTime.parse(timeStr));
                
                if (ldt.isBefore(LocalDateTime.now())) {
                    showAlert("Hata", "Geçmiş bir tarihe uçuş eklenemez!\nLütfen ileri bir tarih seçiniz.");
                    return;
                }
                
                Flight newFlight = new Flight(fNum, route, ldt, Integer.parseInt(txtDur.getText()));
                newFlight.setPlane(selectedPlane);
                
                flightManager.addFlight(newFlight);
                updateFlightTable();
                clearFlightFields();
                showAlert("Başarılı", "Uçuş Eklendi.\nAtanan Uçak: " + selectedPlane.getPlaneModel() + "\nKapasite: " + selectedPlane.getCapacity());
                
            } catch (Exception ex) {
                showAlert("Hata", "Girişleri kontrol edin: " + ex.getMessage());
            }
        });
        
        btnUpdate.setOnAction(this::handleUpdateFlight);
        
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
                alert.setContentText("DİKKAT: Bu uçuşu silerseniz, uçuşa ait TÜM BİLETLER de silinecektir!\nOnaylıyor musunuz?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    
                    reservationManager.cancelReservationsByFlightID(selected.getFlightNum());
                    
                    flightManager.deleteFlight(selected);
                    
                    updateFlightTable();
                    clearFlightFields();
                    showAlert("Başarılı", "Uçuş ve ilgili tüm biletler silindi.");
                }
            } else {
                showAlert("Uyarı", "Lütfen silinecek uçuşu seçiniz.");
            }
        });

        HBox buttonBox = new HBox(10, btnAdd, btnUpdate, btnDelete, btnClear, btnShowDetails);
        
        form.getChildren().addAll(
            new HBox(10, txtNum, txtPlaneId, txtDep, txtArr),
            new HBox(10, datePicker, new Label("Saat:"), spinHour, new Label(":"), spinMinute, txtDur, txtDist),
            buttonBox
        );
        innerLayout.setBottom(form);
        
        return innerLayout;
    }

    private void updateFlightTable() {
    	flightManager.removeExpiredFlights();
    	
    	reservationManager.cleanUpOrphanReservations();
    	
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

        String fNumInput = txtNum.getText().trim();
        String planeIdInput = txtPlaneId.getText().trim();
        String depInput = txtDep.getText().trim();
        String arrInput = txtArr.getText().trim();
        String durInput = txtDur.getText().trim();
        String distInput = txtDist.getText().trim();

        if (fNumInput.isEmpty() || planeIdInput.isEmpty() || depInput.isEmpty() || 
            arrInput.isEmpty() || durInput.isEmpty() || distInput.isEmpty()) {
            showAlert("Hata", "Lütfen tüm alanları doldurunuz! Boş alan bırakılamaz.");
            return;
        }
        
        if (datePicker.getValue() == null) {
            showAlert("Hata", "Lütfen bir tarih seçiniz.");
            return;
        }

        try {
            String oldFlightNum = selectedFlight.getFlightNum();
            
            if (!oldFlightNum.equalsIgnoreCase(fNumInput)) {
                if (flightManager.getFlightByID(fNumInput) != null) {
                    showAlert("Hata", "Bu Uçuş No (" + fNumInput + ") zaten kullanımda! Farklı bir numara giriniz.");
                    return;
                }
                selectedFlight.setFlightNum(fNumInput);
            }

            if (!selectedFlight.getPlane().getPlaneID().equals(planeIdInput)) {
                Plane newPlane = flightManager.getPlaneTemplateByID(planeIdInput);
                if (newPlane == null) {
                    selectedFlight.setFlightNum(oldFlightNum); 
                    showAlert("Hata", "Girilen Uçak ID (" + planeIdInput + ") sistemde bulunamadı!");
                    return;
                }
                new SeatManager().seatingArrangements(newPlane);
                selectedFlight.setPlane(newPlane);
            }
            String newTime = String.format("%02d:%02d", spinHour.getValue(), spinMinute.getValue());
            LocalDate newDate = datePicker.getValue();
            LocalDateTime newDateTime = LocalDateTime.of(newDate, LocalTime.parse(newTime));

            selectedFlight.setDate(newDateTime);
            selectedFlight.setDuration(Integer.parseInt(durInput));
            
            Route newRoute = new Route(depInput, arrInput, Double.parseDouble(distInput));
            selectedFlight.setRoute(newRoute);

            boolean success = flightManager.updateFlight(selectedFlight);

            if (success) {
                showAlert("Başarılı", "Uçuş bilgileri güncellendi.");
                updateFlightTable();
                clearFlightFields();
            } else {
                selectedFlight.setFlightNum(oldFlightNum);
                showAlert("Hata", "Güncelleme dosya sistemine yazılamadı.");
            }

        } catch (java.time.format.DateTimeParseException e) {
            showAlert("Hata", "Saat formatı hatalı.");
        } catch (NumberFormatException e) {
            showAlert("Hata", "Mesafe ve Süre alanlarına sadece sayı giriniz.");
        } catch (Exception e) {
            showAlert("Hata", "Beklenmedik hata: " + e.getMessage());
        }
    }

    
    void handleFlightRowSelect() {
    	Flight selected = flightTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if(selected.getDate() != null)
                datePicker.setValue(selected.getDate().toLocalDate());
           
            String timeStr = selected.getHour();
            if (timeStr != null && timeStr.contains(":")) {
                try {
                    String[] parts = timeStr.split(":");
                    int h = Integer.parseInt(parts[0]);
                    int m = Integer.parseInt(parts[1]);
                    
                    
                    spinHour.getValueFactory().setValue(h);
                    spinMinute.getValueFactory().setValue(m);
                } catch (NumberFormatException e) {
                	
                    spinHour.getValueFactory().setValue(12);
                    spinMinute.getValueFactory().setValue(0);
                }
            }
           
            txtDur.setText(String.valueOf(selected.getDuration()));
            txtNum.setText(selected.getFlightNum());
            
            if (selected.getRoute() != null) {
                txtDep.setText(selected.getRoute().getDepartureCity());
                txtArr.setText(selected.getRoute().getArrivalCity());
                txtDist.setText(String.valueOf((int)selected.getRoute().getDistanceKm()));
            }
            
            txtPlaneId.setText(selected.getPlane().getPlaneID());
        }
    }
    
    private void handleShowFlightDetails() {
        Flight selected = flightTable.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showAlert("Uyarı", "Lütfen detaylarını görmek istediğiniz uçuşu tablodan seçiniz.");
            return;
        }
        java.util.List<reservationAndTicketing.Reservation> resList = reservationManager.getReservationsByFlight(selected.getFlightNum());
        
        int capacity = selected.getPlane().getCapacity();
        int occupiedCount = resList.size();
        int emptyCount = capacity - occupiedCount;
        
        double occupancyRate = reservationManager.calculateOccupancyRate(selected);
        
        StringBuilder sb = new StringBuilder();
        sb.append("Uçuş: ").append(selected.getFlightNum()).append("\n");
        sb.append("Rota: ").append(selected.getRoute().toString()).append("\n");
        sb.append("Tarih: ").append(selected.getFormattedDate()).append(" ").append(selected.getHour()).append("\n\n");
        
        sb.append("--- DOLULUK DURUMU ---\n");
        sb.append("Toplam Kapasite: ").append(capacity).append("\n");
        sb.append("Dolu Koltuk: ").append(occupiedCount).append("\n");
        sb.append("Boş Koltuk: ").append(emptyCount).append("\n");
        sb.append(String.format("Doluluk Oranı: %% %.2f", occupancyRate)).append("\n\n");
        
        sb.append("--- YOLCU LİSTESİ ---\n");
        if (resList.isEmpty()) {
            sb.append("(Henüz yolcu yok)");
        } else {
            int count = 1;
            for (reservationAndTicketing.Reservation r : resList) {
                sb.append(count++).append(". ")
                  .append(r.getPassenger().getName()).append(" ")
                  .append(r.getPassenger().getSurname())
                  .append(" [Koltuk: ").append(r.getSeat().getSeatNum()).append("]")
                  .append("\n");
            }
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Uçuş Detayları");
        alert.setHeaderText(selected.getFlightNum() + " - Yolcu ve Kapasite Bilgisi");
        
        TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        
        alert.getDialogPane().setContent(textArea);
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(400, 500);
        
        alert.showAndWait();
    }
    
    private void filterFlights(String searchText, String searchType) {
        if (searchText == null || searchText.isEmpty()) {
            updateFlightTable();
            return;
        }

        String lowerSearch = searchText.toLowerCase(java.util.Locale.ENGLISH);
        ObservableList<Flight> filteredList = FXCollections.observableArrayList();

        for (Flight f : flightManager.getFlights()) {
            boolean match = false;
            
            switch (searchType) {
                case "Uçuş No":
                    match = f.getFlightNum().toLowerCase().contains(lowerSearch);
                    break;
                case "Kalkış Yeri":
                    match = f.getRoute().getDepartureCity().toLowerCase().contains(lowerSearch);
                    break;
                case "Varış Yeri":
                    match = f.getRoute().getArrivalCity().toLowerCase().contains(lowerSearch);
                    break;
                case "Uçak Modeli":
                    match = f.getPlane().getPlaneModel().toLowerCase().contains(lowerSearch);
                    break;
                case "Tümü":
                default:
                    match = f.getFlightNum().toLowerCase().contains(lowerSearch) || 
                            f.getRoute().toString().toLowerCase().contains(lowerSearch) ||
                            f.getPlane().getPlaneModel().toLowerCase().contains(lowerSearch);
                    break;
            }

            if (match) {
                filteredList.add(f);
            }
        }
        flightTable.setItems(filteredList);
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
        txtStaffUser.clear();
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
