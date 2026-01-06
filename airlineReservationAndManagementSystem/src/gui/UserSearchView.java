package gui;

import java.util.stream.Collectors;

import flightManagement.Flight;
import servicesAndManagers.FlightManager;
import servicesAndManagers.ReservationManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import reservationAndTicketing.Passenger;

import java.util.Locale;

public class UserSearchView {
	private MainApp mainApp;
    private FlightManager flightManager;
    private ReservationManager reservationManager;
    private TableView<Flight> table;
    private Passenger loggedInPassenger;

    public UserSearchView(MainApp mainApp, FlightManager flightManager, ReservationManager reservationManager, Passenger loggedInPassenger) {
        this.mainApp = mainApp;
        this.flightManager = flightManager;
        this.reservationManager = reservationManager;
        this.loggedInPassenger = loggedInPassenger;
    }

    public Parent getView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));
        
        VBox topContainer = new VBox(15);
        
        HBox headerBox = new HBox();
        Label lblTitle = new Label("Uçuş Arama");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Araya boşluk at
        
        Label lblWelcome = new Label("Hoşgeldiniz, Sayın " + loggedInPassenger.getName() + " " + loggedInPassenger.getSurname());
        lblWelcome.setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold; -fx-font-size: 14px;");
        
        Button btnMyReservations = new Button("Rezervasyonlarım");
        btnMyReservations.setStyle("-fx-base: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");
        btnMyReservations.setOnAction(e -> mainApp.showReservationManagementScreen(loggedInPassenger));
        
        Button btnLogout = new Button("Çıkış");
        btnLogout.setStyle("-fx-base: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
        btnLogout.setOnAction(e -> mainApp.showLoginScreen());

        headerBox.getChildren().addAll(lblTitle, spacer, lblWelcome, new Label("  "), btnMyReservations, new Label(" "), btnLogout);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        
        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(10));
        searchBox.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 5;");
        searchBox.setAlignment(Pos.CENTER_LEFT);

        TextField txtFrom = new TextField(); 
        txtFrom.setPromptText("Nereden (Kalkış Şehri)");
        txtFrom.setPrefWidth(200);

        TextField txtTo = new TextField(); 
        txtTo.setPromptText("Nereye (Varış Şehri)");
        txtTo.setPrefWidth(200);

        Button btnSearch = new Button("Uçuş Ara");
        btnSearch.setStyle("-fx-base: #3498db; -fx-text-fill: white;");
        
        Button btnShowAll = new Button("Tüm Uçuşları Listele");
        
        btnSearch.setOnAction(e -> handleSearch(txtFrom.getText(), txtTo.getText()));
        
        btnShowAll.setOnAction(e -> {
            txtFrom.clear();
            txtTo.clear();
            // Tüm listeyi geri yükle
            if(flightManager.getFlights() != null) {
                table.setItems(FXCollections.observableArrayList(flightManager.getFlights()));
            }
        });
        
        btnShowAll.setOnAction(e -> table.setItems(FXCollections.observableArrayList(flightManager.getFlights())));

        searchBox.getChildren().addAll(new Label("Rota Ara:"), txtFrom, new Label("->"), txtTo, btnSearch, btnShowAll);
        
        topContainer.getChildren().addAll(headerBox, searchBox);
        layout.setTop(topContainer);

       
        table = new TableView<>();
        createTableColumns();
        
        if(flightManager.getFlights() != null) {
            table.setItems(FXCollections.observableArrayList(flightManager.getFlights()));
        }
        
        layout.setCenter(table);
        
        Button btnSelectSeat = new Button("Seçili Uçuşta Koltuk Seç");
        btnSelectSeat.setStyle("-fx-font-size: 14px; -fx-base: #27ae60; -fx-text-fill: white;");
        btnSelectSeat.setPadding(new Insets(10, 20, 10, 20));
        
        btnSelectSeat.setOnAction(e -> {
            Flight selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                openSeatSelection(selected);
            } else {
                showAlert("Uyarı", "Lütfen listeden bir uçuş seçiniz!");
            }
        });

        HBox bottomBox = new HBox(btnSelectSeat);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        bottomBox.setPadding(new Insets(15, 0, 0, 0));
        layout.setBottom(bottomBox);

        return layout;
        
    }
    
    private void createTableColumns() {
        // Uçuş No
        TableColumn<Flight, String> colNum = new TableColumn<>("Uçuş No");
        colNum.setCellValueFactory(new PropertyValueFactory<>("flightNum"));

        // Kalkış Yeri (Route içinden)
        TableColumn<Flight, String> colDep = new TableColumn<>("Kalkış");
        colDep.setCellValueFactory(cell -> 
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getRoute().getDepartureCity()));

        // Varış Yeri
        TableColumn<Flight, String> colArr = new TableColumn<>("Varış");
        colArr.setCellValueFactory(cell -> 
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getRoute().getArrivalCity()));

        // Tarih (Özel formatlı metodunuz varsa onu kullanır, yoksa toString)
        TableColumn<Flight, String> colDate = new TableColumn<>("Tarih");
        colDate.setCellValueFactory(new PropertyValueFactory<>("formattedDate")); 

        // Saat
        TableColumn<Flight, String> colTime = new TableColumn<>("Kalkış Saati");
        colTime.setCellValueFactory(new PropertyValueFactory<>("hour"));

        // Süre
        TableColumn<Flight, String> colDur = new TableColumn<>("Uçuş Süresi");
        colDur.setCellValueFactory(cell -> {
            int rawMinutes = cell.getValue().getDuration(); // Toplam dakika (örn: 150)
            
            int hours = rawMinutes / 60;    // Tam sayı bölmesi (150 / 60 = 2)
            int minutes = rawMinutes % 60;  // Kalanı bulma (150 % 60 = 30)
            
            // Ekranda görünecek format
            // İsterseniz "saat" ve "dakika" kelimelerini uzun da yazabilirsiniz.
            String formatted = String.format("%d sa %d dk", hours, minutes);
            
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        
        // Mesafe
        TableColumn<Flight, Double> colDist = new TableColumn<>("Mesafe (KM)");
        colDist.setCellValueFactory(cell -> 
             new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getRoute().getDistanceKm()));

        table.getColumns().addAll(colNum, colDep, colArr, colDate, colTime, colDur, colDist);
       
        
        TableColumn<Flight, String> colPrice = new TableColumn<>("Tahmini Fiyat (Eco - Bus)");
        
        colPrice.setCellValueFactory(cell -> {
            Flight f = cell.getValue();
            if (f.getRoute() != null) {
                double dist = f.getRoute().getDistanceKm();
                
                // FİYAT POLİTİKASI VARSAYIMI:
                // Sizin CalculatePrice sınıfınızdaki mantığa yakın bir oran kullanıyoruz.
                // Örnek: KM başına 1.5 TL (Ekonomi) ve 3.0 TL (Business) olsun.
                // Bu oranları projenizin gerçek fiyatlandırma mantığına göre değiştirebilirsiniz.
                
                double minPrice = dist * 1.5; 
                double maxPrice = dist * 3.5; 
                
                String priceText = String.format("%.0f ₺ - %.0f ₺", minPrice, maxPrice);
                return new javafx.beans.property.SimpleStringProperty(priceText);
            } else {
                return new javafx.beans.property.SimpleStringProperty("-");
            }
        });
        
        table.getColumns().add(colPrice);
        
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void handleSearch(String from, String to) {
    	if (flightManager.getFlights() == null) return;

        // Türkçe karakter sorunlarını (İ-i, I-ı) en aza indirmek için Locale.ROOT veya ENGLISH kullanabiliriz
        // veya varsayılan locale ile devam edebiliriz.
        String searchFrom = from.trim().toLowerCase(Locale.ROOT);
        String searchTo = to.trim().toLowerCase(Locale.ROOT);

        ObservableList<Flight> filteredList = FXCollections.observableArrayList(
            flightManager.getFlights().stream()
                .filter(flight -> {
                    // Veritabanındaki şehir isimlerini de aynı şekilde küçültüyoruz
                    String depCity = flight.getRoute().getDepartureCity().toLowerCase(Locale.ROOT);
                    String arrCity = flight.getRoute().getArrivalCity().toLowerCase(Locale.ROOT);
                    
                    // Logic:
                    // 1. Arama kutusu boşsa (isEmpty), o kriteri yok say (true dön).
                    // 2. Doluysa, şehir isminin içinde aranan kelime geçiyor mu (contains)?
                    
                    boolean matchFrom = searchFrom.isEmpty() || depCity.contains(searchFrom);
                    boolean matchTo = searchTo.isEmpty() || arrCity.contains(searchTo);
                    
                    // Her iki şart da (veya boşlarsa) sağlanmalı
                    return matchFrom && matchTo;
                })
                .collect(Collectors.toList())
        );
        
        table.setItems(filteredList);
        
        if(filteredList.isEmpty()) {
            showAlert("Bilgi", "Aradığınız kriterlere uygun uçuş bulunamadı.");
        }
    }

    private void openSeatSelection(Flight flight) {
        SeatSelectionView seatView = new SeatSelectionView(mainApp, flight, reservationManager, loggedInPassenger);
        Stage stage = new Stage();
        stage.setTitle("Koltuk Seçimi: " + flight.getFlightNum());
        stage.setScene(new Scene(seatView.getView(), 900, 650));
        
        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        
        // 2. Bu pencerenin sahibini (Owner) ana sahne yap (Hep onun üstünde dursun)
        stage.initOwner(mainApp.getPrimaryStage());
        
        stage.show(); // showAndWait yerine show yaptık ki ana ekran kilitlenmesin
    }
    
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
