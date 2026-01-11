package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import servicesAndManagers.CalculatePrice;
import flightManagement.*;
import reservationAndTicketing.*;


public class CalculatePriceTest {
	
	private CalculatePrice calculator;
    private Flight flight;
    private Passenger passenger;
	//private IntSet set;
	
	@BeforeEach
	void setUp() {
		
		calculator = new CalculatePrice();
		
		// Fiyat hesabı için 1000 km'lik bir rota
        Route route = new Route("Istanbul", "Antalya", 1000); 
        
        // 10 gün sonraki bir uçuş (Son dakika zammı etkilemesin diye)
        flight = new Flight("TEST01", route, LocalDateTime.now().plusDays(10), 90);
        
        // Fiyat hesabı için sahte bir yolcu
        passenger = new Passenger("1", "Test", "User", "05555555555");
	}
	
	@Test
	public void setUpTest()
    {
		// Economy Rezervasyonu
        Seat ecoSeat = new Seat("1A", Seat.SeatType.ECONOMY);
        Reservation resEco = new Reservation("R1", flight, passenger, ecoSeat);
        
        // Business Rezervasyonu
        Seat busSeat = new Seat("1B", Seat.SeatType.BUSINESS);
        Reservation resBusiness = new Reservation("R2", flight, passenger, busSeat);
        
        // Fiyatları Hesapla
        double ecoPrice = calculator.calculateTicketPrice(resEco);
        double busPrice = calculator.calculateTicketPrice(resBusiness);
        
        System.out.println("Economy Fiyat: " + ecoPrice);
        System.out.println("Business Fiyat: " + busPrice);

        // Doğrulama: Business fiyatı Economy'den büyük olmalı
        assertTrue(busPrice > ecoPrice, "Hata: Business bilet, Economy'den daha pahalı olmalıdır!");
    }
	
}
