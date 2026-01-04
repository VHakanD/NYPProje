package gui;

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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class UserSearchView {
	private MainApp mainApp;
    private FlightManager flightManager;
    private ReservationManager reservationManager;
    private TableView<Flight> table;
    private String passengerName;

    public UserSearchView(MainApp mainApp, FlightManager flightManager, ReservationManager reservationManager, String passengerName) {
        this.mainApp = mainApp;
        this.flightManager = flightManager;
        this.reservationManager = reservationManager;
        this.passengerName = passengerName;
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
        
        Label lblWelcome = new Label("Hoşgeldiniz, Sayın " + passengerName);
        lblWelcome.setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold; -fx-font-size: 14px;");
        
        Button btnLogout = new Button("Çıkış");
        btnLogout.setStyle("-fx-font-size: 11px;");
        btnLogout.setOnAction(e -> mainApp.showLoginScreen());

        headerBox.getChildren().addAll(lblTitle, spacer, lblWelcome, new Label("  "), btnLogout);
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
        
        btnSearch.setOnAction(e -> {
            table.setItems(FXCollections.observableArrayList(
                flightManager.flightsByDepartureCity(txtFrom.getText())
            ));
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
        TableColumn<Flight, String> colTime = new TableColumn<>("Saat");
        colTime.setCellValueFactory(new PropertyValueFactory<>("hour"));

        // Süre
        TableColumn<Flight, Integer> colDur = new TableColumn<>("Süre (Dk)");
        colDur.setCellValueFactory(new PropertyValueFactory<>("duration"));
        
        // Mesafe
        TableColumn<Flight, Double> colDist = new TableColumn<>("Mesafe (KM)");
        colDist.setCellValueFactory(cell -> 
             new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getRoute().getDistanceKm()));

        table.getColumns().addAll(colNum, colDep, colArr, colDate, colTime, colDur, colDist);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Sütunları ekrana yay
    }

    private void handleSearch(String from, String to) {
        if (flightManager.getFlights() == null) return;

        // Java Streams ile filtreleme (Hem Kalkış Hem Varış'a göre)
        ObservableList<Flight> filteredList = FXCollections.observableArrayList(
            flightManager.getFlights().stream()
                .filter(f -> {
                    String dep = f.getRoute().getDepartureCity().toLowerCase();
                    String arr = f.getRoute().getArrivalCity().toLowerCase();
                    String searchFrom = from.toLowerCase().trim();
                    String searchTo = to.toLowerCase().trim();
                    
                    // Arama kutusu boşsa o kriteri geç (true), doluysa eşleşme ara
                    boolean matchFrom = searchFrom.isEmpty() || dep.contains(searchFrom);
                    boolean matchTo = searchTo.isEmpty() || arr.contains(searchTo);
                    
                    return matchFrom && matchTo;
                })
                .collect(Collectors.toList())
        );
        
        table.setItems(filteredList);
    }

    private void openSeatSelection(Flight flight) {
        SeatSelectionView seatView = new SeatSelectionView(mainApp, flight, reservationManager);
        Stage stage = new Stage();
        stage.setTitle("Koltuk Seçimi: " + flight.getFlightNum());
        stage.setScene(new Scene(seatView.getView(), 900, 650));
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
