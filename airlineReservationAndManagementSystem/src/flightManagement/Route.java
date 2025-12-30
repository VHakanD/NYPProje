package flightManagement;

<<<<<<< HEAD

public class Route {
	private String departureCity;
	private String arrivalCity;
	private double distanceKm;
=======
public class Seat {
>>>>>>> e76dfb56fb9176556174f43c9385ff657edf20b1
	
	public enum SeatType{ ECONOMY, BUSINESS} 
	
	private String seatNum;
	private boolean reserveStatus;
	private double price;
	private SeatType seatType;
	
	public Seat(String seatNum, double price, SeatType seatType) {
		this.seatNum = seatNum;
		this.reserveStatus = false;
		this.price = price;
		this.seatType = seatType;
	}

	public String getSeatNum() {
		return seatNum;
	}

	public boolean isReserveStatus() {
		return reserveStatus;
	}

	public void setReserveStatus(boolean reserveStatus) {
		this.reserveStatus = reserveStatus;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public SeatType getSeatType() {
		return seatType;
	}
	
	public void reserved() {
        if (!this.reserveStatus) {
        	
            setReserveStatus(true);
            System.out.println(seatNum + " numaralı koltuk rezerve edildi.");
            
        }
        else
        {
            System.out.println(seatNum + " dolu! işlem başarısız.");
        }
    }
	
	public void cancelReservation() {
        if (this.reserveStatus) {
        	
            setReserveStatus(false);
            System.out.println("Rezervasyon iptal edildi.");
            
        }
    }
	
	public String toString() {
		String info = "Koltuk Türü: " + this.seatType + "\nKoltuk Numarası: " + this.seatNum +
				 "\nKoltuk Ücreti:" + this.price + "\nRezervasyon Durumu: " + this.reserveStatus;
		return info;
	}
<<<<<<< HEAD
	
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
=======
}
>>>>>>> e76dfb56fb9176556174f43c9385ff657edf20b1
