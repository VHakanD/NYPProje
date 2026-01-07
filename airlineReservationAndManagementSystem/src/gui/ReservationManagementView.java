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
    
    private TableView<Reservation> tableMyTickets;
    private TableView<Reservation> tableOthersTickets;

    public ReservationManagementView(MainApp mainApp, ReservationManager reservationManager, Passenger loggedInPassenger) {
        this.mainApp = mainApp;
        this.reservationManager = reservationManager;
        this.loggedInPassenger = loggedInPassenger;
    }

    public Parent getView() {
    	BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));

        // --- ÜST BAŞLIK ---
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Label lblTitle = new Label("Rezervasyon Yönetimi");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnBack = new Button("← Uçuş Aramaya Dön");
        btnBack.setOnAction(e -> mainApp.showUserSearchScreen(loggedInPassenger));
        topBar.getChildren().addAll(lblTitle, spacer, btnBack);
        layout.setTop(topBar);

        // --- ORTA KISIM (VBox içinde iki tablo) ---
        VBox centerContent = new VBox(15);
        centerContent.setPadding(new Insets(10, 0, 10, 0));

        // 1. Tablo: Kendi Biletlerim
        Label lblMine = new Label("Kendi Adınıza Olan Biletler");
        lblMine.setStyle("-fx-font-weight: bold; -fx-text-fill: #2980b9; -fx-font-size: 14px;");
        tableMyTickets = new TableView<>();
        tableMyTickets.setPrefHeight(200);
        createTableColumns(tableMyTickets);

        // 2. Tablo: Başkasına Aldıklarım
        Label lblOthers = new Label("Sizin Adınıza Olmayan / Başkasına Aldığınız Biletler");
        lblOthers.setStyle("-fx-font-weight: bold; -fx-text-fill: #e67e22; -fx-font-size: 14px;");
        tableOthersTickets = new TableView<>();
        tableOthersTickets.setPrefHeight(200);
        createTableColumns(tableOthersTickets);

        centerContent.getChildren().addAll(lblMine, tableMyTickets, new Separator(), lblOthers, tableOthersTickets);
        layout.setCenter(centerContent);

        // --- ALT KISIM ---
        HBox bottomBar = new HBox(15);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnChangeSeat = new Button("Koltuk Değiştir");
        btnChangeSeat.setStyle("-fx-base: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
        btnChangeSeat.setPrefHeight(40);
        btnChangeSeat.setOnAction(e -> handleChangeSeatAction());
        
        Button btnCancel = new Button("Seçili Rezervasyonu İptal Et");
        btnCancel.setStyle("-fx-base: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnCancel.setPrefHeight(40);
        btnCancel.setOnAction(e -> handleCancelReservation());

        bottomBar.getChildren().addAll(btnChangeSeat, btnCancel);
        layout.setBottom(bottomBar);
        
        // Verileri Yükle
        refreshTables();

        return layout;
    }

    private void createTableColumns(TableView<Reservation> table) {
    	TableColumn<Reservation, String> colPNR = new TableColumn<>("PNR");
        colPNR.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReservationCode()));

        // Yolcu Adını da gösterelim
        TableColumn<Reservation, String> colPass = new TableColumn<>("Yolcu");
        colPass.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getPassenger().getName() + " " + c.getValue().getPassenger().getSurname()
        ));

        TableColumn<Reservation, String> colFlight = new TableColumn<>("Uçuş");
        colFlight.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFlight().getFlightNum()));
        
        TableColumn<Reservation, String> colRoute = new TableColumn<>("Rota");
        colRoute.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFlight().getRoute().toString()));

        TableColumn<Reservation, String> colDate = new TableColumn<>("Tarih");
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFlight().getFormattedDate()));
        
        TableColumn<Reservation, String> colSeat = new TableColumn<>("Koltuk");
        colSeat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSeat().getSeatNum()));

        table.getColumns().addAll(colPNR, colPass, colFlight, colRoute, colDate, colSeat);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void refreshTables() {
    	// Giriş yapan kişinin (Booker) yaptığı tüm rezervasyonları al
        ArrayList<Reservation> allMyBookings = reservationManager.getReservationsByBooker(loggedInPassenger.getPassengerID());

        ArrayList<Reservation> listMine = new ArrayList<>();
        ArrayList<Reservation> listOthers = new ArrayList<>();

        // Listeyi ayıkla
        for (Reservation r : allMyBookings) {
            // Eğer uçan kişi ID == Giriş yapan ID ise "Benim"dir.
            if (r.getPassenger().getPassengerID().equals(loggedInPassenger.getPassengerID())) {
                listMine.add(r);
            } else {
                listOthers.add(r);
            }
        }

        if (tableMyTickets != null) {
            tableMyTickets.setItems(FXCollections.observableArrayList(listMine));
            tableMyTickets.refresh(); // <-- EKLENDİ: Görseli zorla yenile
        }
        if (tableOthersTickets != null) {
            tableOthersTickets.setItems(FXCollections.observableArrayList(listOthers));
            tableOthersTickets.refresh(); // <-- EKLENDİ: Görseli zorla yenile
        }
    }

    private void handleCancelReservation() {
    	// Hangi tablodan seçim yapıldığını bul
        Reservation selected = tableMyTickets.getSelectionModel().getSelectedItem();
        if (selected == null) {
            selected = tableOthersTickets.getSelectionModel().getSelectedItem();
        }

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen iptal etmek için bir rezervasyon seçiniz.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("İptal Onayı");
        alert.setHeaderText("Rezervasyon İptali: " + selected.getReservationCode());
        alert.setContentText("Yolcu: " + selected.getPassenger().getName() + "\nBu işlemi onaylıyor musunuz?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = reservationManager.cancelReservation(selected.getReservationCode());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Rezervasyon iptal edildi.");
                refreshTables();
            } else {
                showAlert(Alert.AlertType.ERROR, "Hata", "İptal başarısız (Uçuşa 24 saatten az kalmış olabilir).");
            }
        }
    }
    
    private void handleChangeSeatAction() {
        Reservation selected = tableMyTickets.getSelectionModel().getSelectedItem();
        if (selected == null) {
            selected = tableOthersTickets.getSelectionModel().getSelectedItem();
        }

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen koltuk değiştirmek istediğiniz rezervasyonu seçiniz.");
            return;
        }
        
        // GÜNCELLEME: loggedInPassenger'ı da gönderiyoruz
        mainApp.showSeatChangeScreen(selected, loggedInPassenger);
        
        refreshTables();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
