package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import reservationAndTicketing.Passenger;
import reservationAndTicketing.Reservation;
import servicesAndManagers.ReservationManager;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

public class ReservationManagementView {
	private MainApp mainApp;
    private ReservationManager reservationManager;
    private Passenger loggedInPassenger;
    private TableView<Reservation> table;

    public ReservationManagementView(MainApp mainApp, ReservationManager reservationManager, Passenger loggedInPassenger) {
        this.mainApp = mainApp;
        this.reservationManager = reservationManager;
        this.loggedInPassenger = loggedInPassenger;
    }

    public Parent getView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));

        // --- ÜST BAŞLIK ALANI ---
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        Label lblTitle = new Label("Rezervasyonlarım");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnBack = new Button("← Uçuş Aramaya Dön");
        btnBack.setOnAction(e -> mainApp.showUserSearchScreen(loggedInPassenger));

        topBar.getChildren().addAll(lblTitle, spacer, btnBack);
        layout.setTop(topBar);

        // --- TABLO OLUŞTURMA ---
        table = new TableView<>();
        createTableColumns();
        refreshTableData(); // Verileri yükle

        layout.setCenter(table);

        // --- ALT İŞLEM BUTONLARI ---
        HBox bottomBar = new HBox(15);
        bottomBar.setPadding(new Insets(15, 0, 0, 0));
        bottomBar.setAlignment(Pos.CENTER_RIGHT);

        Button btnCancel = new Button("Seçili Rezervasyonu İptal Et");
        btnCancel.setStyle("-fx-base: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnCancel.setPrefHeight(40);
        
        btnCancel.setOnAction(e -> handleCancelReservation());

        bottomBar.getChildren().add(btnCancel);
        layout.setBottom(bottomBar);

        return layout;
    }

    private void createTableColumns() {
        // PNR Kodu
        TableColumn<Reservation, String> colPNR = new TableColumn<>("PNR Kodu");
        colPNR.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getReservationCode()));

        // Uçuş No
        TableColumn<Reservation, String> colFlightNum = new TableColumn<>("Uçuş No");
        colFlightNum.setCellValueFactory(cell -> 
            new SimpleStringProperty(cell.getValue().getFlight().getFlightNum()));

        // Rota
        TableColumn<Reservation, String> colRoute = new TableColumn<>("Rota");
        colRoute.setCellValueFactory(cell -> 
            new SimpleStringProperty(cell.getValue().getFlight().getRoute().toString()));

        // Tarih ve Saat
        TableColumn<Reservation, String> colDate = new TableColumn<>("Uçuş Tarihi");
        colDate.setCellValueFactory(cell -> 
            new SimpleStringProperty(cell.getValue().getFlight().getFormattedDate() + " " + cell.getValue().getFlight().getHour()));

        // Koltuk
        TableColumn<Reservation, String> colSeat = new TableColumn<>("Koltuk");
        colSeat.setCellValueFactory(cell -> 
            new SimpleStringProperty(cell.getValue().getSeat().getSeatNum() + " (" + cell.getValue().getSeat().getSeatType() + ")"));
        
        // Rezervasyon Tarihi (İşlemin yapıldığı tarih)
        TableColumn<Reservation, String> colResDate = new TableColumn<>("İşlem Tarihi");
        colResDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDateOfReservation()));

        table.getColumns().addAll(colPNR, colFlightNum, colRoute, colDate, colSeat, colResDate);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void refreshTableData() {
        // Sadece giriş yapan yolcunun rezervasyonlarını getir
        ArrayList<Reservation> myReservations = reservationManager.getReservationsByPassenger(loggedInPassenger.getPassengerID());
        ObservableList<Reservation> data = FXCollections.observableArrayList(myReservations);
        table.setItems(data);
        table.refresh();
        
        if (myReservations.isEmpty()) {
            table.setPlaceholder(new Label("Henüz hiç rezervasyonunuz bulunmuyor."));
        }
    }

    private void handleCancelReservation() {
        Reservation selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen iptal etmek istediğiniz rezervasyonu seçiniz.");
            return;
        }

        // Onay Penceresi
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("İptal Onayı");
        alert.setHeaderText("Rezervasyon İptali: " + selected.getReservationCode());
        alert.setContentText("Bu rezervasyonu iptal etmek istediğinize emin misiniz?\n(Uçuşa 24 saatten az kaldıysa iptal edilemez)");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Manager üzerinden iptal isteği gönder
            boolean success = reservationManager.cancelReservation(selected.getReservationCode());

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Rezervasyon başarıyla iptal edildi.");
                refreshTableData(); // Tabloyu güncelle
            } else {
                showAlert(Alert.AlertType.ERROR, "Hata", "İptal işlemi başarısız.\nUçuşa 24 saatten az kalmış olabilir veya sistem hatası.");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
