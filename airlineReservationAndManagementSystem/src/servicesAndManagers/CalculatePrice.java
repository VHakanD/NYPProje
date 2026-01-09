package servicesAndManagers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import flightManagement.Flight;
import flightManagement.Seat;
import flightManagement.Seat.SeatType;
import reservationAndTicketing.Reservation;
import reservationAndTicketing.Ticket;

public class CalculatePrice {
	//Includes the business logic for calculating ticket prices (mandatory for JUnit).
	private static final int LAST_MINUTE_THRESHOLD_DAYS = 3;
	private static final double LAST_MINUTE_SURCHARGE = 1.5;
	public enum Distance {
		VERYSHORT(0, 500, 4.0),
        SHORT(500, 1500, 3.5),
        LONG(1500, 3000, 3.0),
        VERYLONG(3000, 20000, 2.5);
		
		private final int minKm;
		private final int maxKm;
		private final double coefficient;
		
		private Distance(int minKm, int maxKm, double coefficient) {
			this.minKm = minKm;
			this.maxKm = maxKm;
			this.coefficient = coefficient;
		}
		
		public static Distance getDistanceType(double km) {
			for(Distance d : values()) {
				if(km >= d.minKm && km <= d.maxKm) {
					return d;
				}
			}
			return VERYLONG;
		}
	}
	
	public CalculatePrice() {
		//boş bırakıldı. Metotlarda işlemler yapılacak.
	}
	
	public double calculateTicketPrice(Reservation reservation) {
		if (reservation == null || reservation.getFlight() == null || reservation.getSeat() == null) {
            return 0.0;
        }
	    Flight flight = reservation.getFlight();
	    Seat seat = reservation.getSeat();
	    
	    double km = flight.getRoute().getDistanceKm();
	    Distance distType = Distance.getDistanceType(km);
	    
	    double finalPrice = (km * distType.coefficient) + seat.getPrice();
	    if(seat.getSeatType() == SeatType.BUSINESS) {
			finalPrice *= 1.5;
	    }
	    
	    long daysLeft = ChronoUnit.DAYS.between(LocalDateTime.now(), flight.getDate());    
	    if(daysLeft <= LAST_MINUTE_THRESHOLD_DAYS) {
            finalPrice *= LAST_MINUTE_SURCHARGE;
	    }
	    
	    return finalPrice;
	}
	
	public double calculateTotalPayment(Ticket ticket) {
        if (ticket == null) return 0.0;

        double ticketPrice = ticket.getPrice();
        double baggageFee = ticket.calculateExcessFee();
        
        return ticketPrice + baggageFee;
    }
}
