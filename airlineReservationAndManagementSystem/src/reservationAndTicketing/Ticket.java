package reservationAndTicketing;

import flightManagement.Seat;

public class Ticket {
	//ticketID, Reservation object, price, baggageAllowance
	private String ticketId;
	private Reservation aReservation;
	private double price;
	private double baggageAllowance;
	private Baggage passengerBaggage;
	
	public Ticket(String ticketId, Reservation aReservation, double price) {
		this.ticketId = ticketId;
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

	public String getTicketId() {
		return ticketId;
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
		else
		{
			return 0.0;	
		}
		
	}
	public String toString() {
		String info = "Bilet Numarası: " + this.ticketId + " " + this.aReservation.toString() 
					+ "Bilet Ücreti: " + this.price + "Bagaj Ekstrası: " + excessWeight() ;
		return info;
	}

}
