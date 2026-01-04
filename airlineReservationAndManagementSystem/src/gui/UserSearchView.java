package gui;

import flightManagement.Flight;
import servicesAndManagers.FlightManager;
import servicesAndManagers.ReservationManager;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserSearchView {
	private MainApp mainApp;
    private FlightManager flightManager;
    private ReservationManager reservationManager;
    private TableView<Flight> table;

    public UserSearchView(MainApp mainApp, FlightManager flightManager, ReservationManager reservationManager) {
        this.mainApp = mainApp;
        this.flightManager = flightManager;
        this.reservationManager = reservationManager;
    }

    public Parent getView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));

        // --- FİLTRELEME ALANI ---
        TextField txtFrom = new TextField(); txtFrom.setPromptText("Nereden");
        Button btnSearch = new Button("Uçuş Ara");
        
        btnSearch.setOnAction(e -> {
            // Basit filtreleme (Backend'deki flightsByDepartureCity metodunu kullanıyoruz)
            table.setItems(FXCollections.observableArrayList(
                flightManager.flightsByDepartureCity(txtFrom.getText())
            ));
        });
        
        Button btnShowAll = new Button("Tümünü Göster");
        btnShowAll.setOnAction(e -> table.setItems(FXCollections.observableArrayList(flightManager.getFlights())));

        HBox topBar = new HBox(10, txtFrom, btnSearch, btnShowAll);
        layout.setTop(topBar);

        // --- TABLO ---
        table = new TableView<>();
        // (Kolon tanımları AdminView ile aynı mantıkta yapılabilir, kodu kısaltmak için tekrarlamıyorum)
        // Burada basitçe uçuş numarasını gösterelim örnek olarak:
        TableColumn<Flight, String> colNum = new TableColumn<>("Uçuş No");
        colNum.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("flightNum"));
        table.getColumns().add(colNum);
        
        table.setItems(FXCollections.observableArrayList(flightManager.getFlights()));
        layout.setCenter(table);

        // --- SEÇİM VE İLERLEME ---
        Button btnSelectSeat = new Button("Koltuk Seç ve İlerle");
        btnSelectSeat.setStyle("-fx-font-size: 14px; -fx-base: #b6e7c9;");
        
        btnSelectSeat.setOnAction(e -> {
            Flight selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // Seçilen uçuşu alıp Koltuk Ekranını açıyoruz (Popup veya Yeni Sahne)
                openSeatSelection(selected);
            } else {
                new Alert(Alert.AlertType.WARNING, "Lütfen bir uçuş seçiniz!").show();
            }
        });

        layout.setBottom(new HBox(btnSelectSeat));
        return layout;
    }

    private void openSeatSelection(Flight flight) {
        // Koltuk Seçim Ekranını Başlat
        SeatSelectionView seatView = new SeatSelectionView(mainApp, flight, reservationManager);
        Stage stage = new Stage();
        stage.setTitle("Koltuk Seçimi: " + flight.getFlightNum());
        stage.setScene(new Scene(seatView.getView(), 800, 600));
        stage.show();
    }

}
