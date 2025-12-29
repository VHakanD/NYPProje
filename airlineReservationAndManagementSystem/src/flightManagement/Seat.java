package flightManagement;


public class Seat {

	public enum SeatType {
    	ECONOMY,
    	BUSINESS
	}
	/*seatNum (String, exp. "15A"), Class (Enum: ECONOMY, BUSINESS), price, reserveStatus (boolean).
	private String seatNum;
		enum için inner class mı kullanmamız gerekiyor?
	private boolean reserveStatus;
	*/
	private String seatNum;
	private SeatType type;
	private double price;
	private boolean reserveStatus;
	
	public Seat(String seatNum, SeatType type, double price) {
		
        this.seatNum = seatNum;
        this.type = type;
        this.price = price;
        this.reserveStatus = false;
        
	}
	
	public void reserved() {
        if (!this.reserveStatus) {
        	
            this.reserveStatus = true;
            System.out.println(seatNum + " numaralı koltuk rezerve edildi.");
            
        }
        else
        {
            System.out.println(seatNum + " dolu! işlem başarısız.");
        }
    }
	
	public void cancelReservation() {
        if (this.reserveStatus) {
        	
            this.reserveStatus = false;
            System.out.println("Rezervasyon iptal edildi.");
            
        }
    }
	
	public boolean isReserved() {
        return reserveStatus;
    }

    public String getSeatNum() {
        return seatNum;
    }

    public double getPrice() {
        return price;
    }
    
    public SeatType getType() {
        return type;
    }
}
