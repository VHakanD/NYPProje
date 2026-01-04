package servicesAndManagers;

import java.io.*;
import java.util.*;

import flightManagement.Staff;

public class StaffManager {
	private ArrayList<Staff> staffList;
    private final String FILE_NAME = "staff.txt";

    public StaffManager() {
        this.staffList = new ArrayList<>();
        loadStaff();
    }

    public void addStaff(Staff staffMember) {
        staffList.add(staffMember);
        saveStaff();
    }

    public void deleteStaff(Staff staffMember) {
        staffList.remove(staffMember);
        saveStaff();
    }

    public ArrayList<Staff> getAllStaff() {
        return staffList;
    }

    private void loadStaff() {
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

    private void saveStaff() {
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
