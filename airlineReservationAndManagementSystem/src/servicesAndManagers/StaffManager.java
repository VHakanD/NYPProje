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

}
