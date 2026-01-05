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
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.Optional;

public class SeatSelectionView {
	
	private MainApp mainApp;
    private Flight flight;
    private ReservationManager reservationManager;
    private String selectedSeatNum = null; // Kullanıcının o an tıkladığı koltuk

    public SeatSelectionView(MainApp mainApp, Flight flight, ReservationManager reservationManager) {
        this.mainApp = mainApp;
        this.flight = flight;
        this.reservationManager = reservationManager;
    }

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

                    // Duruma göre renk ver
                    if (seat.isReserved()) {
                        btn.setStyle("-fx-background-color: #ff6b6b;"); // Kırmızı (Dolu)
                        btn.setDisable(true);
                    } else {
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
        Button btnBook = new Button("Bilgileri Gir ve Rezervasyonu Tamamla");
        btnBook.setStyle("-fx-base: #f39c12; -fx-font-size: 14px; -fx-font-weight: bold;");
        btnBook.setPrefHeight(50);
        btnBook.setOnAction(e -> handleBookingProcess());
        
        HBox bottomBar = new HBox(btnBook);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(10));
        layout.setBottom(bottomBar);

        return layout;
    }
    
    
    private void handleBookingProcess() {
        if (selectedSeatNum == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen önce tablodan boş bir koltuk seçiniz!");
            return;
        }

        // Özel Dialog Penceresi Oluştur (Yolcu Bilgileri İçin)
        Dialog<Passenger> dialog = new Dialog<>();
        dialog.setTitle("Yolcu Bilgileri");
        dialog.setHeaderText("Biletleme için lütfen bilgilerinizi eksiksiz giriniz.");

        // Dialog Butonları
        ButtonType loginButtonType = new ButtonType("Onayla", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Form Düzeni
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtName = new TextField(); txtName.setPromptText("Ad");
        TextField txtSurname = new TextField(); txtSurname.setPromptText("Soyad");
        TextField txtId = new TextField(); txtId.setPromptText("T.C. / Pasaport No");
        TextField txtPhone = new TextField(); txtPhone.setPromptText("Telefon (5XX...)");

        grid.add(new Label("Ad:"), 0, 0);
        grid.add(txtName, 1, 0);
        grid.add(new Label("Soyad:"), 0, 1);
        grid.add(txtSurname, 1, 1);
        grid.add(new Label("Kimlik No:"), 0, 2);
        grid.add(txtId, 1, 2);
        grid.add(new Label("Telefon:"), 0, 3);
        grid.add(txtPhone, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Varsayılan olarak odaklanılacak alan
        Platform.runLater(() -> txtName.requestFocus());

        // Sonuç Dönüştürücü (Butona basılınca ne dönecek?)
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                // Boş alan kontrolü
                if (txtName.getText().isEmpty() || txtSurname.getText().isEmpty() || 
                    txtId.getText().isEmpty() || txtPhone.getText().isEmpty()) {
                    return null; // Null dönerse aşağıda hata basarız veya işlem yapmayız
                }
                return new Passenger(txtId.getText(), txtName.getText(), txtSurname.getText(), txtPhone.getText());
            }
            return null;
        });

        // Dialogu Göster ve Sonucu Bekle
        Optional<Passenger> result = dialog.showAndWait();

        if (result.isPresent()) {
            Passenger passenger = result.get();
            
            // Backend'e gönder (Şimdilik mock işlem, backend entegrasyonu için ReservationManager kullanılmalı)
            // boolean success = reservationManager.makeReservation(flight.getPlane(), flight, passenger, selectedSeatNum);
            
            // GEÇİCİ SİMÜLASYON (Backend tam değilse bunu kullanın):
            flight.getPlane().getSeatMatrix().get(selectedSeatNum).setReserveStatus(true);
            boolean success = true;

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", 
                        "Sayın " + passenger.getName() + " " + passenger.getSurname() + "\n" +
                        flight.getFlightNum() + " uçuşu için " + selectedSeatNum + " numaralı koltuk ayrıldı.");
                
                // Pencereyi kapat veya güncelle (Burada basitçe yeni bir View çağırılabilir veya bu pencere kapatılabilir)
            } else {
                showAlert(Alert.AlertType.ERROR, "Hata", "Rezervasyon işlemi gerçekleştirilemedi.");
            }
        } else {
            // Kullanıcı iptal etti veya eksik bilgi girdi
            showAlert(Alert.AlertType.WARNING, "İptal", "Bilgiler eksik girildiği için işlem yapılmadı.");
        }
    }
    
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    

    /*private void handleBooking() {
        if (selectedSeatNum == null) {
            new Alert(Alert.AlertType.WARNING, "Lütfen bir koltuk seçin!").show();
            return;
        }

        // Basitçe yolcu bilgisi alalım (Normalde önceki ekrandan gelmeli)
        TextInputDialog dialog = new TextInputDialog("Ali");
        dialog.setTitle("Yolcu Bilgisi");
        dialog.setHeaderText("Lütfen Adınızı Girin:");
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            // Geçici Yolcu Oluştur (Sizin Passenger constructor'a uygun)
            //Passenger passenger = new Passenger("12345678901", result.get(), "Yilmaz", "05554443322");
            
            
            //plane'in içini doldurmamız lazım
            //Plane plane = new Plane("1234", "Boeing", 180);
            //boolean success = reservationManager.makeReservation(plane, flight, passenger, selectedSeatNum);
            boolean success = false;
            
            // Eğer backend hazır değilse GUI tarafında manuel set edelim ki görelim:
            // (Backend tamamlanınca burayı silin)
            if(!success) { 
                 flight.getPlane().getSeatMatrix().get(selectedSeatNum).setReserveStatus(true);
                 success = true; // Simüle edildi
            }

            if (success) {
                new Alert(Alert.AlertType.INFORMATION, "Rezervasyon Başarılı!").showAndWait();
                // Ekranı kapatılabilir veya yenilenebilir
            } else {
                new Alert(Alert.AlertType.ERROR, "Rezervasyon Başarısız!").show();
            }
        }
    }
    */

}
