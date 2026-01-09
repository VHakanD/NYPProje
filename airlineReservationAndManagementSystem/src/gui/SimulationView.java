package gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import reservationAndTicketing.Passenger;
import servicesAndManagers.ReservationManager;
import servicesAndManagers.SeatManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import flightManagement.Flight;
import flightManagement.Plane;
import flightManagement.Route;
import flightManagement.Seat;
import flightManagement.Staff;

public class SimulationView {
	private MainApp mainApp;
	private ReservationManager reservationManager;
	private Staff adminStaff;
    
    private final int TOTAL_SEATS = 180;
    private final int PASSENGER_COUNT = 90;
    private final String SIM_FLIGHT_ID = "SIM_TEST_001";
    
    private Flight simFlight;
    private List<String> seatNumbers;
    
    private Rectangle[] seatRects = new Rectangle[TOTAL_SEATS];
    private Label lblResult;
    private CheckBox chkSync;
    private Button btnStart;
    private Button btnReset;
    

    public SimulationView(MainApp mainApp, ReservationManager reservationManager, Staff adminStaff) {
        this.mainApp = mainApp;
        this.reservationManager = reservationManager;
        this.adminStaff = adminStaff; 
        prepareSimulationEnvironment(); 
    }

    public Parent getView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(15));

        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);
        
        Label lblTitle = new Label("Multithreading Testi");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        Label lblInfo = new Label("Bu ekran ReservationManager üzerindeki senkronize/senkronize olmayan metotları test eder.");
        
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER);
        
        chkSync = new CheckBox("Synchronized");
        chkSync.setSelected(true);
        
        btnStart = new Button("Başlat");
        btnStart.setStyle("-fx-base: #3498db; -fx-text-fill: white;");
        btnStart.setOnAction(e -> startSimulation());
        
        btnReset = new Button("Sıfırla");
        btnReset.setStyle("-fx-base: #e67e22; -fx-text-fill: white;");
        btnReset.setOnAction(e -> resetSimulation());
        
        Button btnBack = new Button("Geri Dön");
        btnBack.setOnAction(e -> mainApp.showAdminDashboard(adminStaff));
        
        controls.getChildren().addAll(chkSync, btnStart, btnReset, btnBack);
        topBox.getChildren().addAll(lblTitle, lblInfo, controls);
        layout.setTop(topBox);

        GridPane seatGrid = new GridPane();
        seatGrid.setAlignment(Pos.CENTER);
        seatGrid.setHgap(3);
        seatGrid.setVgap(3);
        
        char[] cols = {'A', 'B', 'C', 'D', 'E', 'F'};
        
        
        for (int i = 0; i < cols.length; i++) {
            Label lblCol = new Label(String.valueOf(cols[i]));
            lblCol.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            lblCol.setMinWidth(15);
            lblCol.setAlignment(Pos.CENTER);
            lblCol.setStyle("-fx-text-fill: black;");
            
            int gridColIndex = i + 1; 
            if (i >= 3) gridColIndex++;
            
            seatGrid.add(lblCol, gridColIndex, 0);
        }
        
        Label lblAisle = new Label("|");
        lblAisle.setStyle("-fx-text-fill: #ccc;");
        seatGrid.add(lblAisle, 4, 0);

        seatRects = new Rectangle[seatNumbers.size()];
        
        for (int i = 0; i < seatNumbers.size(); i++) {
            String seatNum = seatNumbers.get(i);
            
            String rowPart = seatNum.substring(0, seatNum.length() - 1);
            char colChar = seatNum.charAt(seatNum.length() - 1);
            
            int rowNum = Integer.parseInt(rowPart);
            
            Label lblRow = new Label(String.valueOf(rowNum));
            lblRow.setFont(Font.font("Arial", 10));
            lblRow.setMinWidth(20);
            lblRow.setAlignment(Pos.CENTER_RIGHT);
            lblRow.setStyle("-fx-text-fill: black;");
            if (colChar == 'A') {
                seatGrid.add(lblRow, 0, rowNum);
            }

            Rectangle rect = new Rectangle(14, 14);
            rect.setFill(Color.LIGHTGREEN);
            rect.setStroke(Color.DARKGRAY);
            rect.setStrokeWidth(0.5);
            
            seatRects[i] = rect; 
            
            int colIndex = -1;
            switch(colChar) {
                case 'A': colIndex = 1; break;
                case 'B': colIndex = 2; break;
                case 'C': colIndex = 3; break;
                case 'D': colIndex = 5; break;
                case 'E': colIndex = 6; break;
                case 'F': colIndex = 7; break;
            }
            
            if (colIndex != -1) {
                seatGrid.add(rect, colIndex, rowNum);
            }
        }
        
        ScrollPane scroll = new ScrollPane(seatGrid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;"); 
        scroll.setPadding(new Insets(5));
        layout.setCenter(scroll);

        lblResult = new Label("Durum: Hazır - " + seatNumbers.size() + " koltuk yüklendi.");
        lblResult.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        HBox bottomBox = new HBox(lblResult);
        bottomBox.setAlignment(Pos.CENTER);
        layout.setBottom(bottomBox);

        resetSimulation();

        return layout;
    }
    
    private void prepareSimulationEnvironment() {
    	Plane plane = new Plane("SIM_PLANE", "Boeing 737-800", TOTAL_SEATS);
        new SeatManager().seatingArrangements(plane);
        
        seatNumbers = new ArrayList<>(plane.getSeatMatrix().keySet());
        
        Route route = new Route("SimCity", "TestLand", 1000);
        simFlight = new Flight(SIM_FLIGHT_ID, route, LocalDateTime.now().plusDays(5), 120);
        simFlight.setPlane(plane);
    }

    private void startSimulation() {
        resetSimulation();
        
        btnStart.setDisable(true);
        btnReset.setDisable(true);
        boolean isSafeMode = chkSync.isSelected();
        
        lblResult.setText("Simülasyon Çalışıyor... Mod: " + (isSafeMode ? "GÜVENLİ" : "GÜVENSİZ"));
        lblResult.setStyle("-fx-text-fill: blue;");

        ExecutorService executor = Executors.newFixedThreadPool(PASSENGER_COUNT);

        for (int i = 0; i < PASSENGER_COUNT; i++) {
            final int passengerIndex = i;
            executor.execute(() -> {
                boolean success = false;
                while (!success) {
                    try {
                        Random random = new Random();
                        int randomIndex = random.nextInt(seatNumbers.size());
                        String targetSeatNum = seatNumbers.get(randomIndex);
                        
                        Passenger simPassenger = new Passenger("SIM_" + passengerIndex, "Sim", "User", "5550000");

                        if (isSafeMode) {
                            success = reservationManager.makeReservation(
                                simFlight.getPlane(), 
                                simFlight, 
                                simPassenger, 
                                targetSeatNum, 
                                "SIM_ADMIN"
                            );
                        } else {
                            success = reservationManager.makeReservationUnsafe(
                                simFlight, 
                                simPassenger, 
                                targetSeatNum
                            );
                        }

                        if (success) {
                            Platform.runLater(() -> seatRects[randomIndex].setFill(Color.RED));
                        }
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        executor.shutdown();
        
        new Thread(() -> {
            try {
                Thread.sleep(2000); 
            } catch (InterruptedException e) { }

            long actualBookings = 0;
            Map<String, Seat> matrix = simFlight.getPlane().getSeatMatrix();
            for(Seat s : matrix.values()) {
                if(s.isReserved()) actualBookings++;
            }

            long finalCount = actualBookings;
            Platform.runLater(() -> {
                lblResult.setText("Toplam Rezervasyon: " + finalCount + " / 90 Hedeflenen");
                
                if (finalCount < 90) {
                    lblResult.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    lblResult.setText(lblResult.getText() + " (ÇAKIŞMA / BAŞARISIZLIK)");
                } else {
                    lblResult.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    lblResult.setText(lblResult.getText() + " (BAŞARILI)");
                }
                reservationManager.cancelReservationsByFlightID(SIM_FLIGHT_ID);
                mainApp.cleanUpSimulationPassengers();
                
                System.out.println("Simülasyon verileri (Rezervasyonlar + Yolcular) temizlendi.");
                
                btnStart.setDisable(false);
                btnReset.setDisable(false);
            });
        }).start();
    }

    private void resetSimulation() {
        reservationManager.cancelReservationsByFlightID(SIM_FLIGHT_ID);
        
        for(Seat s : simFlight.getPlane().getSeatMatrix().values()) {
            s.setReserveStatus(false);
        }
        
        if(seatRects != null) {
            for (Rectangle rect : seatRects) {
                rect.setFill(Color.LIGHTGREEN);
            }
        }
        
        lblResult.setText("Durum: Hazır (Veritabanı ve Arayüz Sıfırlandı)");
        lblResult.setStyle("-fx-text-fill: black;");
    }
}
