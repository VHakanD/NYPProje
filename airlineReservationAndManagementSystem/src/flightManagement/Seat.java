package flightManagement;

public class Seat {
	//seatNum (String, exp. "15A"), Class (Enum: ECONOMY, BUSINESS), price, reserveStatus (boolean).
	public enum SeatType{ ECONOMY, BUSINESS} 
	private String seatNum;
	private boolean reserveStatus;
	private double price;
	private SeatType seatType;
	
	public Seat(String seatNum, boolean reserveStatus, double price, SeatType seatType) {
		super();
		this.seatNum = seatNum;
		this.reserveStatus = reserveStatus;
		this.price = price;
		this.seatType = seatType;
	}

	public String getSeatNum() {
		return seatNum;
	}

	public void setSeatNum(String seatNum) {
		this.seatNum = seatNum;
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

	public void setSeatType(SeatType seatType) {
		this.seatType = seatType;
	}

	
	
	
	

}
