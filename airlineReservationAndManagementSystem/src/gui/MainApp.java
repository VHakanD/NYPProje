package gui;

import servicesAndManagers.*;

import java.io.*;
import java.util.*;

import flightManagement.Staff;
import javafx.application.Application;
import javafx.stage.Stage;
import reservationAndTicketing.Passenger;
import reservationAndTicketing.Reservation;
import javafx.scene.Scene;

public class MainApp extends Application{
	
	private FlightManager flightManager;
    private ReservationManager reservationManager;
    private StaffManager staffManager;
    private List<Passenger> passengerList;
    
    private final String PASSENGER_FILE = "passengers.txt";
    
    private Stage primaryStage;
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
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
        
        flightManager.removeExpiredFlights();
        reservationManager.cleanUpOrphanReservations(); 
    }
    
    private void loadPassengers() {
        File file = new File(PASSENGER_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Passenger p = Passenger.fromFileFormat(line);
                    if (p != null) {
                        passengerList.add(p);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Yolcu dosyası okuma hatası: " + e.getMessage());
        }
    }
    
    public void savePassengerToFile(Passenger p) {
        passengerList.add(p);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PASSENGER_FILE, true))) {
            writer.write(p.toFileFormat());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Yolcu kaydedilemedi: " + e.getMessage());
        }
    }
    
    public void showLoginScreen() {
    	LoginView loginView = new LoginView(this, staffManager);
        Scene scene = new Scene(loginView.getView(), 400, 450);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Giriş Yap");
    }
    
    public void showAdminDashboard(Staff adminStaff) {
    	AdminDashboardView dashboard = new AdminDashboardView(this, adminStaff, flightManager, reservationManager);
        Scene scene = new Scene(dashboard.getView(), 600, 500); // Boyutu biraz artırdık
        primaryStage.setScene(scene);
        primaryStage.setTitle("Yönetici Paneli - Ana Menü");
        primaryStage.centerOnScreen();
    }
    
    public void showFlightScreen(Staff adminStaff) {
        // AdminView'i oluşturuyoruz ama sadece Flight kısmını alacağız
    	AdminView adminView = new AdminView(this, flightManager, reservationManager, staffManager, adminStaff);
    	Scene scene = new Scene(adminView.getFlightView(), 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Uçuş Yönetimi");
        primaryStage.centerOnScreen();
    }

    
    public void showStaffScreen(Staff adminStaff) {
    	AdminView adminView = new AdminView(this, flightManager, reservationManager, staffManager, adminStaff);
    	Scene scene = new Scene(adminView.getStaffView(), 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Personel Yönetimi");
        primaryStage.centerOnScreen();
    }

    public void showUserSearchScreen(Passenger loggedInPassenger) {
        UserSearchView userView = new UserSearchView(this, flightManager, reservationManager, loggedInPassenger);
        Scene scene = new Scene(userView.getView(), 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Yolcu Paneli - " + loggedInPassenger.getName());
        primaryStage.centerOnScreen();
    }
    
    public void showReservationManagementScreen(Passenger loggedInPassenger) {
        ReservationManagementView resView = new ReservationManagementView(this, reservationManager, loggedInPassenger);
        Scene scene = new Scene(resView.getView(), 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Rezervasyonlarım - " + loggedInPassenger.getName());
        primaryStage.centerOnScreen();
    }
    
    public void showSeatChangeScreen(Reservation reservation, Passenger loggedInPassenger) {
        // loggedInPassenger'ı buraya da geçiriyoruz
        SeatChangeView changeView = new SeatChangeView(this, reservation, reservationManager, loggedInPassenger);
        
        Stage stage = new Stage();
        stage.setTitle("Koltuk Değiştirme");
        stage.setScene(new Scene(changeView.getView(), 900, 650));
        
        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        stage.initOwner(primaryStage);
        
        stage.showAndWait(); 
    }
    
    public void showSimulationScreen(Staff adminStaff) {
        // Parametre olarak reservationManager'ı gönderiyoruz
        SimulationView simView = new SimulationView(this, reservationManager, adminStaff);
        
        Scene scene = new Scene(simView.getView(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Multithreading Simülasyonu");
        primaryStage.centerOnScreen();
    }
    
    public void cleanUpSimulationPassengers() {
        // 1. Hafızadaki listeden "SIM_" ile başlayanları sil
        boolean removed = passengerList.removeIf(p -> p.getPassengerID().startsWith("SIM_"));
        
        if (removed) {
            // 2. Dosyayı baştan aşağı yenile (Append modu kapalı)
            rewritePassengerFile();
            System.out.println("Simülasyon yolcuları dosýadan temizlendi.");
        }
    }
    
    private void rewritePassengerFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PASSENGER_FILE, false))) { // false = append kapalı
            for (Passenger p : passengerList) {
                writer.write(p.toFileFormat());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Yolcu dosyası güncellenirken hata: " + e.getMessage());
        }
    }
    
    
    public List<Passenger> getPassengerList() {
        return passengerList;
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
