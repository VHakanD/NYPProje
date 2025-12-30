package flightManagement;

public class Seat {
	
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
				 "\nÜcret:" + this.price + "\nRezervasyon Durumu: " + this.reserveStatus;
		return info;
	}
}
