package gui;

import flightManagement.Flight;
import flightManagement.Staff;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import servicesAndManagers.FlightManager;
import servicesAndManagers.ReservationManager;

public class AdminDashboardView {
	private MainApp mainApp;
	private Staff adminStaff;
    private FlightManager flightManager;
    private ReservationManager reservationManager;
    

    public AdminDashboardView(MainApp mainApp, Staff adminStaff, FlightManager fm, ReservationManager rm) {
        this.mainApp = mainApp;
        this.adminStaff = adminStaff;
        this.flightManager = fm;
        this.reservationManager = rm;
    }

    public Parent getView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.CENTER);
        
        
        Label lblWelcome = new Label("Hoşgeldiniz, " + adminStaff.getName().toUpperCase() + " " + adminStaff.getSurname().toUpperCase());
        lblWelcome.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        Label lblInstruction = new Label("Lütfen yapmak istediğiniz işlemi seçiniz:");
        
        
        Button btnFlights = new Button("Uçuş İşlemleri");
        btnFlights.setPrefWidth(200);
        btnFlights.setPrefHeight(50);
        btnFlights.setStyle("-fx-font-size: 16px; -fx-base: #4a90e2;");
        
        Button btnStaff = new Button("Personel İşlemleri");
        btnStaff.setPrefWidth(200);
        btnStaff.setPrefHeight(50);
        btnStaff.setStyle("-fx-font-size: 16px; -fx-base: #50c878;");
        
        Button btnSimulation = new Button("Thread Simülasyonu (Concurrency)");
        btnSimulation.setPrefWidth(250);
        btnSimulation.setStyle("-fx-font-size: 14px; -fx-base: #9b59b6; -fx-text-fill: black;");
        btnSimulation.setOnAction(e -> mainApp.showSimulationScreen(adminStaff));

        
        Button btnReport = new Button("Doluluk Raporu Oluştur (Asenkron)");
        btnReport.setPrefWidth(250);
        btnReport.setStyle("-fx-font-size: 14px; -fx-base: #f39c12; -fx-text-fill: black;");
        
        Label lblStatus = new Label("");
        lblStatus.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");

        btnReport.setOnAction(e -> startAsynchronousReport(lblStatus));
        
        Button btnLogout = new Button("Çıkış Yap (Logout)");
        btnLogout.setPrefWidth(200);
        btnLogout.setStyle("-fx-base: #e74c3c;");
        
        
        btnFlights.setOnAction(e -> mainApp.showFlightScreen(adminStaff));
        btnStaff.setOnAction(e -> mainApp.showStaffScreen(adminStaff));
        btnLogout.setOnAction(e -> mainApp.showLoginScreen());
        
        layout.getChildren().addAll(lblWelcome, lblInstruction, btnFlights, btnStaff, btnSimulation, btnReport, lblStatus, btnLogout);
        
        return layout;
    }
    
    private void startAsynchronousReport(Label statusLabel) {
        Task<String> reportTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                updateMessage("Rapor Hazırlanıyor...");
                
                StringBuilder report = new StringBuilder();
                report.append("--- UÇUŞ DOLULUK RAPORU ---\n");
                int count = 0;
                int totalFlights = flightManager.getFlights().size();
                
                for (Flight f : flightManager.getFlights()) {
                    Thread.sleep(500); 
                    
                    double rate = reservationManager.calculateOccupancyRate(f);
                    report.append(String.format("Uçuş: %s | Doluluk: %%%.2f\n", f.getFlightNum(), rate));
                    
                    count++;
                    updateMessage("Rapor Hazırlanıyor... (" + count + "/" + totalFlights + ")");
                }
                
                return report.toString();
            }
        };

        statusLabel.textProperty().bind(reportTask.messageProperty());

        
        reportTask.setOnSucceeded(e -> {
            statusLabel.textProperty().unbind();
            statusLabel.setText("Rapor Tamamlandı!");
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Rapor Sonucu");
            alert.setHeaderText("Doluluk Oranları Hesaplanmıştır");
            TextArea area = new TextArea(reportTask.getValue());
            area.setEditable(false);
            area.setWrapText(true);
            alert.getDialogPane().setContent(area);
            alert.showAndWait();
        });

        
        new Thread(reportTask).start();
    }

}
