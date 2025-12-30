package flightManagement;


import java.time.LocalDateTime;

public class Flight {
	//flightNum, departurePlace, arrivalPlace, date, hour, duration
	private String flightNum;
	//private String departurePlace;
	//private String arrivalPlace;
	private LocalDateTime date;
	private int hour;
	private int duration;
	private Route route;
	//private Route route eklemek mantıklı mı?? 
	
	public Flight(String flightNum, Route route, LocalDateTime date, int hour, int duration) {
		this.flightNum = flightNum;
		//this.departurePlace = departurePlace;
		//this.arrivalPlace = arrivalPlace;
		this.route = route;
		this.date = date;
		this.hour = hour;
		this.duration = duration;
	}

	public String getFlightNum() {
		return flightNum;
	}

	public Route getRoute() {
		return route;
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
		String info = "Uçuş Numarası: " + this.flightNum + "\nRota: " + this.route.toString()  + "\nUçuş Tarihi - Saati: " + this.date + " - "
				+ this.hour + "\nUçuş Süresi: " + this.duration;
		return info;
	}
	
	
	

}
