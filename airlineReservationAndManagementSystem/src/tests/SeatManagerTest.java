package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import servicesAndManagers.*;
import flightManagement.*;
import reservationAndTicketing.*;

public class SeatManagerTest {
	
	private SeatManager seatManager;
	private Plane plane;
	
	@BeforeEach
    void setUp() {
        seatManager = new SeatManager();
        plane = new Plane("P1", "Cessna", 6);
        seatManager.seatingArrangements(plane);
    }

    @Test
    void testAvailableSeatCountDecreases() {
        int initialCount = seatManager.availableSeatCount(plane);
        
        if (plane.getSeatMatrix().get("1A") != null) {
            plane.getSeatMatrix().get("1A").setReserveStatus(true);
        }
        
        int newCount = seatManager.availableSeatCount(plane);
        
        assertEquals(initialCount - 1, newCount, "Hata: Rezervasyon sonrası boş koltuk sayısı düşmedi.");
    }

    @Test
    void testInvalidSeatException() {
        assertThrows(IllegalArgumentException.class, () -> {
            seatManager.checkExistingSeats(plane, "99Z");
        }, "Hata: Olmayan koltuk sorgulandığında IllegalArgumentException fırlatılmalıydı.");
    }
	
}
