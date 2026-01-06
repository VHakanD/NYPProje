package flightManagement;

public class Staff extends Person{
	private String role;
	private String username;
    private String password;

    public Staff(String username, String password, String name, String surname, String contactInfo, String role) {
        super(name, surname, contactInfo);
        this.username = username;
        this.password = password;
        this.role = role;
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
	
	public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getUsername() { 
    	return username; 
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return super.toString() + " [Rol: " + role + "]";
    }

}
