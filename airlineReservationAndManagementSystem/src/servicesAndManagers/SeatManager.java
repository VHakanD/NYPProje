package servicesAndManagers;

import java.util.ArrayList;

import flightManagement.Plane;
import flightManagement.Seat;

public class SeatManager {
	//It creates the seating arrangements on the plane and calculates the number of available seats.
	private Plane plane;
	
	public SeatManager(Plane plane) {
		this.plane = plane;
	}
	
	public void seatingArrangements() {
		if (!plane.getSeatMatrix().isEmpty()) return;
		
		char[] columns = {'A', 'B', 'C', 'D', 'E', 'F'};
		int rows = plane.getCapacity() / 6;
		int seatTypeChoice = (rows * 10) / 100;
		
		for(int i = 0; i < rows; i++) {
			for(char col: columns) {
				String seatNum = Integer.toString(i+1) + String.valueOf(col);
				
				Seat.SeatType type;
				if(i < seatTypeChoice)
					type = Seat.SeatType.BUSINESS;
				else
					type = Seat.SeatType.ECONOMY;
				
				Seat seat = new Seat(seatNum, type);
				
				plane.addSeat(seat);
				
			}
		}
	}
	
	public int availableSeats() {
		ArrayList<Seat> availableSeats = new ArrayList<>();
		availableSeats = plane.getSeatsByStatus(false);
		return availableSeats.size();
	}
	
	public boolean bookSeat(String flightNum, String seatNum, String passengerName) {
		return false;
	}
	
	

}
