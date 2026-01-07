package reservationAndTicketing;

import java.util.UUID;

import flightManagement.Seat;

public class Ticket {
	//ticketID, Reservation object, price, baggageAllowance
	private String ticketID;
	private Reservation aReservation;
	private double price;
	private double baggageAllowance;
	private Baggage passengerBaggage;
	
	public Ticket(Reservation aReservation, double price) {
		this.ticketID = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		this.aReservation = aReservation;
		this.price = price;
		this.passengerBaggage = null;
		
		if(aReservation.getSeat().getSeatType().equals(Seat.SeatType.BUSINESS)) {
			this.baggageAllowance= 30;
		}
		else
		{
			this.baggageAllowance = 15;
		}
	}
	
	public String toFileFormat() {
        return ticketID + "," + 
               aReservation.getReservationCode() + "," + 
               price + "," + 
               baggageAllowance;
    }

	public String getTicketID() {
		return ticketID;
	}

	public Reservation getaReservation() {
		return aReservation;
	}

	public void setaReservation(Reservation aReservation) {
		this.aReservation = aReservation;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getBaggageAllowance() {
		return baggageAllowance;
	}

	public void setBaggageAllowance(double baggageAllowance) {
		this.baggageAllowance = baggageAllowance;
	}

	public Baggage getPassengerBaggage() {
		return passengerBaggage;
	}

	public void setPassengerBaggage(Baggage passengerBaggage) {
		this.passengerBaggage = passengerBaggage;
	}
	
	public double excessWeight() {
		if(this.passengerBaggage == null) {
			return 0.0;
		}
		if(this.passengerBaggage.getWeight()> this.baggageAllowance) {
			return this.passengerBaggage.getWeight() - baggageAllowance;
		}
		
		return 0.0;	
	}
	public double calculateExcessFee() {
		if(passengerBaggage == null) {
			return 0.0;
		}
		double extraWeight = this.excessWeight() ;
		if(extraWeight > 0) {
			return extraWeight * 50.0 ;
		}
		return 0.0;
	}
	
	public String toString() {
		String info = "Bilet Numarası: " + this.ticketID + " " + this.aReservation.toString() 
					+ "Bilet Ücreti: " + this.price + "Bagaj Ekstrası: " + excessWeight() ;
		return info;
	}

}
