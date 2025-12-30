package reservationAndTicketing;

public class Passenger {
	//passengerID, name, surname, contactInfo
	private String passengerID;
	private String name;
	private String surname;
	private String contactInfo;
	
	public Passenger(String passengerID, String name, String surname, String contactInfo) {
		this.passengerID = passengerID;
		this.name = name;
		this.surname = surname;
		this.contactInfo = contactInfo;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public String getPassengerID() {
		return passengerID;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}
	
	public boolean isValidNumber() {
		if(contactInfo.length() == 11 && contactInfo.contains("05")) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		String info = "İsim: " + this.name + " " + this.surname + "YolcuID: " + this.passengerID + "Telefon Numarası: " + this.contactInfo;
		return info;
	}
}
