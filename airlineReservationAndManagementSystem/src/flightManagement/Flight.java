package flightManagement;


import java.time.LocalDateTime;

public class Flight {
	//flightNum, departurePlace, arrivalPlace, date, hour, duration
	private int flightNum;
	private String departurePlace;
	private String arrivalPlace;
	private LocalDateTime date;
	private int hour;
	private int duration;
	//private Route route eklmemek mantıklı mı??
	
	public Flight(int flightNum, String departurePlace, String arrivalPlace, LocalDateTime date, int hour, int duration) {
		super();
		this.flightNum = flightNum;
		this.departurePlace = departurePlace;
		this.arrivalPlace = arrivalPlace;
		this.date = date;
		this.hour = hour;
		this.duration = duration;
	}

	public int getFlightNum() {
		return flightNum;
	}

	public String getDeparturePlace() {
		return departurePlace;
	}

	public void setDeparturePlace(String departurePlace) {
		this.departurePlace = departurePlace;
	}

	public String getArrivalPlace() {
		return arrivalPlace;
	}

	public void setArrivalPlace(String arrivalPlace) {
		this.arrivalPlace = arrivalPlace;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public String toString() {
		String info = "Uçuş Numarası: " + this.flightNum + "\nKalkış Şehri: " + this.departurePlace
				+ "\nVarış Şehri: " + this.arrivalPlace + "\nUçuş Tarihi - Saati: " + this.date + " - "
				+ this.hour + "\nUçuş Süresi: " + this.duration;
		return info;
	}
	
	
	

}
