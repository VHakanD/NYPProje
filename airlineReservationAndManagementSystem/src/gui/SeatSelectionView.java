package gui;

import flightManagement.Flight;
import flightManagement.Plane;
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
    
    /*public Parent getView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));

        Label lblHeader = new Label("Uçuş: " + flight.getFlightNum() + " - " + flight.getRoute().toString());
        layout.setTop(lblHeader);

        // --- KOLTUK MATRİSİ (GRID) ---
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        Plane plane = flight.getPlane();
        Map<String, Seat> seats = plane.getSeatMatrix();

        int totalRows = plane.getCapacity() / 6;
        char[] cols = {'A', 'B', 'C', 'D', 'E', 'F'};
        lblSelectionInfo = new Label("Lütfen bir koltuk seçiniz.");
        lblSelectionInfo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        for (int row = 1; row <= totalRows; row++) {
            for (int c = 0; c < cols.length; c++) {
                char colChar = cols[c];
                String seatNum = row + String.valueOf(colChar);
                Seat seat = seats.get(seatNum);

                if (seat != null) {
                    Button btn = new Button(seatNum);
                    btn.setPrefSize(55, 45); // Butonları biraz büyüttük (Sehpa yazısı sığsın diye)

                    // --- Backend'den Gelen Gerçek Tipe Bakıyoruz ---
                    boolean isBusiness = (seat.getSeatType() == Seat.SeatType.BUSINESS);
                    
                    // Business Class'ta Orta Koltuklar (B ve E) Sehpa Olsun
                    boolean isTable = isBusiness && (colChar == 'B' || colChar == 'E');

                    if (isTable) {
                        // --- SEHPA / MASAL (Business Orta Koltuk) ---
                        btn.setText("SEHPA");
                        btn.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 10px;");
                        btn.setDisable(true); // Tıklanamaz
                    } 
                    else if (seat.isReserved()) {
                        // --- DOLU KOLTUK ---
                        btn.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;"); 
                        btn.setDisable(true);
                    } 
                    else {
                        // --- RENK AYARLARI ---
                        if (isBusiness) {
                            btn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");
                        } else {
                            btn.setStyle("-fx-background-color: #51cf66; -fx-text-fill: white;");
                        }
                        
                        // --- 3. TIKLAMA OLAYI GÜNCELLEMESİ ---
                        // Hem Business hem Ekonomi için ortak tıklama mantığı
                        btn.setOnAction(e -> {
                            selectedSeatNum = seatNum;
                            
                            // Label'ı güncelle
                            String typeStr = isBusiness ? "Business" : "Ekonomi";
                            lblSelectionInfo.setText("Seçilen Koltuk: " + seatNum + " (" + typeStr + ")");
                            lblSelectionInfo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;"); // Yeşil renk yap
                        });
                    }

                    // Arayüzde koridor boşluğu bırakmak için sütun indeksi ayarlama
                    int colIndex = c;
                    if (c >= 3) colIndex++; // A,B,C (boşluk) D,E,F
                    
                    grid.add(btn, colIndex, row);
                }
            }
        }
        
        // Koridor Etiketi
        Label lblKoridor = new Label("KORİDOR");
        lblKoridor.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
        grid.add(lblKoridor, 3, 0);

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        // ScrollPane arka planını temizle
        scroll.setStyle("-fx-background-color:transparent;");
        layout.setCenter(scroll);

        // --- REZERVASYONU TAMAMLA ---
        btnBook = new Button("Bilgileri Gir ve Rezervasyonu Tamamla");
        btnBook.setStyle("-fx-base: #f39c12; -fx-font-size: 14px; -fx-font-weight: bold;");
        btnBook.setPrefHeight(45);
        btnBook.setOnAction(e -> handleBookingProcess());
        
        VBox bottomContainer = new VBox(10); // Aralarında 10px boşluk
        bottomContainer.setAlignment(Pos.CENTER);
        bottomContainer.setPadding(new Insets(15));
        bottomContainer.getChildren().addAll(lblSelectionInfo, btnBook);
        layout.setBottom(bottomContainer);

        return layout;
    }*/
    
    public Parent getView() {
        // --- 1. ANA KATMAN (StackPane - Hostes için) ---
        StackPane rootOverlay = new StackPane();
        rootOverlay.setAlignment(Pos.CENTER);

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));

        // --- 2. ÜST BİLGİ ---
        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);
        Label lblTitle = new Label("Koltuk Seçimi: " + flight.getFlightNum());
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Label lblRoute = new Label(flight.getRoute().toString() + " | " + flight.getFormattedDate());
        topBox.getChildren().addAll(lblTitle, lblRoute);
        layout.setTop(topBox);

        // --- 3. ORTA KISIM (Koltuk Izgarası) ---
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(8); // Koltuklar arası boşluk
        gridPane.setVgap(8);
        gridPane.setAlignment(Pos.TOP_CENTER);
        
        // --- KORİDOR GENİŞLİĞİ AYARI ---
        // Sütunların genişliklerini ayarlayarak koridoru açıyoruz.
        // İndeksler: 0(SıraNo), 1(Pencere), 2(A), 3(B), 4(C), 5(KORİDOR), 6(D), 7(E), 8(F), 9(Pencere)
        ColumnConstraints colNormal = new ColumnConstraints(); // Normal sütunlar
        ColumnConstraints colAisle = new ColumnConstraints(); 
        colAisle.setMinWidth(40); // Koridor için 40px ekstra boşluk (Genişletildi)
        
        // GridPane'e sütun kısıtlamalarını ekleyelim (Opsiyonel ama garanti çözüm)
        // 5. indekse kadar normal, 5. indeks koridor, sonrası normal
        gridPane.getColumnConstraints().addAll(
            new ColumnConstraints(), new ColumnConstraints(), // 0, 1
            new ColumnConstraints(), new ColumnConstraints(), new ColumnConstraints(), // 2, 3, 4
            colAisle // 5 (Koridor)
        );

        Map<String, Seat> seatMatrix = flight.getPlane().getSeatMatrix();
        int totalRows = flight.getPlane().getCapacity() / 6; 
        char lastColChar = 'F'; 

        ToggleGroup seatGroup = new ToggleGroup();

        for (int row = 1; row <= totalRows; row++) {
            // Sıra Numarası (En Sol)
            Label lblRow = new Label(String.valueOf(row));
            lblRow.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
            gridPane.add(lblRow, 0, row - 1);

            for (char col = 'A'; col <= lastColChar; col++) {
                String seatNum = row + String.valueOf(col);
                Seat seat = seatMatrix.get(seatNum);

                if (seat != null) {
                    ToggleButton btnSeat = new ToggleButton(seatNum);
                    btnSeat.setPrefSize(55, 45); 
                    btnSeat.setToggleGroup(seatGroup);

                    // --- MANTIK BİRLEŞTİRME ---
                    boolean isBusiness = (seat.getSeatType() == Seat.SeatType.BUSINESS);
                    // Business ise ve B veya E sütunu ise -> SEHPA
                    boolean isTable = isBusiness && (col == 'B' || col == 'E');
                    
                    // --- STİL DÜZELTMESİ (-fx-base KULLANIMI) ---
                    // -fx-base kullanınca JavaFX'in 3D buton efekti ve seçim gölgelendirmesi geri gelir.
                    
                    if (isTable) {
                        // SEHPA (Siyah - Tıklanamaz)
                        btnSeat.setText("SEHPA");
                        btnSeat.setStyle("-fx-base: #000000; -fx-text-fill: white; -fx-font-size: 9px; -fx-font-weight: bold;");
                        btnSeat.setDisable(true); 
                    } 
                    else if (seat.isReserved()) {
                        // DOLU (Kırmızı)
                        btnSeat.setStyle("-fx-base: #e74c3c; -fx-text-fill: white;"); 
                        btnSeat.setDisable(true);
                    } 
                    else if (isBusiness) {
                        // BUSINESS (Mor)
                        btnSeat.setStyle("-fx-base: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");
                    } 
                    else {
                        // EKONOMİ (Yeşil)
                        btnSeat.setStyle("-fx-base: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;"); 
                    }
                    
                    // --- SEÇİM VE FİYAT ---
                    btnSeat.setOnAction(e -> {
                        if (btnSeat.isSelected()) {
                            selectedSeatNum = seatNum;
                            
                            // Fiyat Hesaplama
                            CalculatePrice calculator = new CalculatePrice();
                            Reservation tempRes = new Reservation("TEMP", flight, loggedInPassenger, seat);
                            double realPrice = calculator.calculateTicketPrice(tempRes);
                            
                            String typeStr = isBusiness ? "Business" : "Ekonomi";
                            lblSelectionInfo.setText("Seçilen: " + seatNum + " (" + typeStr + ") - Tutar: " + String.format("%.2f", realPrice) + " ₺");
                            lblSelectionInfo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
                            btnBook.setDisable(false);
                            
                            // NOT: ToggleButton seçilince JavaFX otomatik olarak rengi koyulaştırır (-fx-base sayesinde).
                            // Ekstra bir stil vermemize gerek yok, kullanıcı seçili olduğunu butonun "basılı" halinden anlar.
                        } else {
                            selectedSeatNum = null;
                            lblSelectionInfo.setText("Lütfen bir koltuk seçiniz.");
                            lblSelectionInfo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
                            btnBook.setDisable(true);
                        }
                    });

                    // --- PENCERELER VE GRID YERLEŞİMİ ---
                    int gridColIndex = calculateGridColumn(col);

                    // Sol Pencere (Açık Mavi)
                    if (col == 'A') {
                        Rectangle leftWindow = new Rectangle(8, 45, Color.LIGHTBLUE); // İnce uzun cam
                        leftWindow.setArcWidth(5); leftWindow.setArcHeight(5);
                        gridPane.add(leftWindow, gridColIndex - 1, row - 1);
                    }

                    // Koltuğu Ekle
                    gridPane.add(btnSeat, gridColIndex, row - 1);

                    // Sağ Pencere (Açık Mavi)
                    if (col == 'F') {
                        Rectangle rightWindow = new Rectangle(8, 45, Color.LIGHTBLUE);
                        rightWindow.setArcWidth(5); rightWindow.setArcHeight(5);
                        gridPane.add(rightWindow, gridColIndex + 1, row - 1);
                    }
                }
            }
        }
        
        // ScrollPane (Kaydırma Çubuğu)
        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #f0f2f5;");
        layout.setCenter(scrollPane);

        // --- 4. ALT KISIM ---
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
            // Pencereyi kapatma işlemi
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

        // --- 5. HOSTES FİGÜRÜ ---
        // Hostes görseli internetten çekiliyor
        Image stewardessImage = new Image("https://cdn-icons-png.flaticon.com/512/2534/2534690.png", 60, 150, true, true);
        ImageView stewardessView = new ImageView(stewardessImage);
        stewardessView.setOpacity(0.85);
        StackPane.setAlignment(stewardessView, Pos.CENTER); // Tam ortaya sabitle
        
        // Hostesi en üste ekle
        rootOverlay.getChildren().addAll(layout, stewardessView);

        return rootOverlay;
    }

    // Grid Hesaplama (Pencereler ve Koridor Boşluğu İçin)
    private int calculateGridColumn(char colChar) {
        int baseIndex = colChar - 'A'; 
        // 0:SıraNo, 1:Pencere, 2:A, 3:B, 4:C ...
        int gridIndex = baseIndex + 2; 
        
        // C'den sonra (D, E, F) koridor boşluğu bırak (+1 Sütun Atla)
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

        // --- Yolcu Bilgileri Dialogu ---
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

        // Sonuç Dönüştürücü
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                // Basit Validasyon
                if (txtName.getText().isEmpty() || txtSurname.getText().isEmpty() || 
                    txtId.getText().isEmpty() || txtPhone.getText().isEmpty() || txtBaggage.getText().isEmpty()) {
                    return null;
                }
                
                // Verileri paketle ve döndür
                Map<String, Object> data = new HashMap<>();
                data.put("passenger", new Passenger(txtId.getText(), txtName.getText(), txtSurname.getText(), txtPhone.getText()));
                
                // Bagaj kilosunu sayıya çevir (Hata kontrolü basit tutuldu)
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
            
            closeWindow(); // Önce bu ekranı kapat
            
            // Ödeme Ekranını Aç
            PaymentView paymentView = new PaymentView(mainApp, flight, flyerPassenger, loggedInPassenger, selectedSeatNum, baggageKg, reservationManager);
            Stage paymentStage = new Stage();
            paymentStage.setTitle("Ödeme ve Biletleme");
            paymentStage.setScene(new Scene(paymentView.getView(), 500, 600));
            paymentStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            
            // 2. Sahiplik: Ana pencereye bağla
            paymentStage.initOwner(mainApp.getPrimaryStage());
            paymentStage.show();
            
        } else {
            // İptal edildi veya eksik bilgi
        	showAlert(Alert.AlertType.INFORMATION, "İşlem İptal Edildi", 
                    "Yolcu bilgileri eksik girildiği veya işlem iptal edildiği için ödeme adımına geçilemedi.\n\n" +
                    "Lütfen tekrar deneyiniz.");
        }
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
