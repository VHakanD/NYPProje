package gui;

import java.util.stream.Collectors;

import flightManagement.Flight;
import flightManagement.Seat;
import servicesAndManagers.CalculatePrice;
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
import reservationAndTicketing.Reservation;

import java.util.Locale;

public class UserSearchView {
    private MainApp mainApp;
    private FlightManager flightManager;
    private ReservationManager reservationManager;
    private TableView<Flight> table;
    private Passenger loggedInPassenger;
    
    private TextField txtFrom;
    private TextField txtTo;

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
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
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

        txtFrom = new TextField(); 
        txtFrom.setPromptText("Kalkış Şehri");
        txtFrom.setPrefWidth(200);

        txtTo = new TextField(); 
        txtTo.setPromptText("Varış Şehri");
        txtTo.setPrefWidth(200);
        
        
        txtFrom.textProperty().addListener((observable, oldValue, newValue) -> filterFlights());
        txtTo.textProperty().addListener((observable, oldValue, newValue) -> filterFlights());

        Button btnSearch = new Button("Uçuş Ara");
        btnSearch.setStyle("-fx-base: #3498db; -fx-text-fill: white;");
        btnSearch.setOnAction(e -> filterFlights());
        
        Button btnShowAll = new Button("Tüm Uçuşları Listele");
        
        btnShowAll.setOnAction(e -> {
            txtFrom.clear();
            txtTo.clear();
            if(flightManager.getFlights() != null) {
                table.setItems(FXCollections.observableArrayList(flightManager.getFlights()));
            }
        });

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
        TableColumn<Flight, String> colNum = new TableColumn<>("Uçuş No");
        colNum.setCellValueFactory(new PropertyValueFactory<>("flightNum"));

        TableColumn<Flight, String> colDep = new TableColumn<>("Kalkış");
        colDep.setCellValueFactory(cell -> 
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getRoute().getDepartureCity()));

        TableColumn<Flight, String> colArr = new TableColumn<>("Varış");
        colArr.setCellValueFactory(cell -> 
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getRoute().getArrivalCity()));

        TableColumn<Flight, String> colDate = new TableColumn<>("Tarih");
        colDate.setCellValueFactory(new PropertyValueFactory<>("formattedDate")); 

        TableColumn<Flight, String> colTime = new TableColumn<>("Kalkış Saati");
        colTime.setCellValueFactory(new PropertyValueFactory<>("hour"));

        TableColumn<Flight, String> colDur = new TableColumn<>("Uçuş Süresi");
        colDur.setCellValueFactory(cell -> {
            int rawMinutes = cell.getValue().getDuration();
            int hours = rawMinutes / 60;
            int minutes = rawMinutes % 60;
            String formatted = String.format("%d sa %d dk", hours, minutes);
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        
        TableColumn<Flight, Double> colDist = new TableColumn<>("Mesafe (KM)");
        colDist.setCellValueFactory(cell -> 
             new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getRoute().getDistanceKm()));

        TableColumn<Flight, String> colPrice = new TableColumn<>("Eco - Bus Fiyatları");
        
        colPrice.setCellValueFactory(cell -> {
            Flight f = cell.getValue();
            if (f.getRoute() != null) {
                CalculatePrice calculator = new CalculatePrice();
                
                Passenger dummyPassenger = new Passenger("TEMP_ID", "Fiyat", "Hesaplayici", "000");
                
                Seat dummyEcoSeat = new Seat("XX", Seat.SeatType.ECONOMY);
                Seat dummyBusSeat = new Seat("XX", Seat.SeatType.BUSINESS);
                
                Reservation dummyResEco = new Reservation("TEMP", f, dummyPassenger, dummyEcoSeat);
                Reservation dummyResBus = new Reservation("TEMP", f, dummyPassenger, dummyBusSeat);
                
                double realEcoPrice = calculator.calculateTicketPrice(dummyResEco);
                double realBusPrice = calculator.calculateTicketPrice(dummyResBus);
                
                String priceText = String.format("%.0f ₺ - %.0f ₺", realEcoPrice, realBusPrice);
                return new javafx.beans.property.SimpleStringProperty(priceText);
            } else {
                return new javafx.beans.property.SimpleStringProperty("-");
            }
        });
        
        table.getColumns().add(colNum);
        table.getColumns().add(colDep);
        table.getColumns().add(colArr);
        table.getColumns().add(colDate);
        table.getColumns().add(colTime);
        table.getColumns().add(colDur);
        table.getColumns().add(colDist);
        table.getColumns().add(colPrice);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private void filterFlights() {
        if (flightManager.getFlights() == null) return;

        Locale trLocale = Locale.of("tr", "TR");
        String searchFrom = txtFrom.getText().toLowerCase(trLocale);
        String searchTo = txtTo.getText().toLowerCase(trLocale);

        ObservableList<Flight> filteredList = FXCollections.observableArrayList(
            flightManager.getFlights().stream()
                .filter(flight -> {
                	String depCity = flight.getRoute().getDepartureCity().toLowerCase(trLocale);
                    String arrCity = flight.getRoute().getArrivalCity().toLowerCase(trLocale);
                    
                    boolean matchFrom = searchFrom.isEmpty() || depCity.contains(searchFrom);
                    boolean matchTo = searchTo.isEmpty() || arrCity.contains(searchTo);
                    
                    return matchFrom && matchTo;
                })
                .collect(Collectors.toList())
        );
        
        table.setItems(filteredList);
    }

    private void openSeatSelection(Flight flight) {
        SeatSelectionView seatView = new SeatSelectionView(mainApp, flight, reservationManager, loggedInPassenger);
        Stage stage = new Stage();
        stage.setTitle("Koltuk Seçimi: " + flight.getFlightNum());
        stage.setScene(new Scene(seatView.getView(), 900, 650));
        
        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        stage.initOwner(mainApp.getPrimaryStage());
        
        stage.show();
    }
    
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}