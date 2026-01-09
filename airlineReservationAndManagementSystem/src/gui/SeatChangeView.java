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
        
        /*StackPane topContainer = new StackPane();
        
        VBox titleBox= new VBox(10);
        titleBox.setAlignment(Pos.CENTER);

        Label lblHeader = new Label("Koltuk Değişimi: " + flight.getFlightNum());
        lblHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label lblCurrent = new Label("Mevcut Koltuğunuz: " + currentReservation.getSeat().getSeatNum());
        lblCurrent.setStyle("-fx-font-size: 14px; -fx-text-fill: #e67e22; -fx-font-weight: bold;"); // Turuncu
        
        titleBox.getChildren().addAll(lblHeader, lblCurrent);
        
        VBox legendBox = createLegendBox();
        legendBox.setMaxWidth(Region.USE_PREF_SIZE);
        
        topContainer.getChildren().addAll(titleBox, legendBox);
        StackPane.setAlignment(titleBox, Pos.CENTER);      // Başlık Ortada
        StackPane.setAlignment(legendBox, Pos.CENTER_RIGHT);
        
        layout.setTop(topContainer);*/
        
        Label lblHeader = new Label("Koltuk Değişimi");
        lblHeader.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        StackPane topBox = new StackPane(lblHeader);
        topBox.setPadding(new Insets(0, 0, 15, 0));
        layout.setTop(topBox);

        // --- 2. SOL PANEL (UÇUŞ BİLGİSİ) ---
        VBox leftBox = createLeftInfoPane();
        BorderPane.setAlignment(leftBox, Pos.TOP_CENTER);
        BorderPane.setMargin(leftBox, new Insets(10, 10, 10, 0));
        layout.setLeft(leftBox);
        
        // --- 3. SAĞ PANEL (LEJANT) ---
        VBox rightBox = createRightLegendPane();
        BorderPane.setAlignment(rightBox, Pos.TOP_CENTER);
        BorderPane.setMargin(rightBox, new Insets(10, 0, 10, 10));
        layout.setRight(rightBox);

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
        
        Label lblAisleHeader = new Label("KORİDOR");
        lblAisleHeader.setStyle("-fx-font-size: 10px; -fx-text-fill: #95a5a6; -fx-font-weight: bold;");
        GridPane.setHalignment(lblAisleHeader, javafx.geometry.HPos.CENTER);
        GridPane.setMargin(lblAisleHeader, new Insets(0, 0, 10, 0));
        grid.add(lblAisleHeader, 5, 0);
       
        for (int row = 1; row <= totalRows; row++) {
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
        stewardessView.setMouseTransparent(true);

        // 3. StackPane ile üst üste bindir
        StackPane centerStack = new StackPane();
        StackPane.setAlignment(stewardessView, Pos.CENTER);
        centerStack.getChildren().addAll(scroll, stewardessView);

        // 4. Layout'un ortasına ekle
        layout.setCenter(centerStack);
        
        rootOverlay.getChildren().addAll(layout);

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
    
    private VBox createLeftInfoPane() {
    	VBox box = new VBox(8); // Satırlar arası boşluk
        box.setPadding(new Insets(15));
        box.setPrefWidth(200); // Genişliği biraz artırdık
        box.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");

        Label lblHeader = new Label("Uçuş Detayları");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblHeader.setStyle("-fx-text-fill: #2980b9;");
        lblHeader.setUnderline(true);

        Label lblNum = new Label("Sefer No: " + flight.getFlightNum());
        lblNum.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        // Şehirler
        Label lblDep = new Label("Kalkış: " + flight.getRoute().getDepartureCity());
        Label lblArr = new Label("Varış: " + flight.getRoute().getArrivalCity());
        
        // Tarih
        Label lblDate = new Label("Tarih: " + flight.getFormattedDate());
        
        // Saat Hesaplaması (Varış Saati)
        String depTime = flight.getHour();
        java.time.LocalDateTime arrDate = flight.getDate().plusMinutes(flight.getDuration());
        String arrTime = arrDate.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        
        Label lblTimeDep = new Label("Kalkış Saati: " + depTime);
        Label lblTimeArr = new Label("Varış Saati: " + arrTime);
        
        // Stil Ayarları (Okunabilirlik için)
        lblDep.setStyle("-fx-text-fill: #555;");
        lblArr.setStyle("-fx-text-fill: #555;");
        lblTimeDep.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;"); // Yeşil
        lblTimeArr.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;"); // Kırmızı

        box.getChildren().addAll(
            lblHeader, 
            lblNum, 
            new Separator(), 
            lblDep, 
            lblArr, 
            new Separator(),
            lblDate, 
            lblTimeDep, 
            lblTimeArr
        );
        return box;
    }

    private VBox createRightLegendPane() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(15));
        box.setPrefWidth(160);
        box.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");

        Label lblHeader = new Label("Renk Kodları");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblHeader.setStyle("-fx-text-fill: #2980b9;");
        lblHeader.setUnderline(true);

        box.getChildren().addAll(
            lblHeader,
            new Separator(),
            createLegendItem("#2ecc71", "Ekonomi"),
            createLegendItem("#9b59b6", "Business"),
            createLegendItem("#e67e22", "Siz (Mevcut)"), // Burası farklı
            createLegendItem("#e74c3c", "Dolu"),
            createLegendItem("#000000", "Sehpa")
        );
        return box;
    }
    
    private HBox createLegendItem(String colorHex, String text) {
        Rectangle rect = new Rectangle(12, 12, Color.web(colorHex));
        rect.setArcWidth(3);
        rect.setArcHeight(3);
        rect.setStroke(Color.GRAY);
        
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", 10));
        lbl.setTextFill(Color.web("#333"));
        
        HBox item = new HBox(6, rect, lbl);
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
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
