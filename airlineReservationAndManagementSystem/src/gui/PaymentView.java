package gui;

import flightManagement.Flight;
import flightManagement.Seat;
import reservationAndTicketing.Baggage;
import reservationAndTicketing.Passenger;
import reservationAndTicketing.Reservation;
import reservationAndTicketing.Ticket;
import servicesAndManagers.CalculatePrice;
import servicesAndManagers.ReservationManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;

public class PaymentView {
	private MainApp mainApp;
    private Flight flight;
    private Passenger passenger;
    private Passenger bookerPassenger;
    private String seatNum;
    private int baggageKg;
    private ReservationManager reservationManager;
    private CalculatePrice priceCalculator;
    
    private TextField txtCardNum;
    private TextField txtMonth;
    private TextField txtYear;
    private TextField txtCvv;

    public PaymentView(MainApp mainApp, Flight flight, Passenger passenger, Passenger bookerPassenger, String seatNum, int baggageKg, ReservationManager mgr) {
        this.mainApp = mainApp;
        this.flight = flight;
        this.passenger = passenger;
        this.bookerPassenger = bookerPassenger; // Kaydet
        this.seatNum = seatNum;
        this.baggageKg = baggageKg;
        this.reservationManager = mgr;
        this.priceCalculator = new CalculatePrice(); 
    }
    
    public Parent getView() {
    	
    	long hoursUntilFlight = java.time.Duration.between(java.time.LocalDateTime.now(), flight.getDate()).toHours();
        
        if (hoursUntilFlight < 24 && hoursUntilFlight >= 0) {
            // Platform.runLater kullanarak arayüz çizildikten hemen sonra çıkmasını sağlıyoruz
            javafx.application.Platform.runLater(() -> 
                showAlert("Son Dakika Uçuşu", 
                          "Dikkat: Uçuşa 24 saatten az bir süre kaldığı için \nbilet fiyatlarına son dakika tarifesi uygulanmıştır.")
            );
        }
    	
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #fdfdfd;");

        // Başlık
        Label lblTitle = new Label("Ödeme ve Biletleme");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        // --- HESAPLAMALAR ---
        
        // 1. Koltuk objesini bul
        Seat selectedSeat = flight.getPlane().getSeatMatrix().get(seatNum);
        
        // 2. Geçici bir rezervasyon oluştur (Fiyat hesaplamak için)
        // Not: Bu rezervasyon henüz listeye eklenmez, sadece hesaplayıcıya verilir.
        Reservation tempRes = new Reservation("TEMP", flight, passenger, selectedSeat);
        
        // 3. CalculatePrice sınıfı üzerinden bilet fiyatını al (Son dakika, mesafe vb. dahil)
        double ticketBasePrice = priceCalculator.calculateTicketPrice(tempRes);
        
        // 4. Bagaj Ücreti Hesapla (Ticket.java mantığına göre)
        // Business: 30kg, Ekonomi: 15kg. Ekstra kg başı 50 TL.
        int allowance = (selectedSeat.getSeatType() == Seat.SeatType.BUSINESS) ? 30 : 15;
        double excessWeight = Math.max(0, baggageKg - allowance);
        double baggageFee = excessWeight * 50.0;
        
        double totalPrice = ticketBasePrice + baggageFee;

        // --- GÖRÜNÜM ELEMANLARI ---
        
        VBox summaryBox = new VBox(8);
        summaryBox.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label lblPass = new Label("Yolcu: " + passenger.getName() + " " + passenger.getSurname());
        Label lblFlight = new Label("Uçuş: " + flight.getFlightNum() + " | " + flight.getFormattedDate());
        Label lblSeat = new Label("Koltuk: " + seatNum + " (" + selectedSeat.getSeatType() + ")");
        Label lblBaggage = new Label("Bagaj: " + baggageKg + " kg (Hak: " + allowance + " kg)");
        
        summaryBox.getChildren().addAll(lblPass, lblFlight, lblSeat, lblBaggage);

        // Fiyat Detayı
        Label lblBasePrice = new Label(String.format("Bilet Ücreti: %.2f ₺", ticketBasePrice));
        Label lblBagPrice = new Label(String.format("Ekstra Bagaj Ücreti: %.2f ₺", baggageFee));
        
        Label lblTotal = new Label(String.format("TOPLAM TUTAR: %.2f ₺", totalPrice));
        lblTotal.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblTotal.setStyle("-fx-text-fill: #27ae60;");

        // Kart Bilgileri (Görsel)
        VBox cardForm = new VBox(10);
        cardForm.setAlignment(Pos.CENTER_LEFT);
        
        // Kart Numarası
        txtCardNum = new TextField(); 
        txtCardNum.setPromptText("Kart Numarası (16 Hane)"); // PromptText kullanıldı!
        
        // Son Kullanma Tarihi ve CVV Kutusu
        HBox expiryBox = new HBox(10);
        
        txtMonth = new TextField();
        txtMonth.setPromptText("Ay (01-12)");
        txtMonth.setPrefWidth(80);
        
        txtYear = new TextField();
        txtYear.setPromptText("Yıl (20--)");
        txtYear.setPrefWidth(80);
        
        txtCvv = new TextField();
        txtCvv.setPromptText("CVV (3 Hane)");
        txtCvv.setPrefWidth(100);
        
        expiryBox.getChildren().addAll(txtMonth, new Label("/"), txtYear, new Label("  "), txtCvv);
        
        cardForm.getChildren().addAll(new Label("Kredi Kartı Bilgileri:"), txtCardNum, expiryBox);
        
        // Ödeme Butonu
        Button btnPay = new Button("Ödemeyi Onayla (" + String.format("%.2f", totalPrice) + " ₺)");
        btnPay.setStyle("-fx-base: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnPay.setPrefHeight(45);
        btnPay.setMaxWidth(Double.MAX_VALUE);

        // Butona basılınca önce validasyon, sonra ödeme işlemi
        btnPay.setOnAction(e -> {
            if (validateInputs()) {
                handlePayment(ticketBasePrice, totalPrice);
            }
        });

        layout.getChildren().addAll(lblTitle, summaryBox, new Separator(), lblBasePrice, lblBagPrice, lblTotal, new Separator(), cardForm, new Separator(), btnPay);
        
        return layout;
    }

    private void handlePayment(double basePrice, double totalPrice) {
        // 1. Rezervasyonu Gerçekleştir (Make Reservation)
        boolean resSuccess = reservationManager.makeReservation(
            flight.getPlane(), 
            flight, 
            passenger, 
            seatNum,
            bookerPassenger.getPassengerID()
        );

        if (resSuccess) {
            // 2. Rezervasyonu Bul (Bilet üretmek için)
            // En son eklenen veya ID ile eşleşen rezervasyonu buluyoruz
            Reservation targetRes = findMyReservation();

            if (targetRes != null) {
                // 3. Bileti Oluştur (Generate Ticket)
                // Manager, CalculatePrice kullanarak temel bileti oluşturup kaydeder.
                Ticket ticket = reservationManager.generateTicket(targetRes);
                
                // 4. Bagaj Bilgisini ve Son Fiyatı Güncelle (Opsiyonel: Runtime objesini düzeltiyoruz)
                // Ticket.java içinde Baggage objesi varsa:
                if (baggageKg > 0) ticket.setPassengerBaggage(new Baggage(baggageKg));
                
                // NOT: Manager'daki generateTicket metodu bileti dosyaya hemen yazar.
                // Eğer bagaj ücretini dosyaya da yansıtmak istiyorsanız Manager yapısında 
                // değişiklik gerekir veya burada bileti tekrar kaydetmeniz gerekir.
                // Ancak PaymentView sadece GUI olduğu için şimdilik kullanıcıya bilgi verip geçiyoruz.

                showAlert("Ödeme Başarılı", 
                        "İşleminiz tamamlandı.\n" +
                        "PNR: " + targetRes.getReservationCode() + "\n" +
                        "Bilet No: " + ticket.getTicketID() + "\n" +
                        "Toplam Tutar: " + String.format("%.2f ₺", totalPrice));
                
                closeAllWindows();
                
            } else {
                showAlert("Hata", "Rezervasyon yapıldı ancak bilet oluşturulamadı.");
            }
        } else {
            showAlert("Hata", "Rezervasyon yapılamadı. Koltuk dolmuş olabilir.");
        }
    }

    private Reservation findMyReservation() {
        // Yolcunun rezervasyonları arasından bu uçuşa ait olanı bul
        // En güvenli yöntem PNR kodunu makeReservation'dan döndürmektir ama
        // mevcut metod boolean dönüyor. Bu yüzden listeyi tarıyoruz.
        ArrayList<Reservation> list = reservationManager.getReservationsByBooker(bookerPassenger.getPassengerID());
        for (Reservation r : list) {
            if (r.getFlight().getFlightNum().equals(flight.getFlightNum()) && 
                    r.getSeat().getSeatNum().equals(seatNum)) {
                // Çakışmayı önlemek için rezervasyon saati veya durumu da kontrol edilebilir
                return r; 
            }
        }
        return null;
    }
    
    private boolean validateInputs() {
        String errorMsg = "";
        
        // 1. Kart Numarası Kontrolü (Sadece rakam ve 16 hane)
        String cardNum = txtCardNum.getText().trim();
        if (!cardNum.matches("\\d{16}")) {
            errorMsg += "- Kart numarası 16 haneli rakamlardan oluşmalıdır.\n";
        }
        
        // 2. CVV Kontrolü (3 hane)
        String cvv = txtCvv.getText().trim();
        if (!cvv.matches("\\d{3}")) {
            errorMsg += "- CVV kodu 3 haneli olmalıdır.\n";
        }
        
        // 3. Tarih Kontrolü (Ay ve Yıl mantığı)
        String monthStr = txtMonth.getText().trim();
        String yearStr = txtYear.getText().trim();
        
        try {
            int month = Integer.parseInt(monthStr);
            int year = Integer.parseInt(yearStr);
            
            // Ay 1-12 arasında mı?
            if (month < 1 || month > 12) {
                errorMsg += "- Ay bilgisi 1 ile 12 arasında olmalıdır.\n";
            }
            
            // Geçmiş tarih kontrolü
            LocalDate now = LocalDate.now();
            int currentYear = now.getYear();
            int currentMonth = now.getMonthValue();
            
            if (year < currentYear) {
                errorMsg += "- Kartınızın son kullanım yılı geçmiş olamaz.\n";
            } else if (year == currentYear && month < currentMonth) {
                errorMsg += "- Kartınızın son kullanım tarihi geçmiştir.\n";
            }
            
        } catch (NumberFormatException e) {
            errorMsg += "- Ay ve Yıl alanlarına sadece sayı giriniz.\n";
        }
        
        // Hata varsa göster ve false döndür
        if (!errorMsg.isEmpty()) {
            showAlert("Hatalı Giriş", "Lütfen bilgileri kontrol ediniz:\n\n" + errorMsg);
            return false;
        }
        
        return true;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeAllWindows() {
        // Tüm açık popup pencereleri kapat
    	new ArrayList<>(Stage.getWindows()).stream()
        .filter(window -> window instanceof Stage && window != mainApp.getPrimaryStage())
        .forEach(window -> ((Stage) window).close());
    }
}
