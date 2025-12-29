package flightManagement;

import java.util.*;

public class Plane {
	//planeID, planeModel, capacity (int), seatMatrix (2D array or Map structure)
	private String planeID;
	private String planeModel;
	private int capacity;
	private HashMap<String, Seat> seatMatrix = new HashMap<>(); 
	//hashmap mi kullansak daha iyi array mi?
	
	public Plane(String planeID, String planeModel, int capacity) {
		super();
		this.planeID = planeID;
		this.planeModel = planeModel;
		this.capacity = capacity;
	}

	public String getPlaneID() {
		return planeID;
	}

	public String getPlaneModel() {
		return planeModel;
	}

	public void setPlaneModel(String planeModel) {
		this.planeModel = planeModel;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public HashMap<String, Seat> getSeatMatrix() {
		return seatMatrix;
	}

	public void setSeatMatrix(HashMap<String, Seat> seatMatrix) {
		this.seatMatrix = seatMatrix;
	}
	
	public void addSeat(Seat seat) {
		this.seatMatrix.put(seat.getSeatNum(), seat);
	}
	
	
	public String toString() {
		String info = "Uçak Numarası: " + this.planeID + "\nUçak Modeli: " + this.planeModel
				+ "\nKapasite: " + this.capacity + "\nUçağın Doluluk Durumu: " + this.seatMatrix.size() + "/" + this.capacity;
		return info;
	}
	
	

}
