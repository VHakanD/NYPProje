package reservationAndTicketing;

public class Baggage {
	//weight. It is related to the ticket class.
	private double weight;
	
	public Baggage(double weight) {
		this.weight = weight;
		
	}

	public double getWeight() {
		return weight;
	}
	
	public String toString() {
		return "Bagaj Ağırlığı: " + this.weight;
	}

}
