package flightManagement;

public class Seat {
	public enum SeatType{ ECONOMY, BUSINESS}
	private String seatNum;
	private boolean reserveStatus;
	private double price;
	private SeatType seatType;
	
	public Seat(String seatNum, SeatType seatType) {
		this.seatNum = seatNum;
		this.reserveStatus = false;
		this.seatType = seatType;
		price = 1;
	}

	public String getSeatNum() {
		return seatNum;
	}

	public boolean isReserved() {
		return reserveStatus;
	}

	public void setReserveStatus(boolean reserveStatus) {
		this.reserveStatus = reserveStatus;
	}

	public double getPrice() {
		return price;
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
            System.out.println(this.seatNum + "numaralı koltuğun rezervasyonu iptal edildi.");
            
        }
    }
	
	public String toString() {
		String info = "Koltuk Türü: " + this.seatType + "\nKoltuk Numarası: " + this.seatNum +
				 "\nKoltuk Ücreti:" + this.price + "\nRezervasyon Durumu: " + this.reserveStatus;
		return info;
	}
}
