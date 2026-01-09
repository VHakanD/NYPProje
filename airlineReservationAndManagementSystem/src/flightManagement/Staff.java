package flightManagement;

public class Staff implements Person{
	private String role;
	private String username;
    private String password;
    private String name;
    private String surname;
    private String contactInfo;
    
    public Staff(String username, String password, String name, String surname, String contactInfo, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.surname = surname;
        this.contactInfo = contactInfo;
    }
	
    public String toFileFormat() {
        return username + "," + password + "," + getName() + "," + getSurname() + "," + getContactInfo() + "," + role;
    }
	
    public static Staff fromFileFormat(String line) {
        String[] data = line.split(",");
        if (data.length >= 6) {
            return new Staff(data[0], data[1], data[2], data[3], data[4], data[5]);
        }
        return null;
    }
	
	public String getRole() { 
		return role; 
	}
	
    public void setRole(String role) { 
    	this.role = role; 
    }
    
    public String getUsername() { 
    	return username; 
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return super.toString() + " [Rol: " + role + "]";
    }

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getSurname() {
		return surname;
	}

	@Override
	public String getContactInfo() {
		return contactInfo;
	}

}
