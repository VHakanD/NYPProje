package gui;

import flightManagement.*;
import servicesAndManagers.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    private TableView<Flight> table;
    private String adminUsername;
    
    private TextField txtNum, txtDep, txtArr, txtDist, txtDur, txtPlaneId, txtTime;
    private DatePicker datePicker;

    public AdminView(MainApp mainApp, FlightManager flightManager, String adminUsername) {
        this.mainApp = mainApp;
        this.flightManager = flightManager;
        this.adminUsername = adminUsername;
    }

    public Parent getView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));

        
        Label lblTitle = new Label("Yönetici Paneli");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        // 2. Araya Esnek Boşluk (Spacer)
        // Bu eleman, sol ve sağ arasındaki tüm boşluğu kaplar ve diğerlerini iter.
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 3. Sağ Taraf: Kullanıcı Bilgisi ve Çıkış Butonu
        Label lblUser = new Label("Aktif Kullanıcı: " + adminUsername.toUpperCase());
        lblUser.setStyle("-fx-text-fill: #555; -fx-font-style: italic;");
        
        
        Button btnLogout = new Button("Çıkış Yap");
        btnLogout.setOnAction(e -> mainApp.showLoginScreen());
        
        HBox topMenu = new HBox(15, lblTitle, spacer, lblUser, btnLogout);
        topMenu.setAlignment(Pos.CENTER_LEFT);
        topMenu.setPadding(new Insets(0, 0, 20, 0));
        
        layout.setTop(topMenu);

        
        table = new TableView<>();
        updateTable();

        TableColumn<Flight, String> colNum = new TableColumn<>("Uçuş No");
        colNum.setCellValueFactory(new PropertyValueFactory<>("flightNum"));

        
        TableColumn<Flight, String> colRoute = new TableColumn<>("Rota");
        colRoute.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRoute().toString()));

        TableColumn<Flight, Double> colDist = new TableColumn<>("Mesafe (KM)");
        // Mesafe, Flight içinde değil, Flight -> Route -> DistanceKm içinde olduğu için özel bağlama yapıyoruz:
        colDist.setCellValueFactory(cellData -> 
        	new javafx.beans.property.SimpleObjectProperty<Double>(cellData.getValue().getRoute().getDistanceKm()));
        
        TableColumn<Flight, String> dateColumn = new TableColumn<>("Tarih");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        
        TableColumn<Flight, String> timeColumn = new TableColumn<>("Saat");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("hour"));

        table.getColumns().addAll(colNum, colRoute, colDist);
        table.getColumns().add(dateColumn);
        table.getColumns().add(timeColumn);
        layout.setCenter(table);

        
        VBox form = new VBox(10);
        form.setPadding(new Insets(10));
        form.setStyle("-fx-border-color: gray; -fx-border-width: 1px;");

        txtNum = new TextField(); txtNum.setPromptText("Uçuş No (Örn: TK101)");
        txtDep = new TextField(); txtDep.setPromptText("Kalkış (İl)");
        txtArr = new TextField(); txtArr.setPromptText("Varış (İl)");
        txtDist = new TextField(); txtDist.setPromptText("Mesafe (KM)");
        txtDur = new TextField(); txtDur.setPromptText("Süre (Dk)");
        txtPlaneId = new TextField(); txtPlaneId.setPromptText("Uçak ID (P01)");
        
        datePicker = new DatePicker();
        txtTime = new TextField(); txtTime.setPromptText("Saat (HH:mm)");

        Button btnAdd = new Button("Uçuş Ekle");
        Button btnUpdate = new Button("Uçuş Güncelle");
        Button btnDelete = new Button("Seçiliyi Sil");
        
        btnAdd.setOnAction(e -> {
            try {
            	String flightNumInput = txtNum.getText().trim();
                if (flightNumInput.isEmpty()) {
                    showAlert("Uyarı", "Lütfen bir uçuş numarası giriniz!");
                    return;
                }
                
                if (flightManager.getFlightByID(flightNumInput) != null) {
                    showAlert("Hata", "Bu uçuş numarası (" + flightNumInput + ") zaten sistemde mevcut! Lütfen farklı bir numara giriniz.");
                    return; // İşlemi burada kes, aşağıya inme.
                }
            	
            	
                Route route = new Route(txtDep.getText(), txtArr.getText(), Integer.parseInt(txtDist.getText()));
                
                if (datePicker.getValue() == null || txtTime.getText().isEmpty()) {
                    showAlert("Uyarı", "Lütfen tarih ve saat bilgisini eksiksiz giriniz.");
                    return;
                }
                
                LocalDateTime ldt = LocalDateTime.of(datePicker.getValue(), LocalTime.parse(txtTime.getText()));
                
                
                Flight newFlight = new Flight(txtNum.getText(), route, ldt, Integer.parseInt(txtDur.getText()));
                
                
                Plane newPlane = new Plane(txtPlaneId.getText(), "Boeing 737", 180); 
                // 180 sabit varsayıldı, burayı değiştirebiliriz
                
                
                SeatManager seatMngr = new SeatManager();
                seatMngr.seatingArrangements(newPlane);
                
                newFlight.setPlane(newPlane);

                
                flightManager.addFlight(newFlight);
                updateTable();
                
                showAlert("Başarılı", flightNumInput + " numaralı uçuş başarıyla eklendi.");
                txtNum.clear(); txtDep.clear(); txtArr.clear();
                
            } catch (NumberFormatException nfe) {
            	showAlert("Hata", "Mesafe ve Süre alanlarına sadece sayı girmelisiniz!");	
            }catch (Exception ex) {
            	showAlert("Hata", "Bir hata oluştu: " + ex.getMessage());
                ex.printStackTrace();
            }
            
        });
        
        btnUpdate.setOnAction(this::handleUpdateFlight);

        // SİLME BUTONU AKSİYONU
        btnDelete.setOnAction(e2 -> {
            Flight selected = table.getSelectionModel().getSelectedItem();
            if(selected != null) {
                flightManager.deleteFlight(selected);
                updateTable();
                clearFields();
                showAlert("Bilgi", "Uçuş silindi.");
            } else {
                showAlert("Uyarı", "Silinecek uçuşu seçiniz.");
            }
        });

        form.getChildren().addAll(
                new Label("İşlemler:"),
                new HBox(10, txtNum, txtPlaneId),
                new HBox(10, txtDep, txtArr, txtDist),
                new HBox(10, datePicker, txtTime, txtDur),
                new HBox(10, btnAdd, btnUpdate, btnDelete) // Butonları yan yana koyduk
            );
        layout.setBottom(form);

        return layout;
    }

    private void updateTable() {
        ObservableList<Flight> data = FXCollections.observableArrayList(flightManager.getFlights());
        table.setItems(data);
    }
    
    
    void handleUpdateFlight(ActionEvent event) {
        // 1. Tablodan seçili uçuşu al
        Flight selectedFlight = table.getSelectionModel().getSelectedItem();
        
        if (selectedFlight == null) {
            showAlert("Uyarı", "Lütfen güncellenecek bir uçuş seçin.");
            return;
        }

        // 2. Arayüzdeki (TextField/DatePicker) yeni değerleri al
        // Not: Kullanıcının girdiği yeni tarih ve süreyi alıyoruz
        LocalDate newDate = datePicker.getValue(); 
        String newTime = txtTime.getText(); // Örn: "14:30"
        String newDurationStr = txtDur.getText();

        try {
            // Yeni bir LocalDateTime oluştur
            LocalDateTime newDateTime = LocalDateTime.of(newDate, LocalTime.parse(newTime));
            int newDuration = Integer.parseInt(newDurationStr);

            // 3. Güncelleme için geçici bir nesne oluştur (ID'si aynı olmalı)
            Flight updatedInfo = new Flight();
            updatedInfo.setFlightNum(selectedFlight.getFlightNum()); // ID değişmez
            updatedInfo.setDate(newDateTime);
            updatedInfo.setDuration(newDuration);
            //updatedInfo.setRoute(newRoute);

            // 4. Manager üzerinden güncelleme işlemini yap
            boolean success = flightManager.updateFlight(updatedInfo);

            if (success) {
                showAlert("Başarılı", "Uçuş bilgileri güncellendi.");
                updateTable(); // Tabloyu yenileme metodu (verileri tekrar yükler)
            } else {
                showAlert("Hata", "Güncelleme yapılamadı.");
            }

        } catch (Exception e) {
            showAlert("Hata", "Lütfen tarih ve saat formatını kontrol edin.\nSaat formatı HH:mm olmalı.");
        }
    }

    
    void handleRowSelect() {
    	Flight selected = table.getSelectionModel().getSelectedItem();
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
    
    private void clearFields() {
        txtNum.clear(); 
        txtDep.clear(); 
        txtArr.clear(); 
        txtDist.clear();
        txtDur.clear();
        txtPlaneId.clear();
        txtTime.clear();
        datePicker.setValue(null);
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
