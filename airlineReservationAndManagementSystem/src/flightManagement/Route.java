package flightManagement;

public class Route {
	//Departure/Arrival information
	private String departureCity;
	private String arrivalCity;
	
	public Route(String departureCity, String arrivalCity) {
		if (departureCity.equalsIgnoreCase(arrivalCity)) {
            throw new IllegalArgumentException("Kalkış ve varış şehirleri aynı olamaz!");
        }
        
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
	}

	public String getDepartureCity() {
		return departureCity;
	}

	public String getArrivalCity() {
		return arrivalCity;
	}
	
	public String toString() {
		return "Rota -> Kalkış Şehri: " + this.departureCity + " - Varış Şehri: " + this.arrivalCity; 
	}

}
