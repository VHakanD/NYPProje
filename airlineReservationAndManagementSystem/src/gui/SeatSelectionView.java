package gui;

import flightManagement.Flight;
import flightManagement.Seat;
import reservationAndTicketing.Passenger;
import reservationAndTicketing.Reservation;
import servicesAndManagers.CalculatePrice;
import servicesAndManagers.ReservationManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SeatSelectionView {
    
    private MainApp mainApp;
    private Flight flight;
    private ReservationManager reservationManager;
    private String selectedSeatNum = null; 
    private Button btnBook;
    private Label lblSelectionInfo;
    private Passenger loggedInPassenger;

    public SeatSelectionView(MainApp mainApp, Flight flight, ReservationManager reservationManager, Passenger loggedInPassenger) {
        this.mainApp = mainApp;
        this.flight = flight;
        this.reservationManager = reservationManager;
        this.loggedInPassenger = loggedInPassenger;
    }
    
    public Parent getView() {
        StackPane rootOverlay = new StackPane();
        rootOverlay.setAlignment(Pos.CENTER);

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));
        
        Label lblTitle = new Label("Koltuk Seçimi");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblTitle.setStyle("-fx-text-fill: #2c3e50;");
        
        StackPane topContainer = new StackPane(lblTitle);
        topContainer.setPadding(new Insets(0, 0, 15, 0));
        layout.setTop(topContainer);

        // --- 2. SOL PANEL (UÇUŞ BİLGİLERİ - GÖRSEL BURAYA EKLENDİ) ---
        VBox leftBox = createLeftInfoPane();
        BorderPane.setAlignment(leftBox, Pos.TOP_CENTER);
        BorderPane.setMargin(leftBox, new Insets(10, 10, 10, 0));
        layout.setLeft(leftBox);

        // --- 3. SAĞ PANEL (RENK LEJANTI) ---
        VBox rightBox = createRightLegendPane();
        BorderPane.setAlignment(rightBox, Pos.TOP_CENTER);
        BorderPane.setMargin(rightBox, new Insets(10, 0, 10, 10));
        layout.setRight(rightBox);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(8);
        gridPane.setVgap(8);
        gridPane.setAlignment(Pos.TOP_CENTER);
        
        ColumnConstraints colAisle = new ColumnConstraints(); 
        colAisle.setMinWidth(50);
        
        gridPane.getColumnConstraints().addAll(
            new ColumnConstraints(), new ColumnConstraints(),
            new ColumnConstraints(), new ColumnConstraints(), new ColumnConstraints(),
            colAisle
        );
        
        Label lblAisleHeader = new Label("KORİDOR");
        lblAisleHeader.setStyle("-fx-font-size: 10px; -fx-text-fill: #95a5a6; -fx-font-weight: bold;");
        GridPane.setHalignment(lblAisleHeader, javafx.geometry.HPos.CENTER);
        GridPane.setMargin(lblAisleHeader, new Insets(0, 0, 10, 0));
        gridPane.add(lblAisleHeader, 5, 0);

        Map<String, Seat> seatMatrix = flight.getPlane().getSeatMatrix();
        int totalRows = flight.getPlane().getCapacity() / 6; 
        char lastColChar = 'F'; 

        ToggleGroup seatGroup = new ToggleGroup();

        for (int row = 1; row <= totalRows; row++) {
            for (char col = 'A'; col <= lastColChar; col++) {
                String seatNum = row + String.valueOf(col);
                Seat seat = seatMatrix.get(seatNum);

                if (seat != null) {
                    ToggleButton btnSeat = new ToggleButton(seatNum);
                    btnSeat.setPrefSize(55, 45); 
                    btnSeat.setToggleGroup(seatGroup);
                    boolean isBusiness = (seat.getSeatType() == Seat.SeatType.BUSINESS);
                    boolean isTable = isBusiness && (col == 'B' || col == 'E');
                    
                    
                    if (isTable) {
                        btnSeat.setText("SEHPA");
                        btnSeat.setStyle("-fx-base: #000000; -fx-text-fill: white; -fx-font-size: 9px; -fx-font-weight: bold;");
                        btnSeat.setDisable(true); 
                    } 
                    else if (seat.isReserved()) {
                        btnSeat.setStyle("-fx-base: #e74c3c; -fx-text-fill: white;"); 
                        btnSeat.setDisable(true);
                    } 
                    else if (isBusiness) {
                        btnSeat.setStyle("-fx-base: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");
                    } 
                    else {
                        btnSeat.setStyle("-fx-base: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;"); 
                    }
                    
                    btnSeat.setOnAction(e -> {
                        if (btnSeat.isSelected()) {
                            selectedSeatNum = seatNum;
                            
                            CalculatePrice calculator = new CalculatePrice();
                            Reservation tempRes = new Reservation("TEMP", flight, loggedInPassenger, seat);
                            double realPrice = calculator.calculateTicketPrice(tempRes);
                            
                            String typeStr = isBusiness ? "Business" : "Ekonomi";
                            lblSelectionInfo.setText("Seçilen: " + seatNum + " (" + typeStr + ") - Tutar: " + String.format("%.2f", realPrice) + " ₺");
                            lblSelectionInfo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
                            btnBook.setDisable(false);
                            
                        } else {
                            selectedSeatNum = null;
                            lblSelectionInfo.setText("Lütfen bir koltuk seçiniz.");
                            lblSelectionInfo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
                            btnBook.setDisable(true);
                        }
                    });

                    int gridColIndex = calculateGridColumn(col);

                    if (col == 'A') {
                        Rectangle leftWindow = new Rectangle(8, 45, Color.LIGHTBLUE);
                        leftWindow.setArcWidth(5); leftWindow.setArcHeight(5);
                        gridPane.add(leftWindow, gridColIndex - 1, row - 1);
                    }

                    gridPane.add(btnSeat, gridColIndex, row - 1);

                    if (col == 'F') {
                        Rectangle rightWindow = new Rectangle(8, 45, Color.LIGHTBLUE);
                        rightWindow.setArcWidth(5); rightWindow.setArcHeight(5);
                        gridPane.add(rightWindow, gridColIndex + 1, row - 1);
                    }
                }
            }
        }
        
        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #f0f2f5;");
        layout.setCenter(scrollPane);

        VBox bottomBox = new VBox(15);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20, 0, 0, 0));

        lblSelectionInfo = new Label("Lütfen bir koltuk seçiniz.");
        lblSelectionInfo.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        HBox btnBox = new HBox(20);
        btnBox.setAlignment(Pos.CENTER);
        
        Button btnCancel = new Button("İptal");
        btnCancel.setStyle("-fx-base: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;"); // Kırmızı stil
        btnCancel.setOnAction(e -> {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        });

        btnBook = new Button("Bilgileri Gir ve Tamamla");
        btnBook.setStyle("-fx-base: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        btnBook.setDisable(true);
        btnBook.setOnAction(e -> handleBookingProcess());

        btnBox.getChildren().addAll(btnCancel, btnBook);
        bottomBox.getChildren().addAll(lblSelectionInfo, btnBox);
        layout.setBottom(bottomBox);

        Image stewardessImage = new Image("https://cdn-icons-png.flaticon.com/512/2534/2534690.png", 60, 150, true, true);
        ImageView stewardessView = new ImageView(stewardessImage);
        stewardessView.setOpacity(0.85);
        stewardessView.setMouseTransparent(true);
        
        StackPane centerStack = new StackPane();
        StackPane.setAlignment(stewardessView, Pos.CENTER);
        centerStack.getChildren().addAll(scrollPane, stewardessView);

        layout.setCenter(centerStack);
        
        rootOverlay.getChildren().addAll(layout);

        return rootOverlay;
    }

    private int calculateGridColumn(char colChar) {
        int baseIndex = colChar - 'A';
        int gridIndex = baseIndex + 2; 
        
        if (colChar > 'C') {
            gridIndex++; 
        }
        return gridIndex;
    }
    
    private void handleBookingProcess() {
        if (selectedSeatNum == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen önce tablodan boş bir koltuk seçiniz!");
            return;
        }
        
        Seat currentSeat = flight.getPlane().getSeatMatrix().get(selectedSeatNum);
        int freeAllowance = (currentSeat.getSeatType() == Seat.SeatType.BUSINESS) ? 30 : 15;
        
        Label lblBaggageInfo = new Label("(Hak: " + freeAllowance + " kg)");
        lblBaggageInfo.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");

        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("Yolcu ve Bagaj Bilgileri");
        dialog.setHeaderText("Lütfen yolcu bilgilerini ve bagaj miktarını giriniz.");

        ButtonType confirmButtonType = new ButtonType("Ödemeye Geç", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtName = new TextField(loggedInPassenger.getName()); 
        TextField txtSurname = new TextField(loggedInPassenger.getSurname());
        TextField txtId = new TextField(loggedInPassenger.getPassengerID());
        TextField txtPhone = new TextField(loggedInPassenger.getContactInfo());
        TextField txtBaggage = new TextField(); 
        txtBaggage.setPromptText("KG (Örn: 15)");
        
        
        grid.add(new Label("Ad:"), 0, 0);       grid.add(txtName, 1, 0);
        grid.add(new Label("Soyad:"), 0, 1);    grid.add(txtSurname, 1, 1);
        grid.add(new Label("Kimlik No:"), 0, 2); grid.add(txtId, 1, 2);
        grid.add(new Label("Telefon:"), 0, 3);  grid.add(txtPhone, 1, 3);
        
        grid.add(new Label("Bagaj (kg):"), 0, 4); 
        grid.add(txtBaggage, 1, 4); 
        grid.add(lblBaggageInfo, 2, 4);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(txtBaggage::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                if (txtName.getText().isEmpty() || txtSurname.getText().isEmpty() || 
                    txtId.getText().isEmpty() || txtPhone.getText().isEmpty() || txtBaggage.getText().isEmpty()) {
                    return null;
                }
                
                Map<String, Object> data = new HashMap<>();
                data.put("passenger", new Passenger(txtId.getText(), txtName.getText(), txtSurname.getText(), txtPhone.getText()));
                
                try {
                    data.put("baggage", Integer.parseInt(txtBaggage.getText()));
                } catch (NumberFormatException e) {
                    data.put("baggage", 0);
                }
                
                return data;
            }
            return null;
        });

        Optional<Map<String, Object>> result = dialog.showAndWait();

        if (result.isPresent()) {
            Map<String, Object> data = result.get();
            Passenger flyerPassenger = (Passenger) data.get("passenger");
            int baggageKg = (int) data.get("baggage");
            
            closeWindow();
            
            PaymentView paymentView = new PaymentView(mainApp, flight, flyerPassenger, loggedInPassenger, selectedSeatNum, baggageKg, reservationManager);
            Stage paymentStage = new Stage();
            paymentStage.setTitle("Ödeme ve Biletleme");
            paymentStage.setScene(new Scene(paymentView.getView(), 500, 600));
            paymentStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            
            paymentStage.initOwner(mainApp.getPrimaryStage());
            paymentStage.show();
            
        } else {
            showAlert(Alert.AlertType.INFORMATION, "İşlem İptal Edildi", 
                    "Yolcu bilgileri eksik girildiği veya işlem iptal edildiği için ödeme adımına geçilemedi.\n\n" +
                    "Lütfen tekrar deneyiniz.");
        }
    }
    
    // --- GÜNCELLENEN KISIM BURASI (GÖRSEL EKLENDİ) ---
    private VBox createLeftInfoPane() {
        VBox box = new VBox(8); // Satırlar arası boşluk
        box.setPadding(new Insets(15));
        box.setPrefWidth(220); // Genişliği artırdık (Resim sığsın diye)
        box.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");

        Label lblHeader = new Label("Uçuş Detayları");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblHeader.setStyle("-fx-text-fill: #2980b9;");
        lblHeader.setUnderline(true);

        // --- HAZERFEN GÖRSELİ EKLEME BAŞLANGIÇ ---
        String imageUrl = "https://i.postimg.cc/VNfGzgc3/HAZERFEN_AIRLINES_(1).png";
        Image logoImage = new Image(imageUrl, true); // true = Arka planda yükle
        ImageView imageView = new ImageView(logoImage);
        imageView.setFitWidth(180); // Sol panele sığacak genişlik
        imageView.setPreserveRatio(true); // En/Boy oranını koru
        // --- HAZERFEN GÖRSELİ EKLEME BİTİŞ ---

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
            imageView, // Görseli başlığın altına ekledik
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
        box.setAlignment(Pos.TOP_CENTER);

        String imageUrl = "https://cdn-icons-png.flaticon.com/512/3127/3127363.png";
        Image ticketImage = new Image(imageUrl, true);
        ImageView imageView = new ImageView(ticketImage);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        Label lblHeader = new Label("Koltuk Durumu");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblHeader.setStyle("-fx-text-fill: #2980b9;");
        lblHeader.setUnderline(true);

        box.getChildren().addAll(
            imageView,
            lblHeader,
            new Separator(),
            createLegendItem("#2ecc71", "Ekonomi"),
            createLegendItem("#9b59b6", "Business"),
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
        Stage stage = (Stage) btnBook.getScene().getWindow();
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