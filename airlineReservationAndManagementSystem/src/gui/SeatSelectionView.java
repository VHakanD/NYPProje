package gui;

import flightManagement.Flight;
import flightManagement.Plane;
import flightManagement.Seat;
import reservationAndTicketing.Passenger;
import servicesAndManagers.ReservationManager;
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
                    grid.add(btn, c, row); // (Sütun, Satır)
                }
            }
        }

        ScrollPane scroll = new ScrollPane(grid);
        layout.setCenter(scroll);

        // --- REZERVASYONU TAMAMLA ---
        Button btnBook = new Button("Rezervasyonu Tamamla");
        btnBook.setOnAction(e -> handleBooking());
        
        HBox bottomBar = new HBox(10, btnBook);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(10));
        layout.setBottom(bottomBar);

        return layout;
    }

    private void handleBooking() {
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

}
