package gui;

import flightManagement.Flight;
import flightManagement.Plane;
import flightManagement.Route; // Route sınıfınızın olduğu yer
import servicesAndManagers.FlightManager;
import servicesAndManagers.SeatManager; // Koltukları oluşturmak için lazım
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AdminView {
	
	private MainApp mainApp;
    private FlightManager flightManager;
    private TableView<Flight> table;

    public AdminView(MainApp mainApp, FlightManager flightManager) {
        this.mainApp = mainApp;
        this.flightManager = flightManager;
    }

    public Parent getView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));

        // --- ÜST KISIM: BAŞLIK VE GERİ DÖN ---
        Button btnLogout = new Button("Çıkış Yap");
        btnLogout.setOnAction(e -> mainApp.showLoginScreen());
        HBox topMenu = new HBox(10, new Label("Yönetici Paneli"), btnLogout);
        layout.setTop(topMenu);

        // --- ORTA KISIM: TABLO ---
        table = new TableView<>();
        updateTable(); // Verileri yükle

        TableColumn<Flight, String> colNum = new TableColumn<>("Uçuş No");
        colNum.setCellValueFactory(new PropertyValueFactory<>("flightNum"));

        // Route nesnesinin toString metodunu göstermek için basit bir yaklaşım
        TableColumn<Flight, String> colRoute = new TableColumn<>("Rota");
        colRoute.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRoute().toString()));

        TableColumn<Flight, String> colDate = new TableColumn<>("Tarih");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        table.getColumns().addAll(colNum, colRoute, colDate);
        layout.setCenter(table);

        // --- ALT KISIM: YENİ UÇUŞ EKLEME FORMU ---
        VBox form = new VBox(10);
        form.setPadding(new Insets(10));
        form.setStyle("-fx-border-color: gray; -fx-border-width: 1px;");

        TextField txtNum = new TextField(); txtNum.setPromptText("Uçuş No (Örn: TK101)");
        TextField txtDep = new TextField(); txtDep.setPromptText("Kalkış (İl)");
        TextField txtArr = new TextField(); txtArr.setPromptText("Varış (İl)");
        TextField txtDist = new TextField(); txtDist.setPromptText("Mesafe (KM)");
        TextField txtDur = new TextField(); txtDur.setPromptText("Süre (Dk)");
        TextField txtPlaneId = new TextField(); txtPlaneId.setPromptText("Uçak ID (P01)");
        
        DatePicker datePicker = new DatePicker();
        TextField txtTime = new TextField(); txtTime.setPromptText("Saat (HH:mm)");

        Button btnAdd = new Button("Uçuş Ekle");
        btnAdd.setOnAction(e -> {
            try {
                // 1. Route Oluşturma (Route sınıfınızın constructor'ına göre düzenleyin)
                Route route = new Route(txtDep.getText(), txtArr.getText(), Integer.parseInt(txtDist.getText()));
                
                // 2. Tarih ve Saat Birleştirme
                LocalDateTime ldt = LocalDateTime.of(datePicker.getValue(), LocalTime.parse(txtTime.getText()));
                
                // 3. Flight Oluşturma
                Flight newFlight = new Flight(txtNum.getText(), route, ldt, Integer.parseInt(txtDur.getText()));
                
                // 4. Uçağı ve Koltukları Oluşturma
                Plane newPlane = new Plane(txtPlaneId.getText(), "Boeing 737", 180); // 180 kapasite sabit varsaydım
                
                // SeatManager ile koltukları dolduruyoruz! (Sizin kodunuzdaki logic)
                SeatManager seatMngr = new SeatManager();
                seatMngr.seatingArrangements(newPlane);
                
                newFlight.setPlane(newPlane); // Uçağı uçuşa ata

                // 5. Manager'a Ekle
                flightManager.addFlight(newFlight);
                updateTable();
                
            } catch (Exception ex) {
                showAlert("Hata", "Veri formatı hatalı: " + ex.getMessage());
            }
        });

        form.getChildren().addAll(
            new Label("Yeni Uçuş Ekle:"),
            new HBox(10, txtNum, txtPlaneId),
            new HBox(10, txtDep, txtArr, txtDist),
            new HBox(10, datePicker, txtTime, txtDur),
            btnAdd
        );
        layout.setBottom(form);

        return layout;
    }

    private void updateTable() {
        ObservableList<Flight> data = FXCollections.observableArrayList(flightManager.getFlights());
        table.setItems(data);
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
