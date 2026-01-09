package reservationAndTicketing;

import flightManagement.Person;

public class Passenger implements Person{
	private String passengerID;
	private String username;
    private String password;
    private String name;
    private String surname;
    private String contactInfo;
    
    public Passenger(String passengerID, String name, String surname, String contactInfo, String username, String password) {
        this.passengerID = passengerID;
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.contactInfo = contactInfo;
    }
    
    public Passenger(String passengerID, String name, String surname, String contactInfo) {
        this.passengerID = passengerID;
        this.username = "";
        this.password = "";
        this.name = name;
        this.surname = surname;
        this.contactInfo = contactInfo;
    }

	public String getPassengerID() {
		return passengerID;
	}
	
	public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
	
    public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getContactInfo() {
		return contactInfo;
	}
	
    public String toFileFormat() {
        return passengerID + "," + getName() + "," + getSurname() + "," + getContactInfo() + "," + username + "," + password;
    }
    
    
    public static Passenger fromFileFormat(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 6) {
            return new Passenger(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
        } else if (parts.length >= 4) {
            return new Passenger(parts[0], parts[1], parts[2], parts[3]);
        }
        return null;
    }
	
	public boolean isValidNumber() {
		if(contactInfo.length() == 11 && contactInfo.contains("05")) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		String info = "YolcuID: " + this.passengerID + super.toString();
		return info;
	}

}