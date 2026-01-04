package flightManagement;

public abstract class Person {
	private String name;
	private String surname;
	private String contactInfo;
	
	public Person(String name, String surname, String contactInfo) {
		this.name = name;
		this.surname = surname;
		this.contactInfo = contactInfo;
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
	
	@Override
    public String toString() {
        return "İsim: " + name + " - Soyisim: " + surname + " - Contact Info: " + contactInfo;
    }

}
