package reservationAndTicketing;

public class Ticket {
	//ticketID, Reservation object, price, baggageAllowance
	private String ticketId;
	private Reservation aReservation;
	private double price;
	private double baggageAllowance;
	private Baggage passengerBaggage;
	
	public Ticket(String ticketId, Reservation aReservation, double price, double baggageAllowance, Baggage passengerBaggage) {
		this.ticketId = ticketId;
		this.aReservation = aReservation;
		this.price = price;
		this.baggageAllowance = baggageAllowance;
		this.passengerBaggage = passengerBaggage;
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
	
	

}
