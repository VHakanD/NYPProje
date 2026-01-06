package gui;

import flightManagement.Flight;
import flightManagement.Plane;
import flightManagement.Seat;
import reservationAndTicketing.Passenger;
import servicesAndManagers.ReservationManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SeatSelectionView {
	
	private MainApp mainApp;
    private Flight flight;
    private ReservationManager reservationManager;
    private String selectedSeatNum = null; // Kullanıcının o an tıkladığı koltuk
    private Button btnBook;
    private Label lblSelectionInfo;

    public SeatSelectionView(MainApp mainApp, Flight flight, ReservationManager reservationManager) {
        this.mainApp = mainApp;
        this.flight = flight;
        this.reservationManager = reservationManager;
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

        // 30 Sıra x 6 Sütun (Sizin SeatManager yapınıza göre)
        int totalRows = plane.getCapacity() / 6;
        char[] cols = {'A', 'B', 'C', 'D', 'E', 'F'};

        for (int row = 1; row <= totalRows; row++) {
            for (int c = 0; c < cols.length; c++) {
                String seatNum = row + String.valueOf(cols[c]);
                Seat seat = seats.get(seatNum);

                if (seat != null) {
                    Button btn = new Button(seatNum);
                    btn.setPrefSize(50, 40);
                    
                    boolean isBusiness = (row <= 4);
                    
                    

                    // Duruma göre renk ver
                    if (seat.isReserved()) {
                        btn.setStyle("-fx-background-color: #ff6b6b;"); // Kırmızı (Dolu)
                        btn.setDisable(true);
                    } else if(isBusiness) {
                    	btn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");
                        
                        // Tıklama olayını buraya da ekliyoruz
                        btn.setOnAction(e -> {
                            selectedSeatNum = seatNum;
                            // Kullanıcıya Business seçtiğini belirtebiliriz
                            new Alert(Alert.AlertType.INFORMATION, "Seçilen Business Koltuk: " + seatNum).show();
                        });
                    }else {
                        btn.setStyle("-fx-background-color: #51cf66;"); // Yeşil (Boş)
                        
                        // Tıklama Olayı
                        btn.setOnAction(e -> {
                            selectedSeatNum = seatNum;
                            new Alert(Alert.AlertType.INFORMATION, "Seçilen Koltuk: " + seatNum).show();
                        });
                    }
                    int colIndex = c;
                    if (c >= 3) colIndex++;
                    
                    grid.add(btn, colIndex, row); // (Sütun, Satır)
                }
            }
        }
        
        grid.add(new Label("KORİDOR"), 3, 0);

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        layout.setCenter(scroll);

        // --- REZERVASYONU TAMAMLA ---
        btnBook = new Button("Bilgileri Gir ve Rezervasyonu Tamamla");
        btnBook.setStyle("-fx-base: #f39c12; -fx-font-size: 14px; -fx-font-weight: bold;");
        btnBook.setPrefHeight(50);
        btnBook.setOnAction(e -> handleBookingProcess());
        
        HBox bottomBar = new HBox(btnBook);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(10));
        layout.setBottom(bottomBar);

        return layout;
    }*/
    
    public Parent getView() {
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
    }
    
    private void handleBookingProcess() {
        if (selectedSeatNum == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen önce tablodan boş bir koltuk seçiniz!");
            return;
        }

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

        TextField txtName = new TextField(); txtName.setPromptText("Ad");
        TextField txtSurname = new TextField(); txtSurname.setPromptText("Soyad");
        TextField txtId = new TextField(); txtId.setPromptText("TC / Pasaport");
        TextField txtPhone = new TextField(); txtPhone.setPromptText("05XX...");
        TextField txtBaggage = new TextField(); txtBaggage.setPromptText("KG (Örn: 15)"); // YENİ ALAN

        grid.add(new Label("Ad:"), 0, 0);       grid.add(txtName, 1, 0);
        grid.add(new Label("Soyad:"), 0, 1);    grid.add(txtSurname, 1, 1);
        grid.add(new Label("Kimlik No:"), 0, 2); grid.add(txtId, 1, 2);
        grid.add(new Label("Telefon:"), 0, 3);  grid.add(txtPhone, 1, 3);
        grid.add(new Label("Bagaj (kg):"), 0, 4); grid.add(txtBaggage, 1, 4); // YENİ ALAN EKLENDİ

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(txtName::requestFocus);

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
            Passenger passenger = (Passenger) data.get("passenger");
            int baggageKg = (int) data.get("baggage");

            // BURADAKİ DEĞİŞİKLİK:
            // Artık direkt kaydetmiyoruz. PaymentView (Ödeme Ekranı) açıyoruz.
            // Mevcut pencereyi (Koltuk Seçimi) kapatıp ödemeye geçiyoruz.
            
            closeWindow(); // Önce bu ekranı kapat
            
            // Ödeme Ekranını Aç
            PaymentView paymentView = new PaymentView(mainApp, flight, passenger, selectedSeatNum, baggageKg, reservationManager);
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
