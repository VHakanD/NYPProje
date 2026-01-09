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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
        this.loggedInPassenger = loggedInPassenger;
    }

    public Parent getView() {
    	StackPane rootOverlay = new StackPane();
        rootOverlay.setAlignment(Pos.CENTER);
        
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));
        
        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);

        Label lblHeader = new Label("Koltuk Değişimi: " + flight.getFlightNum());
        lblHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label lblCurrent = new Label("Mevcut Koltuğunuz: " + currentReservation.getSeat().getSeatNum());
        lblCurrent.setStyle("-fx-font-size: 14px; -fx-text-fill: #e67e22; -fx-font-weight: bold;"); // Turuncu
        
        topBox.getChildren().addAll(lblHeader, lblCurrent);
        layout.setTop(topBox);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setAlignment(Pos.TOP_CENTER);
        
        //ColumnConstraints colNormal = new ColumnConstraints(); 
        ColumnConstraints colAisle = new ColumnConstraints(); 
        colAisle.setMinWidth(40);
        
        grid.getColumnConstraints().addAll(
            new ColumnConstraints(), new ColumnConstraints(), // 0, 1
            new ColumnConstraints(), new ColumnConstraints(), new ColumnConstraints(), // 2, 3, 4
            colAisle
        );
        
        Plane plane = flight.getPlane();
        Map<String, Seat> seats = plane.getSeatMatrix();

        int totalRows = plane.getCapacity() / 6;
        char[] cols = {'A', 'B', 'C', 'D', 'E', 'F'};
        
        ToggleGroup seatGroup = new ToggleGroup();
        
        for (int row = 1; row <= totalRows; row++) {
            Label lblRow = new Label(String.valueOf(row));
            lblRow.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
            grid.add(lblRow, 0, row - 1);

            for (int c = 0; c < cols.length; c++) {
                char colChar = cols[c];
                String seatNum = row + String.valueOf(colChar);
                Seat seat = seats.get(seatNum);

                if (seat != null) {
                    ToggleButton btnSeat = new ToggleButton(seatNum);
                    btnSeat.setPrefSize(55, 45); 
                    btnSeat.setToggleGroup(seatGroup);

                    boolean isBusiness = (seat.getSeatType() == Seat.SeatType.BUSINESS);
                    boolean isTable = isBusiness && (colChar == 'B' || colChar == 'E');
                    boolean isMySeat = seatNum.equals(currentReservation.getSeat().getSeatNum());

                    if (isTable) {
                        btnSeat.setText("SEHPA");
                        btnSeat.setStyle("-fx-base: #000000; -fx-text-fill: white; -fx-font-size: 9px; -fx-font-weight: bold;");
                        btnSeat.setDisable(true); 
                    } 
                    else if (isMySeat) {
                        btnSeat.setText("SİZ");
                        btnSeat.setStyle("-fx-base: #e67e22; -fx-text-fill: white; -fx-font-weight: bold;");
                        btnSeat.setDisable(true); 
                    }
                    else if (seat.isReserved()) {
                        btnSeat.setStyle("-fx-base: #e74c3c; -fx-text-fill: white;"); 
                        btnSeat.setDisable(true);
                    } 
                    else {
                        if (isBusiness) {
                            btnSeat.setStyle("-fx-base: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");
                        } else {
                            btnSeat.setStyle("-fx-base: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
                        }
                        
                        btnSeat.setOnAction(e -> {
                            if (btnSeat.isSelected()) {
                                selectedSeatNum = seatNum;
                                
                                double diff = calculatePriceDifference(seat);
                                String diffText = (diff > 0) ? String.format(" (+%.2f ₺)", diff) : " (Farksız/İade)";
                                
                                lblSelectionInfo.setText("Yeni Seçilen: " + seatNum + diffText);
                                lblSelectionInfo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
                                btnConfirm.setDisable(false);
                            } else {
                                selectedSeatNum = null;
                                lblSelectionInfo.setText("Lütfen yeni bir koltuk seçiniz.");
                                lblSelectionInfo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
                                btnConfirm.setDisable(true);
                            }
                        });
                    }

                    int gridColIndex = calculateGridColumn(colChar);

                    if (colChar == 'A') {
                        Rectangle leftWindow = new Rectangle(8, 45, Color.LIGHTBLUE);
                        leftWindow.setArcWidth(5); leftWindow.setArcHeight(5);
                        grid.add(leftWindow, gridColIndex - 1, row - 1);
                    }

                    grid.add(btnSeat, gridColIndex, row - 1);

                    if (colChar == 'F') {
                        Rectangle rightWindow = new Rectangle(8, 45, Color.LIGHTBLUE);
                        rightWindow.setArcWidth(5); rightWindow.setArcHeight(5);
                        grid.add(rightWindow, gridColIndex + 1, row - 1);
                    }
                }
            }
        }
        

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;"); 
        layout.setCenter(scroll);
        
        VBox bottomBox = new VBox(15);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20, 0, 0, 0));
        
        lblSelectionInfo = new Label("Lütfen yeni bir koltuk seçiniz.");
        lblSelectionInfo.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        HBox btnBox = new HBox(20);
        btnBox.setAlignment(Pos.CENTER);
        
        Button btnCancel = new Button("İptal");
        btnCancel.setStyle("-fx-base: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnCancel.setOnAction(e -> closeWindow());
        
        btnConfirm = new Button("Koltuk Değişimini Onayla");
        btnConfirm.setStyle("-fx-base: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        btnConfirm.setDisable(true);
        btnConfirm.setOnAction(e -> handleSeatChange());
        
        btnBox.getChildren().addAll(btnCancel, btnConfirm);
        bottomBox.getChildren().addAll(lblSelectionInfo, btnBox);
        layout.setBottom(bottomBox);
        
       
        Image stewardessImage = new Image("https://cdn-icons-png.flaticon.com/512/2534/2534690.png", 60, 150, true, true);
        ImageView stewardessView = new ImageView(stewardessImage);
        stewardessView.setOpacity(0.85);
        StackPane.setAlignment(stewardessView, Pos.CENTER);
        
        rootOverlay.getChildren().addAll(layout, stewardessView);

        return rootOverlay;
    }
    
    private void handleSeatChange() {
        if (selectedSeatNum == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen yeni bir koltuk seçiniz.");
            return;
        }

        CalculatePrice calculator = new CalculatePrice();
        Seat newSeat = flight.getPlane().getSeatMatrix().get(selectedSeatNum);
        
        Reservation tempOldRes = new Reservation("TEMP", flight, currentReservation.getPassenger(), currentReservation.getSeat());
        double oldPrice = calculator.calculateTicketPrice(tempOldRes);
        
        Reservation tempNewRes = new Reservation("TEMP", flight, currentReservation.getPassenger(), newSeat);
        double newPrice = calculator.calculateTicketPrice(tempNewRes);
        
        double priceDifference = newPrice - oldPrice;

        if (priceDifference > 0) {
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Ödeme Gerekiyor");
            alert.setHeaderText("Koltuk Sınıf Farkı");
            alert.setContentText(
                "Yeni koltuk için " + String.format("%.2f", priceDifference) + " TL fark ödemeniz gerekmektedir.\n" +
                "Ödeme ekranına yönlendiriliyorsunuz..."
            );

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                PaymentView paymentView = new PaymentView(
                    mainApp, 
                    flight, 
                    currentReservation.getPassenger(), 
                    loggedInPassenger, 
                    selectedSeatNum, 
                    0,
                    reservationManager, 
                    currentReservation
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
            boolean success = reservationManager.changeSeat(currentReservation.getReservationCode(), selectedSeatNum);
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Koltuk değişiminiz ücretsiz olarak yapıldı.");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Hata", "İşlem başarısız oldu.");
            }
        }
    }
    
    private int calculateGridColumn(char colChar) {
        int baseIndex = colChar - 'A';
        int gridIndex = baseIndex + 2; 
        
        if (colChar > 'C') {
            gridIndex++;
        }
        return gridIndex;
    }
    
    private double calculatePriceDifference(Seat newSeat) {
        CalculatePrice calculator = new CalculatePrice();
        
        Reservation tempOldRes = new Reservation("TEMP", flight, currentReservation.getPassenger(), currentReservation.getSeat());
        double oldPrice = calculator.calculateTicketPrice(tempOldRes);
        
        Reservation tempNewRes = new Reservation("TEMP", flight, currentReservation.getPassenger(), newSeat);
        double newPrice = calculator.calculateTicketPrice(tempNewRes);
        
        return newPrice - oldPrice;
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
