package gui;

import servicesAndManagers.*;

import java.io.*;
import java.util.*;

import javafx.application.Application;
import javafx.stage.Stage;
import reservationAndTicketing.Passenger;
import javafx.scene.Scene;

public class MainApp extends Application{
	
	private FlightManager flightManager;
    private ReservationManager reservationManager;
    private StaffManager staffManager;
    private List<Passenger> passengerList;
    
    private final String PASSENGER_FILE = "passengers.txt";
    
    private Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Havayolu Rezervasyon Sistemi");

        
        initManagers();

        
        showLoginScreen();
        
        this.primaryStage.show();
    }
    
    private void initManagers() {
    	flightManager = new FlightManager();
    	staffManager = new StaffManager();
        passengerList = new ArrayList<>(); 
        loadPassengers();
        
        reservationManager = new ReservationManager(flightManager, passengerList);
        
    }
    
    private void loadPassengers() {
        File file = new File(PASSENGER_FILE);
        
        // Dosya yoksa oluşturmaya çalışma, sadece listeyi boş bırak
        if (!file.exists()) {
            System.out.println("Uyarı: " + PASSENGER_FILE + " bulunamadı. Yeni liste ile başlanıyor.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] data = line.split(",");
                    
                    // Veri bütünlüğü kontrolü (En az 4 parça olmalı)
                    if (data.length >= 4) {
                        String id = data[0].trim();
                        String name = data[1].trim();
                        String surname = data[2].trim();
                        String contact = data[3].trim();
                        
                        Passenger p = new Passenger(id, name, surname, contact);
                        passengerList.add(p);
                    }
                }
            }
            System.out.println("Yolcular yüklendi. Toplam: " + passengerList.size());
            
        } catch (IOException e) {
            System.out.println("Yolcu dosyası okuma hatası: " + e.getMessage());
        }
    }
    
    public void showLoginScreen() {
        LoginView loginView = new LoginView(this);
        Scene scene = new Scene(loginView.getView(), 400, 300);
        primaryStage.setScene(scene);
    }
    
    public void showAdminDashboard(String adminName) {
        AdminDashboardView dashboard = new AdminDashboardView(this, adminName);
        Scene scene = new Scene(dashboard.getView(), 600, 400); // Daha küçük, kompakt bir ekran
        primaryStage.setScene(scene);
        primaryStage.setTitle("Yönetici Paneli - Ana Menü");
        primaryStage.centerOnScreen();
    }
    
    public void showFlightScreen(String adminName) {
        // AdminView'i oluşturuyoruz ama sadece Flight kısmını alacağız
        AdminView adminView = new AdminView(this, flightManager, staffManager, adminName);
        Scene scene = new Scene(adminView.getFlightView(), 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Uçuş Yönetimi");
        primaryStage.centerOnScreen();
    }

    
    public void showStaffScreen(String adminName) {
        AdminView adminView = new AdminView(this, flightManager, staffManager, adminName);
        Scene scene = new Scene(adminView.getStaffView(), 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Personel Yönetimi");
        primaryStage.centerOnScreen();
    }

    public void showUserSearchScreen(String username) {
        UserSearchView userView = new UserSearchView(this, flightManager, reservationManager, username);
        Scene scene = new Scene(userView.getView(), 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Yolcu Uçuş Arama");
        primaryStage.centerOnScreen();
    }

    
    public List<Passenger> getPassengerList() {
        return passengerList;
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
