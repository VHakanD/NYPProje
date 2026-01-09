package flightManagement;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Flight {
	//flightNum, departurePlace, arrivalPlace, date, hour, duration
	private String flightNum;
	private LocalDateTime date;
	private String hour;
	private int duration;
	private Route route;
	private Plane plane;
	
	private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final DateTimeFormatter TIME_ONLY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
	
	public Flight(String flightNum, Route route, LocalDateTime date, int duration) {
		this.flightNum = flightNum;
		this.route = route;
		this.setDate(date);
		this.duration = duration;
	}
	
	public Flight() {
		
	}
	
	public String getFlightNum() {
		return flightNum;
	}

	public void setFlightNum(String flightNum) {
		this.flightNum = flightNum;
	}
	
	public String getFormattedDate() {
	    if (this.date != null) {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); 
	        return this.date.format(formatter);
	    }
	    return "";
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
		
		if (date != null) {
            this.hour = date.format(TIME_ONLY_FORMATTER);
        } else {
            this.hour = null;
        }
	}

	public String getHour() {
		return hour;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}
	
	public Plane getPlane() { return plane; }
    public void setPlane(Plane plane) {
    	this.plane = plane;
    }

	
	public String toFileFormat() {
		String dateStr = (date != null) ? date.format(DATETIME_FORMATTER) : "null";
		
		return this.flightNum + "," + this.route.getDepartureCity() + "," +
				this.route.getArrivalCity() + "," + dateStr + "," + this.hour + "," + 
				this.duration + "," + this.route.getDistanceKm() + "," + this.plane.getPlaneID() +
				"," + this.plane.getPlaneModel();
	}
	
	public static Flight fromFileFormat(String line) {
		String[] data = line.split(",");
		
		Flight flight = new Flight();
		flight.setFlightNum(data[0]);
		
		
		if (!data[3].equals("null")) {
            LocalDateTime ldt = LocalDateTime.parse(data[3], DATETIME_FORMATTER);
            flight.setDate(ldt); 
        }
		
		flight.setDuration(Integer.parseInt(data[5]));
		
		double distance;
	    try {
	        distance = Double.parseDouble(data[6]);
	    } catch (NumberFormatException e) {
	        distance = 0;
	    }
	    Route tempRoute = new Route(data[1], data[2], distance);
	    flight.setRoute(tempRoute);
		
	    String planeModel = data[8];
	    
		Plane tempPlane = new Plane(data[7], planeModel, 180); 
	    flight.setPlane(tempPlane);
		
		return flight;
	}
	
	
	public String toString() {
		String info = "Uçuş Numarası: " + this.flightNum + "\nRota: " + this.route.toString()  + "\nUçuş Tarihi - Saati: " + this.date + " - "
				+ this.hour + "\nUçuş Süresi: " + this.duration;
		return info;
	}
}
