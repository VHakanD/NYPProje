package reservationAndTicketing;

import java.time.LocalDateTime;

import flightManagement.Flight;
import flightManagement.Seat;

public class Reservation {
	//reservationCode, Flight object, Passenger object, Seat object, dateOfReservation
	private String reservationCode;
	private Flight flight;
	private Passenger passenger;
	private Seat seat;
	private LocalDateTime dateOfReservation;
	
	public Reservation(String reservationCode, Flight flight, Passenger passenger, Seat seat, LocalDateTime dateOfReservation) {
		this.reservationCode = reservationCode;
		this.flight = flight;
		this.passenger = passenger;
		this.seat = seat;
		this.dateOfReservation = dateOfReservation;
	}

	public String getReservationCode() {
		return reservationCode;
	}

	public Flight getFlight() {
		return flight;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public Seat getSeat() {
		return seat;
	}

	public LocalDateTime getDateOfReservation() {
		return dateOfReservation;
	}
	
	public String toString() {
		String info = "PNR: " + this.reservationCode + "Uçuş: " + this.flight.toString() + "Yolcu: " + this.passenger.toString() + "Koltuk: " + this.seat.toString() + "Uçuş Saati: " + dateOfReservation;
		return info;
	}

}
