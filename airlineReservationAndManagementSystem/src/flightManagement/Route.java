package flightManagement;


public class Route {
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
	
	public boolean equals(Object comparedObject) {
		if(this == comparedObject) {
			return true;
		}
		
		if(!(comparedObject instanceof Route)) {
			return false;
		}
		
		Route comparedRoute = (Route) comparedObject;
		
		if(this.arrivalCity.equalsIgnoreCase(comparedRoute.arrivalCity) && this.departureCity.equalsIgnoreCase(comparedRoute.departureCity)) {
			return true;
		}
		
		return false;
	}
	
	public boolean matches(String from, String to) {
		if (from == null || to == null) 
			return false;
		
		boolean isDepartureMatch = this.departureCity.toUpperCase()
	            .contains(from.trim().toUpperCase());

	    boolean isArrivalMatch = this.arrivalCity.toUpperCase()
	            .contains(to.trim().toUpperCase());

	    return isDepartureMatch && isArrivalMatch;
	}

}
