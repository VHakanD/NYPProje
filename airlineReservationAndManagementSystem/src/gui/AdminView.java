package gui;

import flightManagement.*;
import servicesAndManagers.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.*;

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

        
        Button btnLogout = new Button("Çıkış Yap");
        btnLogout.setOnAction(e -> mainApp.showLoginScreen());
        HBox topMenu = new HBox(10, new Label("Yönetici Paneli"), btnLogout);
        layout.setTop(topMenu);

        
        table = new TableView<>();
        updateTable();

        TableColumn<Flight, String> colNum = new TableColumn<>("Uçuş No");
        colNum.setCellValueFactory(new PropertyValueFactory<>("flightNum"));

        
        TableColumn<Flight, String> colRoute = new TableColumn<>("Rota");
        colRoute.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRoute().toString()));

        TableColumn<Flight, String> colDate = new TableColumn<>("Tarih");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        table.getColumns().addAll(colNum, colRoute, colDate);
        layout.setCenter(table);

        
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
                Route route = new Route(txtDep.getText(), txtArr.getText(), Integer.parseInt(txtDist.getText()));
                
                
                LocalDateTime ldt = LocalDateTime.of(datePicker.getValue(), LocalTime.parse(txtTime.getText()));
                
                
                Flight newFlight = new Flight(txtNum.getText(), route, ldt, Integer.parseInt(txtDur.getText()));
                
                
                Plane newPlane = new Plane(txtPlaneId.getText(), "Boeing 737", 180); 
                // 180 sabit varsayıldı, burayı değiştirebiliriz
                
                
                SeatManager seatMngr = new SeatManager();
                seatMngr.seatingArrangements(newPlane);
                
                newFlight.setPlane(newPlane);

                
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
