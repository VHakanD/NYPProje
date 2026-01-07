package gui;

import flightManagement.Flight;
import flightManagement.Plane;
import flightManagement.Seat;
import reservationAndTicketing.Passenger;
import reservationAndTicketing.Reservation;
import servicesAndManagers.CalculatePrice;
import servicesAndManagers.ReservationManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Optional;

public class SeatChangeView {
	private MainApp mainApp;
    private Reservation currentReservation; 
    private Flight flight;
    private ReservationManager reservationManager;
    private Passenger loggedInPassenger;
    private String selectedSeatNum = null;
    private Label lblSelectionInfo;
    private Button btnConfirm;

    public SeatChangeView(MainApp mainApp, Reservation reservation, ReservationManager reservationManager, Passenger loggedInPassenger) {
        this.mainApp = mainApp;
        this.currentReservation = reservation;
        this.flight = reservation.getFlight();
        this.reservationManager = reservationManager;
        this.loggedInPassenger = loggedInPassenger; // Kaydet
    }

    public Parent getView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));

        Label lblHeader = new Label("Koltuk Değişimi: " + flight.getFlightNum());
        lblHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        layout.setTop(lblHeader);

        // --- KOLTUK MATRİSİ ---
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        Plane plane = flight.getPlane();
        Map<String, Seat> seats = plane.getSeatMatrix();

        int totalRows = plane.getCapacity() / 6;
        char[] cols = {'A', 'B', 'C', 'D', 'E', 'F'};
        
        lblSelectionInfo = new Label("Mevcut Koltuğunuz: " + currentReservation.getSeat().getSeatNum());
        lblSelectionInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #2980b9; -fx-font-weight: bold;");

        for (int row = 1; row <= totalRows; row++) {
            for (int c = 0; c < cols.length; c++) {
                char colChar = cols[c];
                String seatNum = row + String.valueOf(colChar);
                Seat seat = seats.get(seatNum);

                if (seat != null) {
                    Button btn = new Button(seatNum);
                    btn.setPrefSize(55, 45); 

                    boolean isBusiness = (seat.getSeatType() == Seat.SeatType.BUSINESS);
                    boolean isTable = isBusiness && (colChar == 'B' || colChar == 'E');
                    boolean isMySeat = seatNum.equals(currentReservation.getSeat().getSeatNum());

                    if (isTable) {
                        btn.setText("SEHPA");
                        btn.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 10px;");
                        btn.setDisable(true); 
                    } 
                    else if (isMySeat) {
                        // Kendi mevcut koltuğumuz (Turuncu)
                        btn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
                        btn.setDisable(true); 
                        btn.setText("BEN");
                    }
                    else if (seat.isReserved()) {
                        // Başkasının koltuğu (Kırmızı)
                        btn.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;"); 
                        btn.setDisable(true);
                    } 
                    else {
                        // Boş Koltuklar (Yeşil/Mor)
                        if (isBusiness) {
                            btn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
                        } else {
                            btn.setStyle("-fx-background-color: #51cf66; -fx-text-fill: white;");
                        }
                        
                        btn.setOnAction(e -> {
                            selectedSeatNum = seatNum;
                            lblSelectionInfo.setText("Yeni Seçilen: " + seatNum);
                            lblSelectionInfo.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 14px;");
                        });
                    }

                    int colIndex = c;
                    if (c >= 3) colIndex++;
                    grid.add(btn, colIndex, row);
                }
            }
        }
        
        grid.add(new Label("KORİDOR"), 3, 0);

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        // Saydam arka plan
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;"); 
        layout.setCenter(scroll);

        // --- ONAY BUTONU ---
        /*btnConfirm = new Button("Koltuk Değişimini Onayla");
        btnConfirm.setStyle("-fx-base: #2980b9; -fx-text-fill: white; -fx-font-weight: bold;");
        btnConfirm.setPrefHeight(40);
        btnConfirm.setOnAction(e -> handleSeatChange());*/
        
        btnConfirm = new Button("Koltuk Değişimini Onayla");
        btnConfirm.setOnAction(e -> handleSeatChange());
        
        VBox bottomContainer = new VBox(10);
        bottomContainer.setAlignment(Pos.CENTER);
        bottomContainer.setPadding(new Insets(15));
        bottomContainer.getChildren().addAll(lblSelectionInfo, btnConfirm);
        layout.setBottom(bottomContainer);

        return layout;
    }
    
    private void handleSeatChange() {
        if (selectedSeatNum == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen yeni bir koltuk seçiniz.");
            return;
        }

        CalculatePrice calculator = new CalculatePrice();
        Seat newSeat = flight.getPlane().getSeatMatrix().get(selectedSeatNum);
        
        // Fiyat Farkı Hesapla
        Reservation tempOldRes = new Reservation("TEMP", flight, currentReservation.getPassenger(), currentReservation.getSeat());
        double oldPrice = calculator.calculateTicketPrice(tempOldRes);
        
        Reservation tempNewRes = new Reservation("TEMP", flight, currentReservation.getPassenger(), newSeat);
        double newPrice = calculator.calculateTicketPrice(tempNewRes);
        
        double priceDifference = newPrice - oldPrice;

        if (priceDifference > 0) {
            // FARK VAR -> ÖDEME EKRANINA GİT (PAYMENT VIEW)
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Ödeme Gerekiyor");
            alert.setHeaderText("Koltuk Sınıf Farkı");
            alert.setContentText(
                "Yeni koltuk için " + String.format("%.2f", priceDifference) + " TL fark ödemeniz gerekmektedir.\n" +
                "Ödeme ekranına yönlendiriliyorsunuz..."
            );

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                //closeWindow(); // Şu anki pencreyi kapat
                
                // PaymentView'i "Koltuk Değişim Modu"nda aç
                // loggedInPassenger hem uçan hem ödeyen olarak gönderildi (Basitlik için)
                // existingReservation parametresi (currentReservation) dolu olduğu için mod değişecek.
                PaymentView paymentView = new PaymentView(
                    mainApp, 
                    flight, 
                    currentReservation.getPassenger(), 
                    loggedInPassenger, 
                    selectedSeatNum, 
                    0, // Ekstra bagaj yok
                    reservationManager, 
                    currentReservation // BU PARAMETRE ÖNEMLİ
                );
                
                Stage paymentStage = new Stage();
                paymentStage.setTitle("Fark Ödemesi");
                paymentStage.setScene(new Scene(paymentView.getView(), 500, 600));
                paymentStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                paymentStage.initOwner(mainApp.getPrimaryStage());
                paymentStage.showAndWait();
                closeWindow();
            }
            
        } else {
            // FARK YOK VEYA İADE -> DİREKT DEĞİŞTİR (Eski Yöntem)
            boolean success = reservationManager.changeSeat(currentReservation.getReservationCode(), selectedSeatNum);
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Koltuk değişiminiz ücretsiz olarak yapıldı.");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Hata", "İşlem başarısız oldu.");
            }
        }
    }
    
    private void closeWindow() {
        Stage stage = (Stage) btnConfirm.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
