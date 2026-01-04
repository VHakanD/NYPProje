package flightManagement;

public class Staff extends Person{
	private String role;
    private String password;

	public Staff(String name, String surname, String contactInfo, String role, String password) {
		super(name, surname, contactInfo);
		this.role = role;
		this.password = password;
	}
	
	public String toFileFormat() {
        return getName() + "," + getSurname() + "," + getContactInfo() + "," + role + "," + password;
    }
	
	public static Staff fromFileFormat(String line) {
        String[] parts = line.split(",");
        if(parts.length >= 5) {
            return new Staff(parts[0], parts[1], parts[2], parts[3], parts[4]);
        }
        return null;
    }
	
	public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return super.toString() + " [Rol: " + role + "]";
    }

}
