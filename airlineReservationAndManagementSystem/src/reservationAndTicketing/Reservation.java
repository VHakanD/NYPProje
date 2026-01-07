package reservationAndTicketing;

import java.util.*;
import java.util.UUID;

import flightManagement.Flight;
import flightManagement.Seat;
import servicesAndManagers.FlightManager;

public class Reservation {
	//reservationCode, Flight object, Passenger object, Seat object, dateOfReservation
	private String reservationCode;
	private Flight flight;
	private Passenger passenger;
	private Seat seat;
	private String dateOfReservation;
	
	private String bookerID;
	
	public Reservation(String reservationCode, Flight flight, Passenger passenger, Seat seat, String bookerID) {
        this.reservationCode = reservationCode;
        this.flight = flight;
        this.passenger = passenger;
        this.seat = seat;
        this.bookerID = bookerID;
        this.dateOfReservation = java.time.LocalDate.now().toString();
    }
	
	public Reservation(String reservationCode, Flight flight, Passenger passenger, Seat seat) {
        this(reservationCode, flight, passenger, seat, passenger.getPassengerID());
    }
	
	public String toFileFormat() {
		return reservationCode + "," + 
	               flight.getFlightNum() + "," + 
	               passenger.getPassengerID() + "," + 
	               seat.getSeatNum() + "," +
	               bookerID;
    }
	
	public static Reservation fromFileFormat(String line, FlightManager flightMngr, List<Passenger> passengers) {
		String[] data = line.split(",");

        Flight foundFlight = null;
        List<Flight> allFlights = flightMngr.getFlights();
        int f = 0;
        boolean flightFound = false;
        
        while (f < allFlights.size() && !flightFound) {
            Flight currentFlight = allFlights.get(f);
            if (currentFlight.getFlightNum().equals(data[1])) {
                foundFlight = currentFlight;
                flightFound = true;
            }
            f++;
        }

        
        Passenger foundPassenger = null;
        int p = 0;
        boolean passengerFound = false;

        while (p < passengers.size() && !passengerFound) {
            Passenger currentPassenger = passengers.get(p);
            if (currentPassenger.getPassengerID().equals(data[2])) {
                foundPassenger = currentPassenger;
                passengerFound = true;
            }
            p++;
        }
        
        
        if (foundPassenger == null) {
             foundPassenger = new Passenger(data[2], "Bilinmeyen", "Yolcu", "000");
        }

        
        Seat foundSeat = null;
        if (foundFlight != null) {
            foundSeat = foundFlight.getPlane().getSeatMatrix().get(data[3]);
        }
        
        String foundBookerID = (data.length >= 5) ? data[4] : foundPassenger.getPassengerID();

        Reservation result = null;
        
        if (foundFlight != null && foundPassenger != null && foundSeat != null) {
            foundSeat.setReserveStatus(true);
            result = new Reservation(data[0], foundFlight, foundPassenger, foundSeat, foundBookerID);
        } else {
            System.out.println("Hata: Eksik veri bulundu. Satır: " + line);
        }

        return result;
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

	public String getDateOfReservation() {
		return dateOfReservation;
	}
	
	public String getBookerID() {
        return bookerID;
    }
	
	public String generatePNR() {
		return UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
	}
	
	public void setReservationCode(String reservationCode) {
		this.reservationCode = reservationCode;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	public void setSeat(Seat seat) {
		this.seat = seat;
	}

	public void setDateOfReservation(String dateOfReservation) {
		this.dateOfReservation = dateOfReservation;
	}

	public String toString() {
		String info = "PNR: " + this.reservationCode + "Uçuş: " + this.flight.toString() + "Yolcu: " + this.passenger.toString() + "Koltuk: " + this.seat.toString() + "Uçuş Saati: " + dateOfReservation;
		return info;
	}

}
