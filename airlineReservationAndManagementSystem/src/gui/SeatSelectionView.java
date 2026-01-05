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
import javafx.stage.Stage;

import java.util.Map;
import java.util.Optional;

public class SeatSelectionView {
	
	private MainApp mainApp;
    private Flight flight;
    private ReservationManager reservationManager;
    private String selectedSeatNum = null; // Kullanıcının o an tıkladığı koltuk
    private Button btnBook;

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
        btnBook = new Button("Bilgileri Gir ve Rezervasyonu Tamamla");
        btnBook.setStyle("-fx-base: #f39c12; -fx-font-size: 14px; -fx-font-weight: bold;");
        btnBook.setPrefHeight(50);
        btnBook.setOnAction(e -> handleBookingProcess());
        
        HBox bottomBar = new HBox(btnBook);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(10));
        layout.setBottom(bottomBar);

        return layout;
    }
    
    
    /*private void handleBookingProcess() {
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
    }*/
    
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void handleBookingProcess() {
        // 1. Koltuk Seçim Kontrolü
        if (selectedSeatNum == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen önce tablodan boş bir koltuk seçiniz!");
            return;
        }

        // 2. Yolcu Bilgileri Dialog Penceresi
        Dialog<Passenger> dialog = new Dialog<>();
        dialog.setTitle("Yolcu Bilgileri");
        dialog.setHeaderText("Biletleme için lütfen bilgilerinizi eksiksiz giriniz.");

        ButtonType loginButtonType = new ButtonType("Onayla", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtName = new TextField(); txtName.setPromptText("Ad");
        TextField txtSurname = new TextField(); txtSurname.setPromptText("Soyad");
        TextField txtId = new TextField(); txtId.setPromptText("T.C. / Pasaport No");
        TextField txtPhone = new TextField(); txtPhone.setPromptText("Telefon (5XX...)");

        grid.add(new Label("Ad:"), 0, 0);       grid.add(txtName, 1, 0);
        grid.add(new Label("Soyad:"), 0, 1);    grid.add(txtSurname, 1, 1);
        grid.add(new Label("Kimlik No:"), 0, 2); grid.add(txtId, 1, 2);
        grid.add(new Label("Telefon:"), 0, 3);  grid.add(txtPhone, 1, 3);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> txtName.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                if (txtName.getText().isEmpty() || txtSurname.getText().isEmpty() || 
                    txtId.getText().isEmpty() || txtPhone.getText().isEmpty()) {
                    return null;
                }
                return new Passenger(txtId.getText(), txtName.getText(), txtSurname.getText(), txtPhone.getText());
            }
            return null;
        });

        Optional<Passenger> result = dialog.showAndWait();

        // 3. Gerçek Rezervasyon İşlemi
        if (result.isPresent()) {
            Passenger passenger = result.get();
            
            // MANAGER ÇAĞRISI (Burada gerçek kayıt yapılıyor ve dosyaya yazılıyor)
            boolean success = reservationManager.makeReservation(
                flight.getPlane(), 
                flight, 
                passenger, 
                selectedSeatNum
            );

            if (success) {
                // İsteğe bağlı: Başarılı rezervasyon sonrası hemen bilet üretimi
                // ReservationManager'da en son eklenen rezervasyonu bulmamız gerekebilir
                // veya makeReservation metodunun Reservation objesi dönmesini sağlayabilirsiniz.
                // Şimdilik sadece mesaj verelim.
                
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", 
                        "Sayın " + passenger.getName() + ",\n" +
                        flight.getFlightNum() + " uçuşu için " + selectedSeatNum + " numaralı koltuk başarıyla ayrıldı.\n" +
                        "Rezervasyon kaydı oluşturuldu.");
                
                // Başarılı işlemden sonra pencreyi kapat
                // 'grid' değişkeni yukarıdaki layout değil, dialog içindeki grid'dir. 
                // Bu yüzden view'in ana layout'una ulaşmamız lazım veya en temiz yol:
                // Butona referans verip oradan sahneye ulaşmak.
                 closeWindow();

            } else {
                showAlert(Alert.AlertType.ERROR, "Hata", "Rezervasyon işlemi gerçekleştirilemedi.\nKoltuk dolu olabilir.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "İptal", "Bilgiler eksik girildiği için işlem yapılmadı.");
        }
    }
    
    private void closeWindow() {
        Stage stage = (Stage) btnBook.getScene().getWindow();
        stage.close();
    }

}
