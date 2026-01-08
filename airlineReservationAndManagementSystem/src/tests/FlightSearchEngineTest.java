package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import servicesAndManagers.*;
import flightManagement.*;

public class FlightSearchEngineTest {
	
	private FlightManager flightManager;

    @BeforeEach
    void setUp() {
        flightManager = new FlightManager();
        flightManager.getFlights().clear();
    }

    @Test
    void testRemoveExpiredFlights() {
        Route route = new Route("Ankara", "Izmir", 600);
        
        // 1. Geçmiş Uçuş
        Flight pastFlight = new Flight("OLD01", route, LocalDateTime.now().minusDays(1), 60);
        pastFlight.setPlane(new Plane("P1", "Test", 100)); // Hata vermemesi için uçak ekle
        
        // 2. Gelecek Uçuş
        Flight futureFlight = new Flight("NEW01", route, LocalDateTime.now().plusDays(1), 60);
        futureFlight.setPlane(new Plane("P2", "Test", 100));
        
        flightManager.addFlight(pastFlight);
        flightManager.addFlight(futureFlight);
        
        flightManager.removeExpiredFlights();
        
        assertFalse(flightManager.getFlights().contains(pastFlight), "Hata: Geçmiş uçuş listeden silinmeliydi!");
        assertTrue(flightManager.getFlights().contains(futureFlight), "Hata: Gelecek uçuş listede kalmalıydı!");
    }
    
    @Test
    void testFilterByDepartureCity() {
        // İstanbul'dan kalkan uçuş
        Route r1 = new Route("Istanbul", "Paris", 2000);
        Flight f1 = new Flight("F1", r1, LocalDateTime.now().plusDays(5), 180);
        f1.setPlane(new Plane("P1", "Test", 100));
        
        // Ankara'dan kalkan uçuş
        Route r2 = new Route("Ankara", "Berlin", 2000);
        Flight f2 = new Flight("F2", r2, LocalDateTime.now().plusDays(5), 180);
        f2.setPlane(new Plane("P2", "Test", 100));
        
        flightManager.addFlight(f1);
        flightManager.addFlight(f2);
        
        // Sadece İstanbul olanları getir
        ArrayList<Flight> results = flightManager.flightsByDepartureCity("Istanbul");
        
        assertEquals(1, results.size(), "Hata: Istanbul kalkışlı sadece 1 uçuş dönmeli.");
        assertEquals("F1", results.get(0).getFlightNum(), "Hata: Yanlış uçuş filtrelendi.");
    }
}
