package servicesAndManagers;

import java.io.*;
import java.util.*;

import flightManagement.Staff;

public class StaffManager {
	private ArrayList<Staff> staffList;
    
    private final StaffFileHandler fileHandler = new StaffFileHandler();

    public StaffManager() {
        this.staffList = new ArrayList<>();
        fileHandler.loadStaff();
    }
    
    private class StaffFileHandler {
        private final String FILE_NAME = "staff.txt";

        public void loadStaff() {
            File file = new File(FILE_NAME);
            if (!file.exists()) return;

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Staff s = Staff.fromFileFormat(line);
                    if (s != null) staffList.add(s);
                }
            } catch (IOException e) {
                System.out.println("Personel okuma hatası: " + e.getMessage());
            }
        }

        public void saveStaff() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (Staff s : staffList) {
                    writer.write(s.toFileFormat());
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Personel yazma hatası.");
            }
        }
    }

    public void addStaff(Staff staffMember) {
        staffList.add(staffMember);
        fileHandler.saveStaff();
    }

    public void deleteStaff(Staff staffMember) {
        staffList.remove(staffMember);
        fileHandler.saveStaff();
    }

    public ArrayList<Staff> getAllStaff() {
        return staffList;
    }
    
    public boolean updateStaff(Staff oldStaff, Staff newStaffDetails) {
        int index = staffList.indexOf(oldStaff);
        
        if (index >= 0) {
            staffList.set(index, newStaffDetails);
            
            fileHandler.saveStaff(); 
            return true;
        }
        return false;
    }
    
    public Staff validateLogin(String inputUsername, String inputPassword) {
        Staff foundStaff = null;
        boolean found = false;
        int i = 0;

        
        while (i < staffList.size() && !found) {
            Staff s = staffList.get(i);
            if (s.getUsername().equals(inputUsername) && s.getPassword().equals(inputPassword)) {
                foundStaff = s;
                found = true;
            }
            i++;
        }
        return foundStaff;
    }
    
    public void createBaseAdmins() {
        Staff baseAdmin1 = new Staff("VHakanD", "1234", "Hakan Vehbi", "Demir", "05339874522", "Admin");
        
        Staff baseAdmin2 = new Staff("zeyneppkts", "5678", "Zeynep", "Pektas", "05532897633", "Admin");
        
        Staff baseAdmin3 = new Staff("admin", "admin1", "base", "admin", "05554443355", "Admin");

        addStaffIfNotExist(baseAdmin1);
        addStaffIfNotExist(baseAdmin2);
        addStaffIfNotExist(baseAdmin3);
    }

    private void addStaffIfNotExist(Staff newStaff) {
        boolean exists = false;
        
        for (Staff s : staffList) {
            if (s.getUsername().equals(newStaff.getUsername())) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            staffList.add(newStaff);
            fileHandler.saveStaff();
            System.out.println("Base Yönetici oluşturuldu: " + newStaff.getUsername());
        }
    }

}
