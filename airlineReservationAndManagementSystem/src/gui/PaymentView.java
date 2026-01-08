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
    
    private Reservation existingReservation = null;
    
    private TextField txtCardNum;
    private TextField txtMonth;
    private TextField txtYear;
    private TextField txtCvv;

    public PaymentView(MainApp mainApp, Flight flight, Passenger passenger, Passenger bookerPassenger, String seatNum, int baggageKg, ReservationManager mgr) {
        this.mainApp = mainApp;
        this.flight = flight;
        this.passenger = passenger;
        this.bookerPassenger = bookerPassenger;
        this.seatNum = seatNum;
        this.baggageKg = baggageKg;
        this.reservationManager = mgr;
        this.priceCalculator = new CalculatePrice(); 
    }
    
    public PaymentView(MainApp mainApp, Flight flight, Passenger passenger, Passenger bookerPassenger, String seatNum, int baggageKg, ReservationManager mgr, Reservation existingReservation) {
        this.mainApp = mainApp;
        this.flight = flight;
        this.passenger = passenger;
        this.bookerPassenger = bookerPassenger;
        this.seatNum = seatNum;
        this.baggageKg = baggageKg;
        this.reservationManager = mgr;
        this.existingReservation = existingReservation;
        this.priceCalculator = new CalculatePrice(); 
    }
    
    public Parent getView() {
    	
    	long hoursUntilFlight = java.time.Duration.between(java.time.LocalDateTime.now(), flight.getDate()).toHours();
        
        if (hoursUntilFlight < 24 && hoursUntilFlight >= 0) {
            javafx.application.Platform.runLater(() -> 
                showAlert("Son Dakika Uçuşu", 
                          "Dikkat: Uçuşa 24 saatten az bir süre kaldığı için \nbilet fiyatlarına son dakika tarifesi uygulanmıştır.")
            );
        }
    	
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #fdfdfd;");

        String titleText = (existingReservation == null) ? "Ödeme ve Biletleme" : "Koltuk Değişimi ve Ödeme";
        Label lblTitle = new Label(titleText);
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        
        Seat selectedSeat = flight.getPlane().getSeatMatrix().get(seatNum);
        
        
        Reservation tempRes = new Reservation("TEMP", flight, passenger, selectedSeat);
        
        double ticketBasePrice = priceCalculator.calculateTicketPrice(tempRes);
        
        
        double finalPriceToPay;
        double priceDiff = 0;
        
        if (existingReservation != null) {
            Reservation tempOldRes = new Reservation("TEMP_OLD", flight, passenger, existingReservation.getSeat());
            double oldPrice = priceCalculator.calculateTicketPrice(tempOldRes);
            
            priceDiff = ticketBasePrice - oldPrice;
            finalPriceToPay = Math.max(0, priceDiff);
            
        } else {
            int allowance = (selectedSeat.getSeatType() == Seat.SeatType.BUSINESS) ? 30 : 15;
            double excessWeight = Math.max(0, baggageKg - allowance);
            double baggageFee = excessWeight * 50.0;
            finalPriceToPay = ticketBasePrice + baggageFee;
        }

        
        VBox summaryBox = new VBox(8);
        summaryBox.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label lblPass = new Label("Yolcu: " + passenger.getName() + " " + passenger.getSurname());
        Label lblFlight = new Label("Uçuş: " + flight.getFlightNum() + " | " + flight.getFormattedDate());
        Label lblSeat = new Label("Koltuk: " + seatNum + " (" + selectedSeat.getSeatType() + ")");
        
        summaryBox.getChildren().addAll(lblPass, lblFlight, lblSeat);
        
        if (existingReservation != null) {
            Label lblOldSeat = new Label("Eski Koltuk: " + existingReservation.getSeat().getSeatNum());
            lblOldSeat.setStyle("-fx-text-fill: #7f8c8d;");
            summaryBox.getChildren().add(lblOldSeat);
            
            Label lblDiffInfo = new Label(String.format("Fiyat Farkı: %.2f ₺", finalPriceToPay));
            lblDiffInfo.setStyle("-fx-font-weight: bold;");
            layout.getChildren().addAll(lblTitle, summaryBox, new Separator(), lblDiffInfo);
        } else {
            Label lblBaggage = new Label("Bagaj: " + baggageKg + " kg");
            summaryBox.getChildren().add(lblBaggage);
            
            Label lblBasePrice = new Label(String.format("Bilet Ücreti: %.2f ₺", ticketBasePrice));
            Label lblTotal = new Label(String.format("TOPLAM TUTAR: %.2f ₺", finalPriceToPay));
            layout.getChildren().addAll(lblTitle, summaryBox, new Separator(), lblBasePrice, lblTotal);
        }
        
        Label lblTotalPay = new Label(String.format("ÖDENECEK TUTAR: %.2f ₺", finalPriceToPay));
        lblTotalPay.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblTotalPay.setStyle("-fx-text-fill: #27ae60;");
        layout.getChildren().add(lblTotalPay);

        
        VBox cardForm = new VBox(10);
        cardForm.setAlignment(Pos.CENTER_LEFT);
        
        
        txtCardNum = new TextField(); 
        txtCardNum.setPromptText("Kart Numarası (16 Hane)"); // PromptText kullanıldı!
        
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
        
        Button btnPay = new Button("Ödemeyi Onayla (" + String.format("%.2f", finalPriceToPay) + " ₺)");
        btnPay.setStyle("-fx-base: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnPay.setPrefHeight(45);
        btnPay.setMaxWidth(Double.MAX_VALUE);

        btnPay.setOnAction(e -> {
            if (validateInputs()) {
            	handlePayment(finalPriceToPay);
            }
        });

        layout.getChildren().addAll(new Separator(), cardForm, new Separator(), btnPay);
        
        return layout;
    }

    private void handlePayment(double amountPaid) {
        if (existingReservation != null) {
            boolean success = reservationManager.changeSeat(existingReservation.getReservationCode(), seatNum);
            
            if (success) {
                showAlert("İşlem Başarılı", 
                        "Koltuk değişiminiz tamamlandı.\n" +
                        "Yeni Koltuk: " + seatNum + "\n" +
                        "Ödenen Fark: " + String.format("%.2f ₺", amountPaid));
                closeAllWindows();
            } else {
                showAlert("Hata", "Koltuk değiştirilemedi (Başkası almış olabilir).");
            }
        } else {
            boolean resSuccess = reservationManager.makeReservation(
                flight.getPlane(), flight, passenger, seatNum, bookerPassenger.getPassengerID()
            );

            if (resSuccess) {
                Reservation targetRes = findMyReservation();
                if (targetRes != null) {
                    Ticket ticket = reservationManager.generateTicket(targetRes);
                    if (baggageKg > 0) ticket.setPassengerBaggage(new Baggage(baggageKg));
                    
                    showAlert("Ödeme Başarılı", 
                            "İşleminiz tamamlandı.\nPNR: " + targetRes.getReservationCode() + 
                            "\nToplam Tutar: " + String.format("%.2f ₺", amountPaid));
                    closeAllWindows();
                }
            } else {
                showAlert("Hata", "Rezervasyon yapılamadı.");
            }
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
                return r; 
            }
        }
        return null;
    }
    
    private boolean validateInputs() {
        String errorMsg = "";
        
        String cardNum = txtCardNum.getText().trim();
        if (!cardNum.matches("\\d{16}")) {
            errorMsg += "- Kart numarası 16 haneli rakamlardan oluşmalıdır.\n";
        }
        
        String cvv = txtCvv.getText().trim();
        if (!cvv.matches("\\d{3}")) {
            errorMsg += "- CVV kodu 3 haneli olmalıdır.\n";
        }
        
        String monthStr = txtMonth.getText().trim();
        String yearStr = txtYear.getText().trim();
        
        try {
            int month = Integer.parseInt(monthStr);
            int year = Integer.parseInt(yearStr);
            
            if (month < 1 || month > 12) {
                errorMsg += "- Ay bilgisi 1 ile 12 arasında olmalıdır.\n";
            }
            
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
    	new ArrayList<>(Stage.getWindows()).stream()
        .filter(window -> window instanceof Stage && window != mainApp.getPrimaryStage())
        .forEach(window -> ((Stage) window).close());
    }
}
