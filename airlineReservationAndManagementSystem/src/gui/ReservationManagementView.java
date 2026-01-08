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
    
    private TextField txtSearchRes;
    private ComboBox<String> cmbResSearchType;
    
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
        
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        
        Label lblSearch = new Label("🔍 Ara:");
        
        cmbResSearchType = new ComboBox<>();
        cmbResSearchType.getItems().addAll("Tümü", "PNR Kodu", "Uçuş No", "Yolcu Adı", "Kalkış Yeri");
        cmbResSearchType.setValue("Tümü");
        cmbResSearchType.setPrefWidth(120);
        
        txtSearchRes = new TextField();
        txtSearchRes.setPromptText("Aranacak kelime...");
        txtSearchRes.setMaxWidth(300);
        
        // Listener'lar: Her iki bileşende de değişim olursa filtrele
        txtSearchRes.textProperty().addListener((obs, oldVal, newVal) -> 
            filterReservations(newVal, cmbResSearchType.getValue()));
            
        cmbResSearchType.valueProperty().addListener((obs, oldVal, newVal) -> 
            filterReservations(txtSearchRes.getText(), newVal));
        
        searchBox.getChildren().addAll(lblSearch, cmbResSearchType, txtSearchRes);
        // ------------------------------------------

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
        
        centerContent.getChildren().addAll(searchBox, lblMine, tableMyTickets, new Separator(), lblOthers, tableOthersTickets);
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

    /*private void refreshTables() {
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
    }*/
    
    /*private void refreshTables() {
        // Önce tüm verileri çekiyoruz (filtresiz)
        // Eğer arama kutusunda yazı varsa, filterReservations metodu zaten filtreleyerek ekrana basacak.
        // Eğer kutu boşsa filterReservations hepsini gösterecek.
    	if (txtSearchRes == null) return;
        filterReservations(txtSearchRes.getText());
    }
    
    private void filterReservations(String searchText) {
        ArrayList<Reservation> allMyBookings = reservationManager.getReservationsByBooker(loggedInPassenger.getPassengerID());
        
        ArrayList<Reservation> listMine = new ArrayList<>();
        ArrayList<Reservation> listOthers = new ArrayList<>();
        
        String lowerSearch = (searchText == null) ? "" : searchText.toLowerCase(java.util.Locale.ENGLISH);

        for (Reservation r : allMyBookings) {
            // Null verilerden kaçınmak için güvenli string alma
            String pnr = (r.getReservationCode() != null) ? r.getReservationCode().toLowerCase() : "";
            String flightNum = (r.getFlight() != null && r.getFlight().getFlightNum() != null) ? r.getFlight().getFlightNum().toLowerCase() : "";
            String depCity = (r.getFlight() != null && r.getFlight().getRoute() != null) ? r.getFlight().getRoute().getDepartureCity().toLowerCase() : "";
            String arrCity = (r.getFlight() != null && r.getFlight().getRoute() != null) ? r.getFlight().getRoute().getArrivalCity().toLowerCase() : "";
            String passName = (r.getPassenger() != null) ? r.getPassenger().getName().toLowerCase() : "";

            // Arama Kriterleri
            boolean matches = lowerSearch.isEmpty() ||
                              pnr.contains(lowerSearch) ||
                              flightNum.contains(lowerSearch) ||
                              depCity.contains(lowerSearch) ||
                              arrCity.contains(lowerSearch) ||
                              passName.contains(lowerSearch);
            
            if (matches) {
                if (r.getPassenger().getPassengerID().equals(loggedInPassenger.getPassengerID())) {
                    listMine.add(r);
                } else {
                    listOthers.add(r);
                }
            }
        }

        if (tableMyTickets != null) {
            tableMyTickets.setItems(FXCollections.observableArrayList(listMine));
            tableMyTickets.refresh();
        }
        if (tableOthersTickets != null) {
            tableOthersTickets.setItems(FXCollections.observableArrayList(listOthers));
            tableOthersTickets.refresh();
        }
    }*/
    
    private void refreshTables() {
        if (txtSearchRes == null || cmbResSearchType == null) return;
        // Mevcut metni ve seçim türünü kullanarak filtrele
        filterReservations(txtSearchRes.getText(), cmbResSearchType.getValue());
    }

    // YENİ FİLTRELEME METODU (Switch-Case ile)
    private void filterReservations(String searchText, String searchType) {
        ArrayList<Reservation> allMyBookings = reservationManager.getReservationsByBooker(loggedInPassenger.getPassengerID());
        
        ArrayList<Reservation> listMine = new ArrayList<>();
        ArrayList<Reservation> listOthers = new ArrayList<>();
        
        String lowerSearch = (searchText == null) ? "" : searchText.toLowerCase(java.util.Locale.ENGLISH);

        for (Reservation r : allMyBookings) {
            // Null check
            String pnr = (r.getReservationCode() != null) ? r.getReservationCode().toLowerCase() : "";
            String flightNum = (r.getFlight() != null) ? r.getFlight().getFlightNum().toLowerCase() : "";
            String depCity = (r.getFlight() != null && r.getFlight().getRoute() != null) ? r.getFlight().getRoute().getDepartureCity().toLowerCase() : "";
            String passName = (r.getPassenger() != null) ? r.getPassenger().getName().toLowerCase() + " " + r.getPassenger().getSurname().toLowerCase() : "";

            boolean matches = false;
            
            // Seçilen kritere göre kontrol
            switch (searchType) {
                case "PNR Kodu":
                    matches = pnr.contains(lowerSearch);
                    break;
                case "Uçuş No":
                    matches = flightNum.contains(lowerSearch);
                    break;
                case "Yolcu Adı":
                    matches = passName.contains(lowerSearch);
                    break;
                case "Kalkış Yeri":
                    matches = depCity.contains(lowerSearch);
                    break;
                case "Tümü":
                default:
                    matches = lowerSearch.isEmpty() ||
                              pnr.contains(lowerSearch) ||
                              flightNum.contains(lowerSearch) ||
                              depCity.contains(lowerSearch) ||
                              passName.contains(lowerSearch);
                    break;
            }
            
            if (matches) {
                if (r.getPassenger().getPassengerID().equals(loggedInPassenger.getPassengerID())) {
                    listMine.add(r);
                } else {
                    listOthers.add(r);
                }
            }
        }

        if (tableMyTickets != null) {
            tableMyTickets.setItems(FXCollections.observableArrayList(listMine));
            tableMyTickets.refresh();
        }
        if (tableOthersTickets != null) {
            tableOthersTickets.setItems(FXCollections.observableArrayList(listOthers));
            tableOthersTickets.refresh();
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
