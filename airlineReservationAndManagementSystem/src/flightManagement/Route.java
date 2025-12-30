package flightManagement;

public class Route {
	private String departureCity;
	private String arrivalCity;
	private double distanceKm;
	
	public Route(String departureCity, String arrivalCity, double distanceKm) {
		if (departureCity.equalsIgnoreCase(arrivalCity)) {
            throw new IllegalArgumentException("Kalkış ve varış şehirleri aynı olamaz!");
        }
        
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.distanceKm = distanceKm;
	}

	public String getDepartureCity() {
		return departureCity;
	}

	public String getArrivalCity() {
		return arrivalCity;
	}
	
	public double getDistanceKm() {
		return distanceKm;
	}
	
	public String toString() {
		return "Rota -> Kalkış Şehri: " + this.departureCity + " - Varış Şehri: " + this.arrivalCity + " - Mesafe" + this.distanceKm; 
	}
	
	public boolean equals(Object comparedObject) {
		if(this == comparedObject) {
			return true;
		}
		
		if(!(comparedObject instanceof Route)) {
			return false;
		}
		
		Route comparedRoute = (Route) comparedObject;
		
		if(this.arrivalCity.equalsIgnoreCase(comparedRoute.arrivalCity) && this.departureCity.equalsIgnoreCase(comparedRoute.departureCity) && this.distanceKm == comparedRoute.distanceKm) {
			return true;
		}
		
		return false;
	}

}
