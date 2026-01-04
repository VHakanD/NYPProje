package reservationAndTicketing;

import flightManagement.Person;

public class Passenger extends Person{
	//passengerID, name, surname, contactInfo
	private String passengerID;
	
	public Passenger(String passengerID, String name, String surname, String contactInfo) {
		super(name, surname, contactInfo);
		this.passengerID = passengerID;
	}

	public String getPassengerID() {
		return passengerID;
	}
	
	public static Passenger fromFileFormat(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 4) {
            return new Passenger(parts[0], parts[1], parts[2], parts[3]);
        }
        return null;
    }
	
	public boolean isValidNumber() {
		if(super.getContactInfo().length() == 11 && super.getContactInfo().contains("05")) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		String info = "YolcuID: " + this.passengerID + super.toString();
		return info;
	}
}
